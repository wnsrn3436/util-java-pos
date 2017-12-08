import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class MessageDialog extends JDialog
{
	public Button btnOK;
	
	MessageDialog(JFrame parent, String title, boolean mode, String msg)
	{
		super(parent, title, mode);
		setLocationRelativeTo(parent);
		
		JPanel msgPanel = new JPanel();
		add(msgPanel, BorderLayout.CENTER);
		
		JLabel label = new JLabel(msg);
		msgPanel.add(label);
		
		JPanel optionPanel = new JPanel();  
		add(optionPanel, BorderLayout.SOUTH);
		
		btnOK = new Button("확인");
		optionPanel.add(btnOK);
		
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dispose();     
			}
		});
		
		pack();
	}
} 