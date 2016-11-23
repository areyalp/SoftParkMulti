package softparkmulti;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
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
	private JPanel titlePanel;
	private boolean succeeded;

	public LoginDialog(Frame parent) {
		
        super(parent, "Login", true);
        //changes on the layout are beyond this line
        //box layout 

        ImageIcon image;
		JLabel background;
        JPanel basic = new JPanel(new BorderLayout(0, 0));       
        
        JPanel leftPanel = new JPanel(new BorderLayout(0, 0));
        leftPanel.setMaximumSize(new Dimension(550, 550));
        		
		image = new ImageIcon("resources/estacionamiento.jpg");
		background = new JLabel(image);
		add(background);
		
        background.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));        
		leftPanel.add(background, BorderLayout.WEST);              
        
        basic.add(leftPanel);                     
        this.add(basic, BorderLayout.WEST);
        
        JPanel container = new JPanel(new BorderLayout(0,0));  
        this.add(container, BorderLayout.EAST);
		
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
 
        textUsername = new JTextField(15);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        loginPanel.add(textUsername, cs);
 
        labelPassword = new JLabel("Contraseña: ");
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        loginPanel.add(labelPassword, cs);
 
        textPassword = new JPasswordField(15);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        loginPanel.add(textPassword, cs);
        
        buttonLogin = new JButton("Login");
      
        ButtonListener lForButton = new ButtonListener();

        buttonLogin.addActionListener(lForButton);
        buttonLogin.setActionCommand("login");
        buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(lForButton);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonCancel);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        loginPanel.add(buttonPanel, cs);
        
        this.add(loginPanel, BorderLayout.SOUTH);        
        container.add(loginPanel);        
        this.add(container, BorderLayout.EAST);
        
        setTitle("Softpark - Login");
        setSize(new Dimension(800, 550));
        setResizable(false);
        Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int x = (dim.width / 2) - (this.getWidth() / 2);
		int y = (dim.height / 2) - (this.getHeight() / 2);
		setLocation(x, y);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
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
