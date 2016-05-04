import java.io.PrintWriter;

/**
 * This is a class that is used by the FalconChatServer to represent one of the people talking
 * in this chat.
 * @author harlan.howe
 *
 */
public class Chatterer {

	private String myName;
	private int myID;
	private PrintWriter myPrintWriter;
	
	public Chatterer(String name, int id, PrintWriter pw) 
	{
		myName = name;
		myID = id;
		myPrintWriter = pw;
	}
	
	public String getName() {return myName;}
	public int getID() {return myID;}
	
	public void sendMessage(String message)
	{
		myPrintWriter.println(message);
		myPrintWriter.flush();
	}
	
	public String toString()
	{
		return myID+": "+myName;
	}
}
