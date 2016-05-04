import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class FalconChatServer extends TimerTask
{
	private int nextAvailableID; // each chatterer/client will be assigned an id number. 
								// This is the number to be assigned next.
	
	private ServerSocket mySocket; // this server's connection to the outside world.
	private Map<Integer, Chatterer> chatterers; // the various people who will be sending and receiving "chits."
	
	public static final String[] messageTypes = {"NEW_CHATTERER","MESSAGE","CHATTERER_LEAVING"}; // String constants that represent the types of messages that may be sent back and forth.
	
	public FalconChatServer() {
		super();
		System.out.println("Initializing.");
		nextAvailableID = 0;
		Timer t = new Timer();
		t.scheduleAtFixedRate(this, 0, 20);  // this is the TimerTask class whose run()
											// method will be called. 0 is the delay before
											// the method is called first; 20 is the delay
											// (in ms) between calls of run().
		chatterers = new HashMap<Integer, Chatterer>();
		setupNetworking();
	}
	/**
	 * listens for clients that would like to join this chat, and sets them up when it finds them.
	 */
	public void setupNetworking()
	{
		try
		{
			mySocket = new ServerSocket(5000);
			while(true)
			{
				System.out.println("Waiting for Client");
				// Wait for a connection request from a client. Don't advance to the next line until you do.
				Socket clientSocket = mySocket.accept(); 
				
				// ask the socket for a writer that will allow us to send stuff to this client.
				PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
				
				// build a ClientReader that will start to constantly listen for message from this client.
				ClientReader cr = new ClientReader(clientSocket, pw); // note: this class is written later in this file.
				
				// build a Chatterer instance that will represent the person on the other end of this connection, one
				//    to whom we can send messages.
				Chatterer nextChatterer = new Chatterer(cr.getName(),nextAvailableID, pw); 
				
				// add the new chatterer to the list of all chatterers.
				chatterers.put(nextAvailableID, nextChatterer);
				
				// tell everybody about this new chatterer.
				broadcast(0,new String[]{""+nextAvailableID, cr.getName()});
				
				nextAvailableID++;
			}
		}
		catch (IOException e) // in case there is a problem with the connection...
        {
            e.printStackTrace();
        }	
	}

	/**
	 * implementation of TimerTask's abstract run() method... this method will be called
	 * over and over again on a regular basis.
	 */
	public void run()
	{
		// in this case, run does nothing. If this were a more interactive program, such as
		// a game, this method would contain the game loop that handled all the motion, interactions
		// and rules...
		// I could have left it out of this program, but I think it is important to see it in action.
		
		; // do nothing.
		
		
	}

	/**
	 * send the given message to every chatterer in the list.
	 * @param messageType - which type of message to send
	 * @param params - an array of strings to send, tab-delimited.
	 */
	public void broadcast(int messageType, String[] params)
	{
		System.out.println("Num chatterers: "+chatterers.size());
		String message = messageTypes[messageType];
		for (String s: params)
		{
			message += "\t"+s;
		}
		Set<Integer> allIDs = chatterers.keySet();
		for (Integer id: allIDs)
		{
			chatterers.get(id).sendMessage(message);
		}
	}
	
	/**
	 * This server has just received a message from a client. Deal with it.
	 * @param message
	 * @param chattererID
	 */
	public void handleMessage(String message, int chattererID)
	{
		String[] messageComponents = message.split("\t");
		System.out.println("Received message: "+message+"From:" + chatterers.get(chattererID).getName());
		
		// In this program, the clients only ever send one type of message - a piece of text they want everybody to read.
		if (messageComponents[0].equals(messageTypes[1]))
		{
			String outgoingMessage = "";
			for (int i=1; i<messageComponents.length; i++)
			{	outgoingMessage += messageComponents[i];
				if (i < messageComponents.length-1)
					outgoingMessage+= " ";
			}
			broadcast(1, new String[] {chatterers.get(chattererID).getName(),outgoingMessage});
		}
	}

	/**
	 * A client needs to be removed from the list of chatterers, and we'll tell all the others
	 * he/she has left.
	 * @param whichID
	 */
	public void disconnectClient(int whichID)
	{
		System.out.println("Disconnecting "+whichID);
		System.out.println("Keys: "+chatterers.keySet());
		broadcast(2, new String[] {chatterers.get(whichID).getName()});  // 2 is the code number for "LEAVING"
		chatterers.remove(whichID);
		
	}
	
	/**
	 * This is an "interior class." It is just like a normal class, except, only FalconChatServer
	 * knows about this class, and it has access to all the variables in the FalconChatServer
	 * instance that created the instance of this class.
	 * 
	 * In this case, ClientReader maintains a thread - a separate, simultaneous process - that
	 * constantly listens to the client to see whether a new message has come in and passes any
	 * that it receives up to FalconChatServer's handleMessage().
	 * @author harlan.howe
	 *
	 */
	private class ClientReader implements Runnable
	{
		private Socket mySocket;
		private PrintWriter myPrintWriter;
		private Scanner myScanner;
		
		private String myName;
		private int myID;
		
		public ClientReader(Socket s, PrintWriter pw)
		{
			mySocket = s;
			myPrintWriter = pw;
			try
			{
				myScanner = new Scanner(mySocket.getInputStream());
				myName = myScanner.nextLine(); // assumes the first thing sent by a new client is its name...
				
				myID = nextAvailableID; // (from the outer class, which we have access to.)
				
				myPrintWriter.println(myID); // send back the ID we assigned it. 
											// (A bit silly for this chat program, but might be handy later.)
				
				myPrintWriter.flush(); // actually sends the message.
				
				new Thread(this).start(); // begin running the thread - it looks for a run() method in this class.
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		public String getName() { return myName; }
	
		@Override
		/**
		 * our implementation of Runnable's abstract run() method. This keeps checking for
		 * messages from this client's socket, via the scanner.
		 */
		public void run()
		{
			try
			{
				while(true)
				{
					handleMessage(myScanner.nextLine(), myID); // wait for a message, and deal with it.
				}
			}
			catch(NoSuchElementException nse) // this client has dropped his/her connection.
			{
				disconnectClient(myID);
			}
		}
	}
	
}
