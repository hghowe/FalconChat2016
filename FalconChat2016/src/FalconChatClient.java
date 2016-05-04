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
	private final String IP_ADDRESS = "172.16.89.200"; // change this!!!
	public FalconChatClient(String name, FalconChatFrame frame) 
	{
		myName = name;
		myFrame = frame;
		setupConnection();
	}
	/**
	 * Asks the user for his/her name & establishes the connection with the server.
	 */
	public void setupConnection()
	{
		try
		{
			mySocket = new Socket(IP_ADDRESS,5000); // communicating with the server via channel 5000.
			mySocketScanner = new Scanner(mySocket.getInputStream());
			mySocketWriter = new PrintWriter(mySocket.getOutputStream());
			Thread readerThread = new Thread(new IncomingReader()); // this class gets written later in this file.
			readerThread.start();
			
			mySocketWriter.println(myName); // add my name to the things to send to the server
			mySocketWriter.flush();         // ...and send it.
			// Note: the server is expecting you to immediately send your name.
		}
		catch (IOException e)
		{
			System.out.println("I couldn't connect.");
			e.printStackTrace();
		}
	}
	/**
	 * we have just received a string from the server that contains a command code
	 * and other information, tab-delimeted, which we must act upon.
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
	
	public class IncomingReader implements Runnable
	{
		public void run()
		{
			try
			{
				// the first thing we receive from the server is this client's assigned ID.
				myID = Integer.parseInt(mySocketScanner.nextLine());
				System.out.println("I have been assigned ID# "+myID);
				while (true)
				{
					// wait for a message from the server and have the FalconChatClient act upon it.
					parseMessage(mySocketScanner.nextLine());
				}
			}
			catch (NoSuchElementException nsee)
			{
				JOptionPane.showConfirmDialog(null, "Lost connection.");
				System.exit(1);
			}
		}
	}
}
