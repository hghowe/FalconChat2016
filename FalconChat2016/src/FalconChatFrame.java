import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class FalconChatFrame extends JFrame {

	private JPanel myPanel;
	private JTextField myTextField;
	
	public FalconChatFrame() {
		super("Falcon Chat!");
		setSize(400,600);
		setResizable(true);
		setupLayout();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void setupLayout()
	{
		getContentPane().setLayout(new BorderLayout());
		myPanel = new JPanel();
		myTextField = new JTextField();
		JScrollPane sp = new JScrollPane();
		sp.add(myPanel);
		getContentPane().add(sp, BorderLayout.CENTER);
		getContentPane().add(myTextField, BorderLayout.SOUTH);
	}


}
