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
		System.out.println("I'm making a FalconChatFrame.");
		setSize(400,600);
		setResizable(true);
		setupLayout();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		myConnection = new FalconChatClient(requestName(), this);
		
	}
	/**
	 * sets up the GUI - a multiline text area embedded in a scroll pane, and a text field below it.
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
		myTextField.requestFocus();
	}
	
	/**
	 * adds the given message to the text in the textview on a new line.
	 * @param message
	 */
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
	
	/**
	 * respond to user pressing return while typing in the text field - sends off a message!
	 */
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
