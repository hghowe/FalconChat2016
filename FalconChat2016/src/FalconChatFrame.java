import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class FalconChatFrame extends JFrame {

	public FalconChatFrame() {
		super("Falcon Chat!");
		setSize(400,600);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}


}
