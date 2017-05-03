import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class FalconChatClient {

	private String myName;
	private int myID;
	private FalconChatFrame myFrame;
	
	private Socket mySocket;
	private Scanner mySocketScanner;
	private PrintWriter mySocketWriter;
	private final String IP_ADDRESS = "10.1.27.175"; // change this as needed!!!
	public FalconChatClient(String name, FalconChatFrame frame) 
	{
		myName = name;
		myFrame = frame;
		setupConnection();
	}
	/**
	 * Establishes the connection with the server.
	 */
	public void setupConnection()
	{
		myFrame.displayMessage("Awaiting connection to server.");
		try
		{
			mySocket = new Socket(IP_ADDRESS,5000); // create a 2-way communication socket to the server via channel 5000.
			
			mySocketScanner = new Scanner(mySocket.getInputStream()); // this is what this class will use to read from the
																	  //   channel, like reading from the keyboard or a file.
			
			mySocketWriter = new PrintWriter(mySocket.getOutputStream()); // and this is what this class will use to send messages out.
			
			Thread readerThread = new Thread(new IncomingReader()); // this IncomingReader class definition is later in this file.
			readerThread.start();
			
			// Note: the server is expecting you to immediately send your name.
			
			mySocketWriter.println(myName); // add my name to the things to send to the server
			mySocketWriter.flush();         // ...and send it.
			myFrame.displayMessage("Connected.");
		}
		catch (IOException e)
		{
			myFrame.displayMessage("I couldn't connect.");
			e.printStackTrace();
		}
	}
	/**
	 * we have just received a string from the server that contains a command code
	 * and other information, tab-delimited, which we must act upon.
	 * @param message
	 */
	public void parseMessage(String message)
	{
		String[] messageSequence = message.split("\t");
		if (messageSequence[0].equals(FalconChatServer.messageTypes[0]))
			myFrame.displayMessage(messageSequence[2]+" just joined the conversation.");
		
		else if (messageSequence[0].equals(FalconChatServer.messageTypes[1]))
			myFrame.displayMessage(messageSequence[1]+": "+messageSequence[2]);
		
		else if (messageSequence[0].equals(FalconChatServer.messageTypes[2]))
			myFrame.displayMessage(messageSequence[1]+" just left the conversation.");
	}
	/**
	 * the user would like to send this "chit" string out to the server to rebroadcast!
	 * @param chit
	 */
	public void sendChatString(String chit)
	{
		// add a code that this is a message to post and the actual message to the outgoing
		// stream of info to the server.
		mySocketWriter.println(FalconChatServer.messageTypes[1]+"\t"+chit);
		// and send it:
		mySocketWriter.flush();
	}
	
	/**
	 * This is an "internal class" - only the FalconChatClient really knows about it, and it has access 
	 * to all of FCC's internal variables and methods. In particular, this is a Runnable object, which means
	 * that it will be what a thread runs - so that the computer can multitask - this is one of the 
	 * simultaneous mini-programs that will be running. 
	 * This Runnable waits to hear messages from the server, and tells the FCC to 
	 * @author harlan.howe
	 *
	 */
	public class IncomingReader implements Runnable
	{
		public void run()
		{
			try
			{
				// the first thing we receive from the server is this client's assigned ID. This is a one-time thing.
				myID = Integer.parseInt(mySocketScanner.nextLine());
				System.out.println("I have been assigned ID# "+myID);
				while (true)
				{
					// wait for a message from the server and have the FalconChatClient act upon it. The program hangs
					//    here until it gets a message, sends it to parseMessage() and then loops to wait again.
					parseMessage(mySocketScanner.nextLine());
				}
			}
			catch (NoSuchElementException nseExcp)
			{
				JOptionPane.showConfirmDialog(null, "Lost connection.");
				System.exit(1);
			}
		}
	}
}
