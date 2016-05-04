import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FalconChatFrame extends JFrame implements ActionListener {

	private JTextArea myPanel;
	private JTextField myTextField;
	private FalconChatClient myConnection;
	
	public FalconChatFrame() {
		super("Falcon Chat!");
		setSize(400,600);
		setResizable(true);
		setupLayout();
		myConnection = new FalconChatClient(requestName(), this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	/**
	 * sets up the GUI
	 */
	private void setupLayout()
	{
		getContentPane().setLayout(new BorderLayout());
		myPanel = new JTextArea();
		myTextField = new JTextField();
		JScrollPane sp = new JScrollPane(myPanel);

		getContentPane().add(sp, BorderLayout.CENTER);
		getContentPane().add(myTextField, BorderLayout.SOUTH);
		myTextField.addActionListener(this);
	}
	
	public void displayMessage(String message)
	{
		myPanel.setText(myPanel.getText()+"\n"+message);
		
	}
	
	/**
	 * asks the user for his/her name. 
	 * @return a non-empty String.
	 */
	public String requestName()
	{
		String name;
		do
		{
		   name = JOptionPane.showInputDialog("Please enter your name.");
		} while (name.equals(""));
		return name;
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == myTextField)
		{
//			System.out.println(myTextField.getText());
			myConnection.sendChatString(myTextField.getText());
			myTextField.setText("");
			
		}
	}

}
