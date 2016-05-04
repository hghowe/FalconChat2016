import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FalconChatFrame extends JFrame implements ActionListener {

	private JTextArea myPanel;
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
		myPanel = new JTextArea();
		myTextField = new JTextField();
		JScrollPane sp = new JScrollPane();
		sp.add(myPanel);
		getContentPane().add(sp, BorderLayout.CENTER);
		getContentPane().add(myTextField, BorderLayout.SOUTH);
		myTextField.addActionListener(this);
	}

	
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == myTextField)
		{
			System.out.println(myTextField.getText());
			myTextField.setText("");
		}
	}

}
