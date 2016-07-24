package softparkmulti;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class LoginDialog extends JDialog{
	
	private JLabel labelUsername, labelPassword;
	private JTextField textUsername;
	private JPasswordField textPassword;
	private JButton buttonLogin, buttonCancel;
	private boolean succeeded;

	public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        //
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
 
        cs.fill = GridBagConstraints.HORIZONTAL;
 
        labelUsername = new JLabel("Usuario: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(labelUsername, cs);
 
        textUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(textUsername, cs);
 
        labelPassword = new JLabel("Contraseña: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(labelPassword, cs);
 
        textPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(textPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));
 
        buttonLogin = new JButton("Login");
        
        ButtonListener lForButton = new ButtonListener();
 
        buttonLogin.addActionListener(lForButton);
        buttonLogin.setActionCommand("login");
        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(lForButton);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonCancel);
 
        this.add(panel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.PAGE_END);
 
        this.pack();
        Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int x = (dim.width / 2) - (this.getWidth() / 2);
		int y = (dim.height / 2) - (this.getHeight() / 2);
		this.setLocation(x, y);
        this.setResizable(false);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setTitle("Softpark - Login");
        JRootPane rootPane = this.getRootPane();
		rootPane.setDefaultButton(buttonLogin);
        this.setLocationRelativeTo(parent);
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
	
	protected String getUsername() {
        return textUsername.getText().trim();
    }
 
    private String getPassword() {
        return new String(textPassword.getPassword());
    }
 
    protected boolean isSucceeded() {
        return succeeded;
    }
	
}
