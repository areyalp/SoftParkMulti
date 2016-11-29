package softparkmulti;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import jssc.SerialPortList;
import tfhka.*;
import tfhka.ve.*;

@SuppressWarnings("serial")
public class SoftParkMultiView extends JFrame {

	final private Tfhka fiscalPrinter;
	private boolean isPrinterConnected;
	
	private int summaryId = 0;
	private boolean summaryHasInvoice = false;
	private int invoiceCount = 0;	
	private User user;
	private int userId;
	private int stationId;
	private ArrayList<Summary> summaries;
	private ArrayList<Station> stationsWithSummary;
	private ArrayList<Transaction> transactionsType;
	private ArrayList<Transaction> transactions;
	private ArrayList<PayType> payTypes;
	
	private Station stationInfo;
	private String stationMode;

	private JMenuBar menuBar;
	private JMenu mainMenu;
	private JMenuItem menu;
	private JMenuItem menuItem;

	private static JMenuItem menuItemConnect;
	private static JMenuItem menuItemDisconnect;

	private JCheckBoxMenuItem cbMenuItemToolbar, cbMenuItemStatusbar;

	private JButton toolBarButtonCollect, toolBarButtonCutoff, toolBarButtonManualTicket, toolBarButtonLostTicket;
	
	private JButton buttonValetInvoice, buttonValetLostTicket;	
	private JButton buttonAccept, buttonCancel;

	private JPanel theToolBarPanel;
	private JPanel theStatusBarPanel;
	private JPanel personalPanel, companyPanel;

	private JCheckBox checkBillsName;
	private JToolBar toolBar;
	
	private JComboBox comboCompany, comboShop,comboColor, comboModel, comboState, comboBrand;

	private static JLabel labelStatus;

	private String activePort;

	private JTextField textTicket, textPlate, textOwnerId, textOwnerName, textOwnerLastName, textDescription;
	private JLabel labelPrice;
	
	private JTree tree;	
	private JButton buttonReloadReports;
	private JComboBox<String> comboCountry, comboDirectionState;
	

	public SoftParkMultiView(int stationId) {
		
		fiscalPrinter = new tfhka.ve.Tfhka();
		
		LoginDialog loginDialog = new LoginDialog(this);
		
		loginDialog.setVisible(true);
		
		if(!loginDialog.isSucceeded()) {
			System.exit(0);
		}
		
		userId = Db.getUserId(loginDialog.getUsername());
		
		Db db = new Db();
		
		user = db.loadUserInfo(userId);
		
		if(user == null) {
			JOptionPane.showMessageDialog(null, "Usuario invalido", "Usuario invalido", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		this.stationId = stationId;
		
		stationInfo = Station.getStationInfo(stationId);
		
		transactionsType = Db.loadTransactionTypes();
		
		transactions = new ArrayList<Transaction>();
		
		payTypes = Db.loadPayTypes();
		
		summaryId = Db.getSummaryId(userId,stationId);
		
		if(summaryId > 0) {
			invoiceCount = db.countSummaryInvoices(summaryId);
		}
		
		if(invoiceCount > 0) {
			summaryHasInvoice = true;
		}
		
		summaries = Db.loadSummaries();

		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(800, 600));

		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();

		int x = (dim.width / 2) - (this.getWidth() / 2);
		int y = (dim.height / 2) - (this.getHeight() / 2);

		this.setLocation(x, y);

		this.setTitle("Softpark - (" + user.getLogin() + ") " + user.getName());

		this.setLayout(new BorderLayout(20, 20));

		// Create the box panel to wrap the menu and the toolbar
		JPanel toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.PAGE_AXIS));

		// Create the menu bar.
		this.setJMenuBar(createMenu());
		// Create the tool bar.
		toolBarPanel.add(createToolBar());

		JTabbedPane tabbedPane = new JTabbedPane();

		// Add a tab
		if(stationInfo.getType() == 1){
			tabbedPane.addTab("Sistema de Cobro", createCashierTab());
		}else if(stationInfo.getType() == 4){
			stationMode = "Valet";
			tabbedPane.addTab("Valet Parking", createValetTab());
		}

		this.add(toolBarPanel, BorderLayout.NORTH);

		this.add(tabbedPane, BorderLayout.CENTER);

		this.add(createStatusBar(), BorderLayout.SOUTH);

		cbMenuItemToolbar.setSelected(true);

		cbMenuItemStatusbar.setSelected(true);
		
		this.add(createReportTree(), BorderLayout.EAST);

