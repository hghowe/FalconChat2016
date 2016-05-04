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
	private int nextAvailableID;
	private ServerSocket mySocket;
	private Map<Integer, Chatterer> chatterers;
	private Map<Integer, String> clientNames;
	private final String[] messageTypes = {"NEW","MESSAGE","LEAVING"};
	public FalconChatServer() {
		super();
		nextAvailableID = 0;
		Timer t = new Timer();
		t.scheduleAtFixedRate(this, 0, 20);  // this is the TimerTask class whose run()
											// method will be called. 0 is the delay before
											// the method is called first; 20 is the delay
											// (in ms) between calls of run().
		chatterers = new HashMap<Integer, Chatterer>();
		setupNetworking();
	}
	
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
				
				// tell everybody about this new chatterer.
				broadcast(0,new String[]{""+nextAvailableID, cr.getName()});
				
				// add the new chatterer to the list of all chatterers.
				chatterers.put(nextAvailableID, nextChatterer);
				
				nextAvailableID++;
			}
		}
		catch (IOException e) // in case there is a problem with the connection...
        {
            e.printStackTrace();
        }	
	}
	
	public void run()
	{
		// in this case, run does nothing. If this were a more interactive program, such as
		// a game, this method would contain the game loop that handled all the motion, interactions
		// and rules...
		// I could have left it out of this program, but I think it is important to see it in action.
		
		// so the code below is just to make sure _something_ happens in this run method so that the
		// cycle doesn't happen too fast....
		
		try
		{
			Thread.sleep(1); 
		}
		catch (InterruptedException ie)
		{
			System.out.println(ie);
		}
		
		
	}

	public void broadcast(int messageType, String[] params)
	{
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
	
	public void handleMessage(String message, int chattererID)
	{
		String[] messageComponents = message.split("\t");
		System.out.println("Received message: "+message+"From:" + chatterers.get(chattererID).getName());
		String outgoingMessage = "";
		for (int i=1; i<messageComponents.length; i++)
		{	outgoingMessage += messageComponents[i];
			if (i < messageComponents.length-1)
				outgoingMessage+= " ";
		}
		broadcast(1, new String[] {chatterers.get(chattererID).getName(),outgoingMessage});
	}
	
	public void disconnectClient(int whichID)
	{
		chatterers.remove(whichID);
		broadcast(2, new String[] {""+whichID});  // 2 is the code number for "LEAVING"
	}
	
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
				myPrintWriter.println(myID);
				myPrintWriter.flush();
				new Thread(this).start();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		public String getName() { return myName; }
	
		@Override
		public void run()
		{
			try
			{
				while(true)
				{
					handleMessage(myScanner.nextLine(), myID);
				}
			}
			catch(NoSuchElementException nse)
			{
				disconnectClient(myID);
			}
		}
	}
	
}
