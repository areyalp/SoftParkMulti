package softparkmulti;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

@SuppressWarnings("serial")
public class LoginView extends JFrame{
	
	private JLabel labelUsername, labelPassword, labelLogin;
	private JTextField textUsername;
	private JPasswordField textPassword;
	private JButton buttonLogin;
	
	private String username;
	private int userId;
	
	private Station stationInfo;
	private int stationId;
	private int retries = 0;
	
	public LoginView(int stationId){
		this.stationId = stationId;
		
		stationInfo = Station.getStationInfo(stationId);
		
		if(stationInfo == null) {
			JOptionPane.showMessageDialog(null, "Estacion invalida", "Estacion invalida, contacte al administrador", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		this.setSize(300, 150);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int x = (dim.width / 2) - (this.getWidth() / 2);
		int y = (dim.height / 2) - (this.getHeight() / 2);
		this.setLocation(x, y);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Softpark - (" + stationInfo.getName() + ")");
		JPanel thePanel = new JPanel(new SpringLayout());
		labelUsername = new JLabel("Usuario", JLabel.TRAILING);
		thePanel.add(labelUsername);
		textUsername = new JTextField(10);
		labelUsername.setLabelFor(textUsername);
		thePanel.add(textUsername);
		labelPassword = new JLabel("Contraseña", JLabel.TRAILING);
		thePanel.add(labelPassword);
		textPassword = new JPasswordField(10);
		textPassword.setEchoChar('*');
		labelPassword.setLabelFor(textPassword);
		thePanel.add(textPassword);
		labelLogin = new JLabel("");
		thePanel.add(labelLogin);
		labelLogin.setLabelFor(buttonLogin);
		buttonLogin = new JButton("Login");
		thePanel.add(buttonLogin);
		SpringUtilities.makeCompactGrid(thePanel,
										3, 2,
										6, 6,
										6, 6);
		ButtonListener lForButton = new ButtonListener();
		buttonLogin.addActionListener(lForButton);
		JRootPane rootPane = this.getRootPane();
		rootPane.setDefaultButton(buttonLogin);
		this.add(thePanel);
		//this.setVisible(true);
		
	}
	
	protected static int Login(String username, String plainPassword){
		boolean isPasswordOk = false;
		int userId = 0;
		ResultSet rows;
		try{
			Db db = new Db();
			rows = db.select("SELECT Id, Password FROM Users WHERE Login='"+ username +"';");
			if(rows.next()){
				String encryptedPassword = rows.getString("Password");
				isPasswordOk = PasswordEncryptor.checkPassword(plainPassword, encryptedPassword);
				if(isPasswordOk){
					userId = rows.getInt("Id");
				}
			}
		} //END OF try
		catch(Exception ex){
			ex.printStackTrace();
		} //END OF catch
		return userId;
	} //END OF login method

	
	private class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			if(ev.getSource()==buttonLogin){
				username = textUsername.getText();
				char[] input = textPassword.getPassword();
				String plainPassword = "";
				for(int i=0; i < input.length; i++){
					plainPassword += input[i];
				}
				
				userId = LoginView.Login(username, plainPassword);
				if(userId > 0){
					dispose();
					new SoftParkMultiView(stationId);
				}else{
					retries++;
					
					if(retries < 3) {
						JOptionPane.showMessageDialog(null,"Combinacion Usuario/Password invalida","Datos invalidos",JOptionPane.WARNING_MESSAGE);
					}else{
						JOptionPane.showMessageDialog(null,"Maximo de intentos alcanzado","Maximo de Intentos",JOptionPane.WARNING_MESSAGE);
						System.exit(0);
					}
					textUsername.requestFocusInWindow();
					textUsername.selectAll();
				};
			} //END OF if
		} //END OF actionPerformed
	} //END OF class ButtonListener
} //END OF class LoginView