		this.setVisible(true);

	}
	
	private JPanel createReportTree() {
		JPanel thePanel = new JPanel();
		thePanel.setLayout(new BorderLayout());
		thePanel.setMinimumSize(new Dimension(200, 200));
		thePanel.setPreferredSize(new Dimension(200, 200));
		
		ButtonListener lForButton = new ButtonListener();
		
		buttonReloadReports = new JButton("Recargar Cierres");
		buttonReloadReports.addActionListener(lForButton);
		buttonReloadReports.setActionCommand("reload-reports");
		thePanel.add(buttonReloadReports, BorderLayout.NORTH);
		
		stationsWithSummary = Db.getStationsWithSummary();
		summaries = Db.loadSummaries();
		
		tree = new JTree(new TreeDataModel(stationsWithSummary, summaries));
		
		MouseClickListener lForMouseClick = new MouseClickListener();
		
		tree.addMouseListener(lForMouseClick);
		
		JScrollPane treeView = new JScrollPane(tree);
		
		thePanel.add(treeView, BorderLayout.CENTER);
		return thePanel;
	}

	private JPanel createValetTab() {
		JPanel theTab = new JPanel();
		
		theTab.setLayout(new GridLayout(0, 2));
		
		JPanel thePanel = new JPanel();
		
		GroupLayout layout = new GroupLayout(thePanel);
		thePanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		ButtonListener lForButton = new ButtonListener();
		
		buttonValetInvoice = new JButton(new ImageIcon("resources/valet-invoice.png"));
		buttonValetInvoice.setActionCommand("valet-invoice");
		buttonValetInvoice.addActionListener(lForButton);
		buttonValetInvoice.addKeyListener(lForButton);
		thePanel.add(buttonValetInvoice);
		
		buttonValetLostTicket = new JButton(new ImageIcon("resources/valet-lost-ticket.png"));
		buttonValetLostTicket.setActionCommand("valet-lost");
		buttonValetLostTicket.addActionListener(lForButton);
		buttonValetLostTicket.addKeyListener(lForButton);
		thePanel.add(buttonValetLostTicket);
		
		JPanel subPanel = new JPanel();
		
		JLabel labelTicket = new JLabel("Ticket No.:");
		subPanel.add(labelTicket);
		textTicket = new JTextField(20);
		subPanel.add(textTicket);
		
		thePanel.add(subPanel);
		
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(buttonValetInvoice)
						.addComponent(buttonValetLostTicket)
						.addComponent(subPanel)
						)
				);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				//.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(buttonValetInvoice)
						.addComponent(buttonValetLostTicket)
						.addComponent(subPanel)
						//)
				);
		
		theTab.add(thePanel);
		
		theTab.add(createValetCashier());
		
		return theTab;
	}

	private JPanel createCashierTab() {

		JPanel theTab = new JPanel();

		theTab.setLayout(new GridLayout(0, 3));

		theTab.add(createSubPanelCharge());
		theTab.add(createSubPanelMiddle());
		theTab.add(createSubPanelRight());

		return theTab;
	}
	
	private JPanel createSubPanelRight() {
		// TODO Auto-generated method stub
		JPanel wrapContainerPanel = new JPanel();
		
		wrapContainerPanel.setLayout(new BoxLayout(wrapContainerPanel, BoxLayout.X_AXIS));
		
		JPanel container = new JPanel(new BorderLayout());
		
		JPanel carsDataPanel = new JPanel();
		
		GroupLayout layout = new GroupLayout(carsDataPanel);
		carsDataPanel.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		//added a title to the ticket data
		JLabel labelTitle = new JLabel("Datos de Vehículo");
		labelTitle.setFont(new Font(null, Font.BOLD, 20));
		carsDataPanel.add(labelTitle);
		
		JLabel labelState = new JLabel("Estado:");
		carsDataPanel.add(labelState);
		comboState = new JComboBox<String>();
		comboState.removeItem("     ");
		carsDataPanel.add(comboState);

		JLabel labelPlate = new JLabel("Placa:");
		carsDataPanel.add(labelPlate);
		JTextField  textPlate = new JTextField(12);
		carsDataPanel.add(textPlate);

		JLabel labelOwnerId = new JLabel("Cedula Propietario:");
		carsDataPanel.add(labelOwnerId);
		JTextField textOwnerId= new JTextField(12);
		carsDataPanel.add(textOwnerId);

		JLabel labelOwnerName = new JLabel("Nombre Propietario:");
		carsDataPanel.add(labelOwnerName);
		JTextField textOwnerName = new JTextField(12);
		carsDataPanel.add(textOwnerName);

		JLabel labelOwnerLastName = new JLabel("Apellido:");
		carsDataPanel.add(labelOwnerLastName);
		JTextField textOwnerLastName = new JTextField(12);
		carsDataPanel.add(textOwnerLastName);		
		
		JLabel labelColor = new JLabel("Color:");
		carsDataPanel.add(labelColor);
		comboColor = new JComboBox<String>();
		comboColor.removeItem("     ");
		carsDataPanel.add(comboColor);

		JLabel labelBrand = new JLabel("Marca:");
		carsDataPanel.add(labelBrand);
		comboBrand = new JComboBox<String>();
		comboBrand.removeItem("     ");
		carsDataPanel.add(comboBrand);	
		
		JLabel labelModel = new JLabel("Modelo:");
		carsDataPanel.add(labelModel);
		comboModel = new JComboBox<String>();
		comboModel.removeItem("     ");
		carsDataPanel.add(comboModel);

		JLabel labelDescription = new JLabel("Descripcion:");
		carsDataPanel.add(labelDescription);
		JTextArea textDescription = new JTextArea(4,20);
		carsDataPanel.add(textDescription);

		layout.setHorizontalGroup(

		layout.createSequentialGroup()

		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(labelTitle)
				.addComponent(labelState).addComponent(labelPlate).addComponent(labelOwnerId)
				.addComponent(labelOwnerName).addComponent(labelOwnerLastName)
				.addComponent(labelColor).addComponent(labelBrand)
				.addComponent(labelModel).addComponent(labelDescription))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(comboState, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(textPlate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textOwnerId, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textOwnerName, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textOwnerLastName, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(comboColor, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(comboBrand, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(comboModel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(textDescription, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))

		);

		layout.setVerticalGroup(

		layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelTitle))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelState)
						.addComponent(comboState))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelPlate)
						.addComponent(textPlate))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelOwnerId)
						.addComponent(textOwnerId))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelOwnerName)
						.addComponent(textOwnerName))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelOwnerLastName)
						.addComponent(textOwnerLastName))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelColor)
						.addComponent(comboColor))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelBrand)
						.addComponent(comboBrand))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelModel)
						.addComponent(comboModel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelDescription)
						.addComponent(textDescription))								
		);
		
		container.add(carsDataPanel, BorderLayout.NORTH);
		
		//add new JPanel to the directions data		
		JPanel directionsPanel = new JPanel();		
		
		GroupLayout layoutDirection = new GroupLayout(directionsPanel);
		directionsPanel.setLayout(layoutDirection);

		layoutDirection.setAutoCreateGaps(true);
		layoutDirection.setAutoCreateContainerGaps(true);
		
		//added a title to the ticket data
		JLabel labelDirectionsTitle = new JLabel("Dirección     ");
		labelDirectionsTitle.setFont(new Font(null, Font.BOLD, 20));
		directionsPanel.add(labelDirectionsTitle);

		JLabel labelCountry = new JLabel("País:");
		directionsPanel.add(labelCountry);
		comboCountry = new JComboBox<String>();
		comboCountry.removeItem("");
		directionsPanel.add(comboCountry);

		JLabel labelDirectionState = new JLabel("Estado:");
		directionsPanel.add(labelDirectionState);
		comboDirectionState = new JComboBox<String>();
		comboDirectionState.removeItem("     ");
		directionsPanel.add(comboDirectionState);

		JLabel labelCity = new JLabel("Ciudad:");
		directionsPanel.add(labelCity);
		JTextField textCity = new JTextField(12);
		directionsPanel.add(textCity);

		JLabel labelZipCode = new JLabel("Codigo Postal:");
		directionsPanel.add(labelZipCode);
		JTextField textZipCode = new JTextField(12);
		directionsPanel.add(textZipCode);

		JLabel labelPhone = new JLabel("Telefono:");
		directionsPanel.add(labelPhone);
		JTextField textPhone = new JTextField(12);
		directionsPanel.add(textPhone);

		JLabel labelStreet = new JLabel("Calle/Casa/Apto:");
		directionsPanel.add(labelStreet);
		JTextField textStreet = new JTextField(12);
		directionsPanel.add(textStreet);
				
		layoutDirection.setHorizontalGroup(
				
		layoutDirection.createSequentialGroup()
		
		.addGroup(layoutDirection.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(labelDirectionsTitle)
				.addComponent(labelCountry).addComponent(labelDirectionState).addComponent(labelCity)
				.addComponent(labelZipCode).addComponent(labelPhone).addComponent(labelStreet))
				.addGroup(layoutDirection.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(comboCountry, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(comboDirectionState, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(textCity, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(textZipCode, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(textPhone, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(textStreet, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE))
		);

		layoutDirection.setVerticalGroup(
		layoutDirection.createSequentialGroup()
		.addGroup(layoutDirection.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelDirectionsTitle))
				.addGroup(layoutDirection.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelCountry)
						.addComponent(comboCountry))	
				.addGroup(layoutDirection.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelDirectionState)
						.addComponent(comboDirectionState))
				.addGroup(layoutDirection.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelCity)
						.addComponent(textCity))	
				.addGroup(layoutDirection.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelZipCode)
						.addComponent(textZipCode))
				.addGroup(layoutDirection.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelPhone)
						.addComponent(textPhone))	
				.addGroup(layoutDirection.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelStreet)
						.addComponent(textStreet))			
		);
		
		container.add(directionsPanel, BorderLayout.CENTER);		
		
		wrapContainerPanel.add(container);
		wrapContainerPanel.add(Box.createHorizontalStrut(50));
		
		return wrapContainerPanel;
	}

	private JPanel createSubPanelMiddle() {
		JPanel container = new JPanel(new BorderLayout());

		JPanel topPanel = new JPanel();
		
		CheckBoxListener lForCheckBox = new CheckBoxListener();		

		checkBillsName = new JCheckBox(" Facturar a nombre de Empresa",false);
		checkBillsName.addItemListener(lForCheckBox);
		topPanel.add(checkBillsName);		
		container.add(topPanel,BorderLayout.NORTH);		
		
		if(checkBillsName.isSelected()){
			container.add(companyPanel(),BorderLayout.CENTER);
		}
		else{
			container.add(personalPanel(),BorderLayout.CENTER);
		}
		
		return container;
	}
	

	private JPanel createValetCashier() {
		
		JPanel containerPanel = new JPanel();
		
		containerPanel.setLayout(new GridBagLayout());
		
		JPanel thePanel = new JPanel();
		
		thePanel.setLayout(new GridBagLayout());
		
		Border loweredEtched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
		TitledBorder title;
		
		title = BorderFactory.createTitledBorder(loweredEtched, "Bs. F.");
		
		title.setTitleFont(new Font(null, Font.BOLD, 24));
		
		thePanel.setBorder(title);
		
		labelPrice = new JLabel("0,00");
		
		labelPrice.setFont(new Font(null, Font.BOLD, 48));
		
		labelPrice.setForeground(Color.RED);
		
		ButtonListener lForButton = new ButtonListener();
		
		GridBagConstraints c = new GridBagConstraints();
		
		thePanel.add(labelPrice);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 10;
		c.ipady = 10;
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.CENTER;
		
		containerPanel.add(thePanel, c);
		
		buttonAccept = new JButton("Aceptar");
		buttonAccept.addActionListener(lForButton);
		buttonAccept.setActionCommand("accept");
		buttonAccept.setMnemonic('A');
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 10;
		c.ipady = 10;
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.insets = new Insets(5,5,5,5);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		containerPanel.add(buttonAccept, c);
		
		buttonCancel = new JButton("Cancelar");
		buttonCancel.addActionListener(lForButton);
		buttonCancel.setActionCommand("cancel");
		buttonCancel.setMnemonic('C');
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 10;
		c.ipady = 10;
		c.weightx = 0.5;
		c.gridwidth = 1;
		c.insets = new Insets(5,5,5,5);
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		containerPanel.add(buttonCancel, c);
		
		buttonAccept.setVisible(false);
		buttonCancel.setVisible(false);
		
		return containerPanel;
	}

	private JPanel personalPanel(){
		
		JPanel personalPanel = new JPanel();
		GroupLayout layout = new GroupLayout(personalPanel);
		personalPanel.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		//added a title to the ticket data
		JLabel labelTitle = new JLabel("Datos Personales");
		labelTitle.setFont(new Font(null, Font.BOLD, 18));
		personalPanel.add(labelTitle);
		
		JLabel labelId = new JLabel("Cedula:");
		personalPanel.add(labelId);
		JTextField textId = new JTextField(12);
		personalPanel.add(textId);

		JLabel labelFirstName = new JLabel("Nombre:");
		personalPanel.add(labelFirstName);
		JTextField textFirstName = new JTextField(12);
		personalPanel.add(textFirstName);

		JLabel labelLastName= new JLabel("Apellido:");
		personalPanel.add(labelLastName);
		JTextField textLastName = new JTextField(12);
		personalPanel.add(textLastName);

		JLabel labelEmail = new JLabel("Email:");
		personalPanel.add(labelEmail);
		JTextField textEmail = new JTextField(12);
		personalPanel.add(textEmail);

		JLabel labelMobilePhone = new JLabel("Móvil:");
		personalPanel.add(labelMobilePhone);
		JTextField textMobilePhone = new JTextField(12);
		personalPanel.add(textMobilePhone);		
		
		layout.setHorizontalGroup(
		layout.createSequentialGroup()

		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(labelTitle)
				.addComponent(labelId).addComponent(labelFirstName).addComponent(labelLastName)
				.addComponent(labelEmail).addComponent(labelMobilePhone))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(textId, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textFirstName, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textLastName, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textEmail, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textMobilePhone, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
		);

		layout.setVerticalGroup(

		layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelTitle))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelId)
						.addComponent(textId))	
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelFirstName)
						.addComponent(textFirstName))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelLastName)
						.addComponent(textLastName))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelEmail)
						.addComponent(textEmail))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelMobilePhone)
						.addComponent(textMobilePhone))			
		);		    
		
		return personalPanel;		
	}
	
	private JPanel companyPanel(){
		
		JPanel companyPanel = new JPanel();		
		GroupLayout layoutcompany = new GroupLayout(companyPanel);
		companyPanel.setLayout(layoutcompany);

		layoutcompany.setAutoCreateGaps(true);
		layoutcompany.setAutoCreateContainerGaps(true);
		
		//added a title to the ticket data
		JLabel labelTitleCompany = new JLabel("Datos de la Empresa");
		labelTitleCompany.setFont(new Font(null, Font.BOLD, 18));
		companyPanel.add(labelTitleCompany);

		JLabel labelCompany = new JLabel("Empresa:");
		companyPanel.add(labelCompany);
		comboCompany = new JComboBox<String>();
		comboCompany.removeItem("");
		companyPanel.add(comboCompany);

		JLabel labelShop = new JLabel("Local:");
		companyPanel.add(labelShop);
		comboShop = new JComboBox<String>();
		comboShop.removeItem("     ");
		companyPanel.add(comboShop);
				
		layoutcompany.setHorizontalGroup(
		layoutcompany.createSequentialGroup()
		.addGroup(layoutcompany.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(labelTitleCompany)
				.addComponent(labelCompany).addComponent(labelShop))
				.addGroup(layoutcompany.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(comboCompany, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE)
						.addComponent(comboShop, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.DEFAULT_SIZE))
		);

		layoutcompany.setVerticalGroup(
		layoutcompany.createSequentialGroup()
		.addGroup(layoutcompany.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelTitleCompany))
				.addGroup(layoutcompany.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelCompany)
						.addComponent(comboCompany))	
				.addGroup(layoutcompany.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelShop)
						.addComponent(comboShop))			
		);
		
		return companyPanel;		
	}
	
	private JPanel createSubPanelCharge() {

		JPanel container = new JPanel();
		//adding box layout
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS)); // top to bottom
		//creating the 3 panels inside
		JPanel thePanel = new JPanel();
		JPanel picturePanel = new JPanel();
		JPanel paymentPanel = new JPanel();
		
		GroupLayout layout = new GroupLayout(thePanel);
		thePanel.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		//added a title to the ticket data
		JLabel labelTitle = new JLabel("Datos del Ticket");
		labelTitle.setFont(new Font(null, Font.BOLD, 22));
		thePanel.add(labelTitle);
		
		JLabel labelTicket = new JLabel("Ticket No.:");
		thePanel.add(labelTicket);
		textTicket = new JTextField(12);
		thePanel.add(textTicket);

		JLabel labelDate = new JLabel("Fecha de Entrada:");
		thePanel.add(labelDate);
		MaskFormatter mask = null;
		try {
			mask = new MaskFormatter("##/##/#### ##:##");
			mask.setPlaceholderCharacter('_');
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JFormattedTextField textDateIn = new JFormattedTextField(mask);
		textDateIn.setEditable(false);
		textDateIn.setColumns(12);
		thePanel.add(textDateIn);

		JLabel labelEntrance = new JLabel("Entrada:");
		thePanel.add(labelEntrance);
		JTextField textEntrance = new JTextField(12);
		textEntrance.setEditable(false);
		thePanel.add(textEntrance);

		JLabel labelDuration = new JLabel("Duracion:");
		thePanel.add(labelDuration);
		JTextField textDuration = new JTextField(12);
		textDuration.setEditable(false);
		thePanel.add(textDuration);

		JLabel labelExpiration = new JLabel("Expiracion:");
		thePanel.add(labelExpiration);
		JTextField textExpiration = new JTextField(12);
		textExpiration.setEditable(false);
		thePanel.add(textExpiration);		
		
		layout.setHorizontalGroup(

		layout.createSequentialGroup()

		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(labelTitle)
				.addComponent(labelTicket).addComponent(labelDate).addComponent(labelEntrance)
				.addComponent(labelDuration).addComponent(labelExpiration))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(textTicket, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textDateIn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textEntrance, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textDuration, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textExpiration, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))

		);

		layout.setVerticalGroup(

		layout.createSequentialGroup()
		.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelTitle))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelTicket)
						.addComponent(textTicket))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelDate)
						.addComponent(textDateIn))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelEntrance)
						.addComponent(textEntrance))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelDuration)
						.addComponent(textDuration))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelExpiration)
						.addComponent(textExpiration))				
		);
		
		//add thePanel to the boxlayoutPanel
		container.add(thePanel);
	    container.add(Box.createVerticalStrut(25));
	    
		picturePanel.setLayout(new FlowLayout() );		
		
		ImageIcon image = new ImageIcon("resources/image404.png");
		JLabel labelPicture = new JLabel(image);	
		labelPicture.setAlignmentY(TOP_ALIGNMENT);
		
		picturePanel.add(labelPicture);		
		container.add(picturePanel);
		
		//payment panel
		GroupLayout payment = new GroupLayout(paymentPanel);
		paymentPanel.setLayout(payment);
		
		payment.setAutoCreateGaps(true);
		payment.setAutoCreateContainerGaps(true);
		
		JLabel labelPayment = new JLabel("Pago");
		labelPayment.setFont(new Font(null, Font.BOLD, 22));
		paymentPanel.add(labelPayment);
		
		JLabel labelTotal = new JLabel("Total");
		labelTotal.setFont(new Font(null, Font.BOLD, 18));
		paymentPanel.add(labelTotal);
		
		JLabel labelMoney = new JLabel("Bs.");
		labelMoney.setForeground(Color.RED);		
		paymentPanel.add(labelMoney);
		
		JLabel labelCashed = new JLabel("Entregado");
		paymentPanel.add(labelCashed);
		JTextField textCashed = new JTextField(12);
		textCashed.setEditable(false);
		paymentPanel.add(textCashed);
		
		JLabel labelChange = new JLabel("Vuelto");
		paymentPanel.add(labelChange);
		JTextField textChange = new JTextField(12);
		textChange.setEditable(false);
		paymentPanel.add(textChange);	
		
		payment.setHorizontalGroup(
				payment.createSequentialGroup()

		.addGroup(payment.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(labelPayment)
				.addComponent(labelTotal).addComponent(labelCashed).addComponent(labelChange))
				.addGroup(payment.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(labelMoney, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textCashed, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textChange, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))

		);

		payment.setVerticalGroup(
				payment.createSequentialGroup()
				.addGroup(payment.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelPayment))
				.addGroup(payment.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelTotal).addComponent(labelMoney))
				.addGroup(payment.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelCashed)
						.addComponent(textCashed))
				.addGroup(payment.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(labelChange)
						.addComponent(textChange))			
		);
		container.add(paymentPanel);
		container.add(Box.createVerticalStrut(140));
		
		return container;
	}
 
	private JMenuBar createMenu() {
		// JPanel thePanel = new JPanel(new BorderLayout());

		menuBar = new JMenuBar();

		// Build the first menu.
		mainMenu = new JMenu("Archivo");
		mainMenu.setMnemonic(KeyEvent.VK_A);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Archivo");
		menuBar.add(mainMenu);

		// a group of JMenuItems
		MenuItemListener lForMenuItem = new MenuItemListener();

		menuItem = new JMenuItem("Reimprimir Factura", new ImageIcon("resources/invoice-reprint.png"));
		menuItem.setActionCommand("reprint");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_F);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("Esto sirve para reimprimir una factura");
		mainMenu.add(menuItem);

		mainMenu.addSeparator();
		menuItem = new JMenuItem("Cerrar Sesion", new ImageIcon("resources/signout.png"));
		menuItem.setActionCommand("logout");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_C);
		mainMenu.add(menuItem);

		menuItem = new JMenuItem("Salir", new ImageIcon("resources/close-program.png"));
		menuItem.setActionCommand("close");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		mainMenu.add(menuItem);

		// Build second menu in the menu bar.
		mainMenu = new JMenu("Ver");
		mainMenu.setMnemonic(KeyEvent.VK_V);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Ver");
		menuBar.add(mainMenu);

		menuItem = new JMenuItem("Resumen del Cierre Actual", new ImageIcon("resources/actual-resume.png"));
		menuItem.setActionCommand("actual_resume");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_R);
		mainMenu.add(menuItem);

		CheckBoxListener lForCheckBox = new CheckBoxListener();

		mainMenu.addSeparator();
		cbMenuItemToolbar = new JCheckBoxMenuItem("Barra de Herramientas");
		// cbMenuItem.setActionCommand("toolbar");
		cbMenuItemToolbar.addItemListener(lForCheckBox);
		cbMenuItemToolbar.setMnemonic(KeyEvent.VK_B);
		mainMenu.add(cbMenuItemToolbar);

		cbMenuItemStatusbar = new JCheckBoxMenuItem("Barra de Estado");
		cbMenuItemStatusbar.setActionCommand("statusbar");
		cbMenuItemStatusbar.addItemListener(lForCheckBox);
		cbMenuItemStatusbar.setMnemonic(KeyEvent.VK_E);
		mainMenu.add(cbMenuItemStatusbar);

		mainMenu.addSeparator();
		menuItem = new JMenuItem("Ver Log", new ImageIcon("resources/log.png"));
		menuItem.setActionCommand("log");
		menuItem.setMnemonic(KeyEvent.VK_L);
		mainMenu.add(menuItem);

		// Build third menu in the menu bar.
		mainMenu = new JMenu("Sistema");
		mainMenu.setMnemonic(KeyEvent.VK_S);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Sistema");
		menuBar.add(mainMenu);

		menuItem = new JMenuItem("Limpiar Pantalla", new ImageIcon("resources/clean-screen.png"));
		menuItem.setActionCommand("clean_screen");
		menuItem.setMnemonic(KeyEvent.VK_L);
		mainMenu.add(menuItem);

		mainMenu.addSeparator();
		menuItem = new JMenuItem("Personalizar Factura", new ImageIcon("resources/fixed-invoice.png"));
		menuItem.setActionCommand("fixed_invoice");
		menuItem.setMnemonic(KeyEvent.VK_P);
		mainMenu.add(menuItem);

		menuItem = new JMenuItem("Detalle de Ultima Transaccion", new ImageIcon("resources/last-transaction.png"));
		menuItem.setActionCommand("last_transaction");
		menuItem.setMnemonic(KeyEvent.VK_D);
		mainMenu.add(menuItem);

		mainMenu.addSeparator();
		menuItem = new JMenuItem("Calculadora", new ImageIcon("resources/calculator.png"));
		menuItem.setActionCommand("calc");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_C);
		mainMenu.add(menuItem);

		// Build fourth menu in the menu bar.
		mainMenu = new JMenu("Reportes");
		mainMenu.setMnemonic(KeyEvent.VK_R);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Reportes");
		menuBar.add(mainMenu);

		menuItem = new JMenuItem("Reporte Fiscal Diario (Z)", new ImageIcon("resources/z-report.png"));
		menuItem.setActionCommand("reporte_z");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_R);
		mainMenu.add(menuItem);

		// Build fifth menu in the menu bar.
		mainMenu = new JMenu("Ayuda");
		mainMenu.setMnemonic(KeyEvent.VK_Y);
		mainMenu.getAccessibleContext().setAccessibleDescription("Menu Ayuda");
		menuBar.add(mainMenu);

		menuItem = new JMenuItem("Acerca de", new ImageIcon("resources/about.png"));
		menuItem.setActionCommand("aboutus");
		menuItem.addActionListener(lForMenuItem);
		menuItem.setMnemonic(KeyEvent.VK_A);
		mainMenu.add(menuItem);

		menuBar.add(mainMenu);
		// thePanel.add(menuBar, BorderLayout.PAGE_START);

		return menuBar;
	}

	private JComponent createToolBar() {

		theToolBarPanel = new JPanel(new BorderLayout());

		toolBar = new JToolBar("Barra de Herramientas");

		toolBarButtonCollect = new JButton("Sistema de Cobro", new ImageIcon("resources/cash_register.png"));

		toolBar.add(toolBarButtonCollect);

		toolBar.addSeparator();
		
		toolBarButtonManualTicket = new JButton("Ticket Manual", new ImageIcon("resources/new_ticket.png"));

		toolBar.add(toolBarButtonManualTicket);

		toolBar.addSeparator();

		toolBarButtonLostTicket = new JButton("Ticket Perdido", new ImageIcon("resources/lost_ticket.png"));

		toolBar.add(toolBarButtonLostTicket);

		toolBar.addSeparator();
		
		toolBarButtonCutoff = new JButton("Cierres", new ImageIcon("resources/lock.png"));

		toolBar.add(toolBarButtonCutoff);

		toolBar.setFloatable(false);

		theToolBarPanel.add(toolBar, BorderLayout.PAGE_START);

		return theToolBarPanel;
	}

	private JComponent createStatusBar() {

		JMenu subMenu, subMenu2;
		JMenuItem menuItem, subMenuItem;

		String[] serialPorts = null;

		theStatusBarPanel = new JPanel(new BorderLayout());
		
		theStatusBarPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		JMenuBar menuBar = new JMenuBar();

		MenuItemListener lForMenuItem = new MenuItemListener();

		menu = new JMenu("•");
		menu.setIcon(new ImageIcon("resources/printer.png"));
		menu.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		menuItemConnect = new JMenuItem("Conectar");
		menuItemConnect.setActionCommand("connect");
		menuItemConnect.addActionListener(lForMenuItem);
		menu.add(menuItemConnect);
		menuItemDisconnect = new JMenuItem("Desconectar");
		menuItemDisconnect.setActionCommand("disconnect");
		menuItemDisconnect.addActionListener(lForMenuItem);
		menuItemDisconnect.setEnabled(false);
		menu.add(menuItemDisconnect);
		menuItem = new JMenuItem("Prueba");
		menuItem.setActionCommand("test");
		menuItem.addActionListener(lForMenuItem);
		menu.add(menuItem);
		subMenu = new JMenu("Puertos");

		// Create the COM Ports
		subMenu2 = new JMenu("COM");
		try {

			serialPorts = CommPortUtils.getSerialPorts();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (serialPorts.length == 0) {
			subMenuItem = new JMenuItem("No hay puertos COM");
			subMenu2.add(subMenuItem);
		} else {
			for (String port : serialPorts) {
				subMenuItem = new JMenuItem(port);
				subMenuItem.setActionCommand(port);
				subMenuItem.addActionListener(lForMenuItem);
				subMenu2.add(subMenuItem);
			}
		}

		subMenu.add(subMenu2);
		subMenuItem = new JMenuItem("USB");
		subMenu.add(subMenuItem);

		menu.add(subMenu);
		menuBar.add(menu);

		labelStatus = new JLabel();
		// theFrame.add(BorderLayout.SOUTH, menuBar);
		theStatusBarPanel.add(menuBar, BorderLayout.WEST);

		theStatusBarPanel.add(labelStatus, BorderLayout.EAST);
		;

		return theStatusBarPanel;
	}

	private class PopUpMenu extends JPopupMenu {
		JMenuItem closeReportMenu;
		
		public PopUpMenu() {
			PopUpMenuListener lForPopUpMenu = new PopUpMenuListener();
			closeReportMenu = new JMenuItem("Cerrar Reporte (Cierre X)");
			closeReportMenu.addActionListener(lForPopUpMenu);
			this.add(closeReportMenu);
		}
	}
	
	private class PopUpMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			PrintXReport x = new PrintXReport();
			Thread t = new Thread(x);
			t.start();
		}
		
	}
	
	private class MouseClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			DefaultMutableTreeNode reportNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			
			//JOptionPane.showMessageDialog(null, "parent node user object: " + stationNode.getUserObject());
			if(SwingUtilities.isRightMouseButton(e) && reportNode.getUserObject() != null && reportNode.getLevel() == 3) {
				DefaultMutableTreeNode stationNode = new DefaultMutableTreeNode(reportNode.getParent().getParent());
				if(stationNode.getUserObject().toString().equalsIgnoreCase(stationInfo.getName())) {
					PopUpMenu popUpMenu = new PopUpMenu();
					popUpMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}
		
	}
	
	private class ButtonListener implements ActionListener, KeyListener {

		@Override
		public void keyPressed(KeyEvent ev) {
			SelectValetRun v = new SelectValetRun(KeyEvent.getKeyText(ev.getKeyCode()));
			new Thread(v).start();
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			if(ev.getActionCommand().equalsIgnoreCase("reload-reports")) {
				stationsWithSummary = Db.getStationsWithSummary();
				summaries = Db.loadSummaries();
				tree.setModel(new TreeDataModel(stationsWithSummary, summaries));
			}else{
				SelectValetRun v = new SelectValetRun(ev.getActionCommand());
				new Thread(v).start();
			}
		}
		
	}
	
	private class MenuItemListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {

			if (ev.getActionCommand().startsWith("COM")) {
				activePort = ev.getActionCommand();
				labelStatus.setText(activePort + " seleccionado");
				OpenCommPortRun o = new OpenCommPortRun(activePort);
				if (!activePort.isEmpty() && !activePort.equals(null)) {
					new Thread(o).start();
				} else {
					labelStatus.setText("No hay puerto COM activo");
				}
			} else if (ev.getActionCommand().startsWith("USB")) {
				activePort = ev.getActionCommand();
				labelStatus.setText(activePort + " seleccionado");
			}

			switch (ev.getActionCommand()) {
			case "connect":
				if (activePort.isEmpty()) {
					activePort = SerialPortList.getPortNames()[0];
				}
				OpenCommPortRun o = new OpenCommPortRun(activePort);
				if (!activePort.isEmpty() && !activePort.equals(null)) {
					new Thread(o).start();
				} else {
					labelStatus.setText("No hay puerto COM activo");
				}
				break;
			case "disconnect":
				CloseCommPortRun c = new CloseCommPortRun();
				Thread t = new Thread(c);
				t.start();
				break;
			case "test":
				labelStatus.setText("Enviando prueba al puerto " + activePort);
				try {
					@SuppressWarnings("unused")
					boolean sentCmd = fiscalPrinter.SendCmd(PrinterCommand.printTest());
				} catch (PrinterException e) {
					e.printStackTrace();
				}
				break;
			}

			switch (ev.getActionCommand()) {
			case "reprint":

				break;
			case "logout":
				dispose();
				new CheckStation();
				break;
			case "close":
				System.exit(0);
				break;
			case "actual_resume":

				break;
			case "log":
				new ViewLog();
				break;
			case "clean_screen":

				break;
			case "fixed_invoice":

				break;
			case "last_transaction":

				break;
			case "calc":
				try {
					Runtime.getRuntime().exec("calc");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				break;
			case "reporte_z":
				PrintZReport z = new PrintZReport();
				Thread t = new Thread(z);
				t.start();
				break;
			case "aboutus":
				new AboutUs();
				break;
			}// END OF switch

		}// END OF method ActionPerformed

	}// END OF class MenuItemListener

	private class CheckBoxListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent ev) {

//			ItemSelectable checkBillsName = null;
			if (ev.getItemSelectable() == cbMenuItemToolbar) {
				switch (ev.getStateChange()) {
				case ItemEvent.SELECTED:
					theToolBarPanel.setVisible(true);
					break;
				case ItemEvent.DESELECTED:
					theToolBarPanel.setVisible(false);
					break;
				}
			} else if (ev.getItemSelectable() == cbMenuItemStatusbar) {
				switch (ev.getStateChange()) {
				case ItemEvent.SELECTED:
					theStatusBarPanel.setVisible(true);
					break;
				case ItemEvent.DESELECTED:
					theStatusBarPanel.setVisible(false);
					break;
				}
			} 
			else if (ev.getItemSelectable() == checkBillsName) {
				if(ev.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
					personalPanel().setVisible(false);
					companyPanel().setVisible(true);
		        } else {//checkbox has been deselected
		        	personalPanel().setVisible(true);
					companyPanel().setVisible(false);
		        };
//				switch (ev.getStateChange()) {
//				case ItemEvent.SELECTED:
//					personalPanel.setVisible(false);
//					companyPanel.setVisible(true);
//					break;
//				case ItemEvent.DESELECTED:
//					personalPanel.setVisible(true);
//					companyPanel.setVisible(false);;
//					break;
//				}
			}// END OF else if

		}// END OF method itemStateChanged

	}// END OF class CheckBoxListener

	private class SelectValetRun implements Runnable {

		String actionCommand;
		
		SelectValetRun(String actionCommand) {
			this.actionCommand = actionCommand;
		}
		
		@Override
		public synchronized void run() {
			if(stationMode.equals("Valet")){
				if(transactions.size() > 0) {
					switch(actionCommand){
					case "valet-invoice":
						int transactionIndex = transactionSelected(transactions, transactionsType.get(0).getId());
						if(transactionIndex > -1) {
							transactions.remove(transactionIndex);
							buttonValetInvoice.setEnabled(true);
						}else{
							transactions.add(transactionsType.get(0));
							buttonValetInvoice.setEnabled(false);
							labelPrice.setText(String.valueOf(getSubTotal(transactions)));
						}
						break;
					case "valet-lost":
						transactions.add(transactionsType.get(1));
						buttonValetLostTicket.setEnabled(false);
						labelPrice.setText(String.valueOf(getSubTotal(transactions)));
						break;
					}
				}else{
					switch(actionCommand){
					case "valet-invoice":
						transactions.add(transactionsType.get(0));
						buttonValetInvoice.setEnabled(false);
						labelPrice.setText(String.valueOf(getSubTotal(transactions)));
						enableButtons();
						break;
					case "valet-lost":
						transactions.add(transactionsType.get(0));
						transactions.add(transactionsType.get(1));
						buttonValetInvoice.setEnabled(false);
						buttonValetLostTicket.setEnabled(false);
						labelPrice.setText(String.valueOf(getSubTotal(transactions)));
						enableButtons();
						break;
					}
				}
				if(actionCommand.equalsIgnoreCase("cancel")) {
					labelPrice.setText("0,00");
					transactions.clear();
					disableButtons();
				}
				if(actionCommand.equalsIgnoreCase("accept")) {
					buttonValetInvoice.setEnabled(false);
					buttonValetLostTicket.setEnabled(false);
					buttonAccept.setEnabled(false);
					buttonCancel.setEnabled(false);
					textTicket.setEditable(false);
					CheckOutRun co = new CheckOutRun(transactions);
					Thread t = new Thread(co);
					t.start();
				}
			}
		}
		
	}
	
	private class CheckOutRun implements Runnable {
		
		ArrayList<Transaction> transactions;
		S1PrinterData statusS1;
		@SuppressWarnings("unused")
		boolean sentCmd = false;

		CheckOutRun(ArrayList<Transaction> transactions) {
			this.transactions = transactions;
		}
		
		@Override
		public void run() {
			Db db = new Db();
			int ticketNumber = 0;
			int insertedSummaryId = 0;
			boolean isTicketProcessed = false;
			
			printerChecker();
			
			if(db.testConnection()){
				if(isPrinterConnected){
					try{
						ticketNumber = Integer.parseInt(textTicket.getText());
						
						isTicketProcessed = Db.checkTicket(ticketNumber);
						if(!isTicketProcessed) {
							if(!textTicket.getText().isEmpty()) {
								try {
									sentCmd = fiscalPrinter.SendCmd(PrinterCommand.setClientInfo(0, "Ticket #: " + ticketNumber));
								} catch (PrinterException ce) {
									ce.printStackTrace();
								}
								for(Transaction t: transactions) {
									try {
										sentCmd = fiscalPrinter.SendCmd(PrinterCommand.setItem(
												PrinterCommand.TAX1, 
												t.getMaxAmount(), 
												1, 
												t.getName()));
									} catch (PrinterException ce) {
										ce.printStackTrace();
									}
								}
								
								try {
									sentCmd = fiscalPrinter.SendCmd(PrinterCommand.checkOut(
											PrinterCommand.PAYMENT_TYPE_EFECTIVO_01));
								} catch (PrinterException ce) {
									ce.printStackTrace();
								}
								try {
									sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("TICKET VALET"));
								} catch (PrinterException ce) {
									ce.printStackTrace();
								}
								try {
									sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Ticket Valet #: " + ticketNumber));
								} catch (PrinterException ce) {
									ce.printStackTrace();
								}
								DateTime dt = new DateTime();
								DateTimeFormatter tFormatter = DateTimeFormat.forPattern("HH:mm:ss");
								DateTimeFormatter dFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
								try {
									sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Hora: " + dt.toString(tFormatter)));
								} catch (PrinterException ce) {
									ce.printStackTrace();
								}
								try {
									sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Fecha: " + dt.toString(dFormatter)));
								} catch (PrinterException ce) {
									ce.printStackTrace();
								}
								try {
									sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Cajero: " + user.getName()));
								} catch (PrinterException ce) {
									ce.printStackTrace();
								}
								try {
									sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentEnd("PAGADO"));
								} catch (PrinterException ce) {
									ce.printStackTrace();
								}
								db = new Db();
								
								if(summaryHasInvoice) {
									for(Transaction t: transactions) {
										db.insertTransaction(stationId, summaryId, ticketNumber, t.getMaxAmount(), 12, 
											t.getId(), payTypes.get(0).getId());
									}
								}else{
									if(summaryId > 0) {
										summaryHasInvoice = true;
										for(Transaction t: transactions) {
											db.insertTransaction(stationId, summaryId, ticketNumber, t.getMaxAmount(), 12, 
													t.getId(), payTypes.get(0).getId());
										}
									}else{
										try{
											statusS1 = fiscalPrinter.getS1PrinterData();
										} catch(PrinterException se) {
											se.printStackTrace();
										}
										int firstInvoiceNumber = statusS1.getLastInvoiceNumber();
										db = new Db();
										insertedSummaryId = db.insertSummary(stationId, user.getId(), firstInvoiceNumber);
										
										if(insertedSummaryId > 0) {
											summaryId = insertedSummaryId;
											summaryHasInvoice = true;
											for(Transaction t: transactions) {
												db.insertTransaction(stationId, summaryId, ticketNumber, t.getMaxAmount(), 12, 
														t.getId(), payTypes.get(0).getId());
											}
											stationsWithSummary = Db.getStationsWithSummary();
											summaries = Db.loadSummaries();
											tree.setModel(new TreeDataModel(stationsWithSummary, summaries));
										}else{
											summaryId = 0;
											summaryHasInvoice = false;
											JOptionPane.showMessageDialog(null, "Error al crear el reporte", "Error de Reporte", JOptionPane.ERROR_MESSAGE);
										}
									}
								}
								transactions.clear();
								textTicket.setText("");
								labelPrice.setText("0,00");
								disableButtons();
							}else{
								JOptionPane.showMessageDialog(null, "El numero de ticket no puede estar vacio", "Numero de ticket invalido", JOptionPane.WARNING_MESSAGE);
							}
						}else{
							JOptionPane.showMessageDialog(null, "Ticket ya procesado, inserte el numero correcto","Ticket procesado", JOptionPane.ERROR_MESSAGE);
						}
					} catch(NumberFormatException ne) {
						JOptionPane.showMessageDialog(null, "Introduzca un numero de ticket valido", "Numero de ticket invalido", JOptionPane.WARNING_MESSAGE);
					}
				}else{
					JOptionPane.showMessageDialog(null, "La impresora esta desconectada", "Impresora desconectada", JOptionPane.ERROR_MESSAGE);
				}
			}else{
				JOptionPane.showMessageDialog(null, "La red esta desconectada, conectela de nuevo", "Red desconectada", JOptionPane.ERROR_MESSAGE);
			}
			textTicket.setEditable(true);
			buttonAccept.setEnabled(true);
			buttonCancel.setEnabled(true);
		}
		
	}
	
	private class OpenCommPortRun implements Runnable{
		
		String activePort;
		
		OpenCommPortRun(String activePort){
			this.activePort = activePort;
		}
		
		@Override
		public void run() {
			
			if (fiscalPrinter.OpenFpctrl(activePort)) {
				isPrinterConnected = true;
				menuItemDisconnect.setEnabled(true);
				menuItemConnect.setEnabled(false);
				menu.setForeground(Color.GREEN);
				labelStatus.setText("Conectado al puerto " + activePort);
			} else {
				isPrinterConnected = false;
				labelStatus.setText("Error al conectarse a la impresora");
				fiscalPrinter.CloseFpctrl();
			}
			
		}
		
	}
	
	private class CloseCommPortRun implements Runnable {

		@Override
		public void run() {
			fiscalPrinter.CloseFpctrl();
			menuItemConnect.setEnabled(true);
			menuItemDisconnect.setEnabled(false);
			menu.setForeground(Color.BLACK);
			labelStatus.setText("Se desconecto la impresora");
		}
		
	}

	public void enableButtons() {
		buttonAccept.setEnabled(true);
		buttonAccept.setVisible(true);
		buttonCancel.setEnabled(true);
		buttonCancel.setVisible(true);
	}
	
	public void disableButtons() {
		buttonAccept.setEnabled(false);
		buttonAccept.setVisible(false);
		buttonCancel.setEnabled(false);
		buttonCancel.setVisible(false);
		buttonValetInvoice.setEnabled(true);
		buttonValetLostTicket.setEnabled(true);
	}

	public double getSubTotal(ArrayList<Transaction> transactions) {
		double subTotal = 0;
		
		for(Transaction t: transactions) {
			subTotal += t.getMaxAmount();
		}
		return subTotal;
	}

	private int transactionSelected(ArrayList<Transaction> transactions, int id) {
		int selectedId = -1;
		int i = 0;
		for(Transaction t: transactions) {
			if(t.getId() == id) {
				selectedId = i;
				break;
			}
			i++;
		}
		return selectedId;
	}

	private class PrintZReport implements Runnable{
		
		@Override
		public void run() {
			User supervisor;
			Db db = new Db();
			int supervisorId = 0;
			LoginDialog loginDialog = new LoginDialog(null);
			
			loginDialog.setVisible(true);
			
			supervisorId = Db.getUserId(loginDialog.getUsername());
			
			supervisor = db.loadUserInfo(supervisorId);
			
			printerChecker();
			
			if(db.testConnection()){
				if(isPrinterConnected){
					if(loginDialog.isSucceeded() && supervisor.canPrintReportX) {
						try {
							fiscalPrinter.printZReport();
						} catch (PrinterException e) {
							JOptionPane.showMessageDialog(null, "Error al imprimir el reporte Z","Error al imprimir", JOptionPane.ERROR_MESSAGE);
						}
					}else{
						JOptionPane.showMessageDialog(null, "Acceso no autorizado");
					}
				}
			}
			
		}
		
	}
	
	private class PrintXReport implements Runnable {

		@Override
		public void run() {
			User supervisor;
			Db db = new Db();
			int supervisorId = 0;
			boolean sentCmd = true;
			
			tree.setEnabled(false);
			
			LoginDialog loginDialog = new LoginDialog(null);
			
			loginDialog.setVisible(true);
			
			supervisorId = Db.getUserId(loginDialog.getUsername());
			
			supervisor = db.loadUserInfo(supervisorId);
			
			printerChecker();
			
			if(isPrinterConnected) {
				
			if(loginDialog.isSucceeded() && supervisor.canPrintReportX) {
				try {
					fiscalPrinter.printXReport();
				} catch (PrinterException e1) {
					sentCmd = false;
				}
				if(sentCmd) {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					boolean closed = Db.closeSummary((int) treeNode.getUserObject(), supervisorId);
					if(closed) {
						treeNode.removeFromParent();
						stationsWithSummary = Db.getStationsWithSummary();
						summaries = Db.loadSummaries();
						tree.setModel(new TreeDataModel(stationsWithSummary, summaries));
						summaryId = 0;
						summaryHasInvoice = false;
					}
				}else{
					JOptionPane.showMessageDialog(null, "Error al imprimir el reporte X","Error al imprimir", JOptionPane.ERROR_MESSAGE);
				}
			}else{
				JOptionPane.showMessageDialog(null, "Acceso no autorizado");
			}
			}else{
				JOptionPane.showMessageDialog(null, "Impresora desconectada", "Impresora desconectada", JOptionPane.ERROR_MESSAGE);
			}
			tree.setEnabled(true);
		}
		
	}
	
	private void printerChecker(){
		
		if(fiscalPrinter.CheckFprinter()){
			isPrinterConnected = true;
			menuItemConnect.setEnabled(false);
			menuItemDisconnect.setEnabled(true);
			menu.setForeground(Color.GREEN);
		}else{
			isPrinterConnected = false;
			menuItemConnect.setEnabled(true);
			menuItemDisconnect.setEnabled(false);
			menu.setForeground(Color.BLACK);
		}
	}

}// END OF class SoftParkMultiView