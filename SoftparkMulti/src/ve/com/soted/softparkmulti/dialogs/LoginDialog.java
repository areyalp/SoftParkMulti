package ve.com.soted.softparkmulti.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog{
	
	private JLabel labelUsername, labelPassword,labelName;
	private JTextField textUsername;
	private JPasswordField textPassword;
	private JButton buttonLogin, buttonCancel;
	private boolean succeeded;

	public LoginDialog(Frame parent) {
        super(parent, true);
		
        JPanel leftWrapper = new JPanel(new BorderLayout(0, 0));
        
        JPanel leftPanel = new JPanel(new BorderLayout(0, 0));
        leftPanel.setMaximumSize(new Dimension(550, 550));
        		
        ImageIcon image = new ImageIcon("resources/estacionamiento.jpg");
        JLabel background = new JLabel(image);
		
        background.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));        
		leftPanel.add(background, BorderLayout.WEST);              
        
		leftWrapper.add(leftPanel);
        this.add(leftWrapper, BorderLayout.WEST);
        
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        
        cs.fill = GridBagConstraints.HORIZONTAL;            
        cs.insets = new Insets(5,5,5,5);
        
        labelName = new JLabel("SoftPark");
        labelName.setFont(new Font("Serif", Font.BOLD, 30));
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 1;
        loginPanel.add(labelName, cs);
        
        labelUsername = new JLabel("Usuario: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        loginPanel.add(labelUsername, cs);
        
        textUsername = new JTextField(8);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        loginPanel.add(textUsername, cs);
        
        labelPassword = new JLabel("Contraseña: ");
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        loginPanel.add(labelPassword, cs);
        
        textPassword = new JPasswordField(8);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        loginPanel.add(textPassword, cs);
        
        ButtonListener lForButton = new ButtonListener();
        
        buttonLogin = new JButton("Login");
        buttonLogin.setActionCommand("login");
        buttonLogin.addActionListener(lForButton);
        
        buttonCancel = new JButton("Cancel");
        buttonCancel.setActionCommand("cancel");
        buttonCancel.addActionListener(lForButton);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonCancel);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        loginPanel.add(buttonPanel, cs);
        
        JPanel rightWrapper = new JPanel(new BorderLayout(0, 0));
        
        rightWrapper.add(loginPanel);
        this.add(rightWrapper, BorderLayout.EAST);
        
        this.setTitle("Softpark - Login");
        this.setSize(new Dimension(850, 550));
        this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.getRootPane().setDefaultButton(buttonLogin);
		this.setLocationRelativeTo(null);
    }
	
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equalsIgnoreCase("login")) {
				if (Login.authenticate(getUsername(), getPassword())) {
                    succeeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this,
                            "Invalid username or password",
                            "Login",
                            JOptionPane.ERROR_MESSAGE);
                    // reset username and password
                    textUsername.setText("");
                    textPassword.setText("");
                    succeeded = false;
 
                }
			}
			if(e.getActionCommand().equalsIgnoreCase("cancel")) {
				dispose();
			}
		}
		
	}
	
	public String getUsername() {
        return textUsername.getText().trim();
    }
 
    private String getPassword() {
        return new String(textPassword.getPassword());
    }
 
    public boolean isSucceeded() {
        return succeeded;
    }
	
}
