import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FalconChatServer extends TimerTask
{
	private int nextAvailableID;
	private ServerSocket mySocket;
	private Map<Integer, FalconChatClient> clients;
	
	public FalconChatServer() {
		super();
		nextAvailableID = 0;
		Timer t = new Timer();
		t.scheduleAtFixedRate(this, 0, 20);  // this is the TimerTask class whose run()
											// method will be called. 0 is the delay before
											// the method is called first; 20 is the delay
											// (in ms) between calls of run().
		clients = new HashMap<Integer, FalconChatClient>();
	}

	
	public void run()
	{
		
		
	}

}
