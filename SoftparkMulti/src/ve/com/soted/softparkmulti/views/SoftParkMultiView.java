package ve.com.soted.softparkmulti.views;

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
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.logging.log4j.*;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import jssc.SerialPortList;
import tfhka.PrinterException;
import tfhka.ve.S1PrinterData;
import tfhka.ve.Tfhka;
import ve.com.soted.softparkmulti.comm.CommPortUtils;
import ve.com.soted.softparkmulti.comm.GetNetworkAddress;
import ve.com.soted.softparkmulti.comm.OldRelayDriver;
import ve.com.soted.softparkmulti.comm.RelayDriver;
import ve.com.soted.softparkmulti.components.TfhkaPrinter;
import ve.com.soted.softparkmulti.components.TreeDataModel;
import ve.com.soted.softparkmulti.db.Db;
import ve.com.soted.softparkmulti.dialogs.LoginDialog;
import ve.com.soted.softparkmulti.objects.PayType;
import ve.com.soted.softparkmulti.objects.Station;
import ve.com.soted.softparkmulti.objects.Summary;
import ve.com.soted.softparkmulti.objects.Ticket;
import ve.com.soted.softparkmulti.objects.Transaction;
import ve.com.soted.softparkmulti.objects.User;
import ve.com.soted.softparkmulti.utils.Numbers;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("serial")
public class SoftParkMultiView extends JFrame {
	
	// Ticket example 2502201712000000100000000004
	// ddmmYYYYHHmmss + 3 digits station id + 11 digits ticket id
	// New ticket # is transaction id
	
	final private Tfhka fiscalPrinter;
	private boolean isPrinterConnected;
	private boolean shiftIsDown = false;
	
	private int summaryId = 0;
	private boolean summaryHasInvoice = false;
	private int invoiceCount = 0;	
	private User user;
	private int userId;
	private int stationId;
	private ArrayList<Summary> summaries;
	private ArrayList<Station> stationsWithSummary;
	private ArrayList<Transaction> transactions;
	private ArrayList<Transaction> transactionsOut;
	private ArrayList<Transaction> allTransactions;
	private ArrayList<PayType> payTypes;
	
	private Station stationInfo;

	private JMenuBar menuBar;
	private JMenu mainMenu;
	private JMenuItem menu;
	private JMenuItem menuItem;

	private JMenuItem menuItemConnect;
	private JMenuItem menuItemDisconnect;

	private JCheckBoxMenuItem cbMenuItemToolbar, cbMenuItemStatusbar;

	private JButton toolBarButtonCollect, toolBarButtonCutoff, toolBarButtonManualTicket, toolBarButtonLostTicket;
	
	private JButton buttonValetInvoice, buttonValetLostTicket;	
	private JButton buttonAccept, buttonCancel;

	private JPanel theToolBarPanel;
	private JPanel theStatusBarPanel;
	private JPanel personalPanel, companyPanel, middleContainer, wrapRightContainerPanel;

	private JCheckBox checkBillsName;
	private JToolBar toolBar;
	
	private JComboBox<String> comboCompany, comboShop,comboColor, comboModel, comboState, comboBrand;

	private JLabel labelStatus;
	
	private String activePort, relayPort="";

	@SuppressWarnings("unused")
	private JTextField textTicket, textPlate, textOwnerId, textOwnerName, textOwnerLastName, textDescription;
	private JTextField textExpiration,textDuration, textEntrance,textCashed,textChange;
	private JTextField textEntrancePlate;
	
	private JLabel labelPrice, labelMoney, labelParkingCounter;
	private JFormattedTextField textDateIn;
	
	private JTree tree;	
	private JButton buttonReloadReports;
	private JComboBox<String> comboCountry, comboDirectionState;
	private JButton buttonCollectAccept, buttonCollectCancel, buttonCarEntrance, buttonCollectExonerate;
	
	private int overnightDays;
	private Ticket ticketInfo;
	private Ticket lostTicket;
	public Timestamp entranceDateTime;
	
	private double transactionOutAmount;
	private boolean printing = false;
	
	private static final Logger log = LogManager.getLogger(SoftParkMultiView.class.getName());
	
	private Map<String, Long> logMap = new HashMap<String, Long>();
	
	public SoftParkMultiView(int stationId) {
		
		log.debug("Initializing");
		UIManager.getLookAndFeelDefaults().put("Button.font", new Font("Arial", Font.BOLD, 18));
		UIManager.getLookAndFeelDefaults().put("Label.font", new Font("Arial", Font.PLAIN, 16));
		UIManager.getLookAndFeelDefaults().put("TextField.font", new Font("Arial", Font.PLAIN, 18));
		UIManager.getLookAndFeelDefaults().put("FormattedTextField.font", new Font("Arial", Font.PLAIN, 18));
		
		fiscalPrinter = new tfhka.ve.Tfhka();
		
		LoginDialog loginDialog = new LoginDialog(this);
		
		loginDialog.setVisible(true);
		
		if(!loginDialog.isSucceeded()) {
			log.error("No success login details: user=" + loginDialog.getUsername() + ", ip=" + GetNetworkAddress.GetAddress("ip") + ", mac=" + GetNetworkAddress.GetAddress("mac"));
			System.exit(0);
		}
		log.debug("Login Successfull");
		
		userId = Db.getUserId(loginDialog.getUsername());
		
		Db db = new Db();
		
		user = db.loadUserInfo(userId);
		
		if(user == null) {
			JOptionPane.showMessageDialog(null, "Usuario invalido", "Usuario invalido", JOptionPane.ERROR_MESSAGE);
			log.fatal("Invalid user");
			System.exit(0);
		}
		
		log.debug("User Info: id=" + user.getId() + ", name=" + user.getName() + ", user type=" + user.getUserType());
		
		stationInfo = Station.getStationInfo(stationId);
		
		log.debug("Station Info: id=" + stationInfo.getId() + ", level=" + stationInfo.getLevelId() + ", name=" + stationInfo.getName() + ", type=" + stationInfo.getType().getName());
		
		allTransactions = Db.loadAllTransactions();
		
		transactions = new ArrayList<Transaction>();
		transactionsOut = new ArrayList<Transaction>();			//TODO check this

		payTypes = Db.loadPayTypes();
		
		summaryId = Db.getSummaryId(userId,stationId);
		
		if(summaryId > 0) {
			invoiceCount = db.countSummaryInvoices(summaryId);
			log.debug("There is an active summary with following details: id=" + summaryId + ", invoices=" + invoiceCount);
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

		this.setLayout(new BorderLayout(5, 5));

		// Create the box panel to wrap the menu and the toolbar
		JPanel toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.PAGE_AXIS));

		// Create the menu bar.
		this.setJMenuBar(createMenu());
		log.trace("Menu created");
		// Create the tool bar.
		toolBarPanel.add(createToolBar());
		log.trace("Toolbar created");

		JTabbedPane tabbedPane = new JTabbedPane();

		// Add a tab
		if(stationInfo.getType().getId() == 5){
			tabbedPane.addTab("Sistema de Cobro", createCashierTab());
			log.trace("Cashier Tab added");
		}
		else if(stationInfo.getType().getId() == 4){
			tabbedPane.addTab("Valet Parking", createValetTab());
		}
		else if(stationInfo.getType().getId() == 2){
			tabbedPane.addTab("Entrada", createEntranceTab());
		}

		this.add(toolBarPanel, BorderLayout.NORTH);

		this.add(tabbedPane, BorderLayout.CENTER);

		this.add(createStatusBar(), BorderLayout.SOUTH);
		
		log.trace("Status Bar added");

		cbMenuItemToolbar.setSelected(true);

		cbMenuItemStatusbar.setSelected(true);
		
		this.add(createReportTree(), BorderLayout.EAST);
		
		log.trace("Reports Tree added");
		
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
			
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				
				if(KeyEvent.KEY_PRESSED == e.getID()) {
//					JOptionPane.showMessageDialog(null, "KeyCode: " + e.getKeyCode() + " -- KeyChar: " + e.getKeyChar());
					int keyCode = e.getKeyCode();
					switch(keyCode) {
						case KeyEvent.VK_F2:
							log.trace("Key Event F2 ID=" + e.getID() + " keyCode=" + e.getKeyCode() + " Modifiers=" + e.getModifiers());
							if(buttonCarEntrance.isEnabled()) {
								log.trace("F2 key pressed");
								CheckInRun v = new CheckInRun("vehicle.in.key");
								Thread t = new Thread(v);
								t.setPriority(Thread.MAX_PRIORITY);
								t.start();
							}
							break;
						case KeyEvent.VK_F8:
							log.trace("F8 key pressed");
							preCheckOutLost();
							break;
					case KeyEvent.VK_F11:
							if(buttonCollectAccept.isEnabled()) {
								log.trace("Key Event F11");
								log.trace("Alternate key pressed int value = " + e.getModifiers());
								if(e.getModifiers() == KeyEvent.SHIFT_MASK) {
									log.trace("Shift is pressed");
									shiftIsDown = true;
								} else {
									shiftIsDown = false;
								}
								CheckOutRun out = new CheckOutRun(stationInfo.getType().getName());
								Thread t = new Thread(out);
								t.setPriority(Thread.MAX_PRIORITY);
								t.start();
							}
							break;
					}
				}
				return false;
			}
		});
		
		Properties prop = new Properties();
		InputStream propertiesInput;
		String printerPort;
		
		propertiesInput = getClass().getResourceAsStream("config.properties");
		try {
			prop.load(propertiesInput);
			printerPort = prop.getProperty("printer");
		} catch (IOException e1) {
			log.error("Error loading properties file to get printer port. COM1 will be used by default");
			printerPort = "COM1";
		}
		
		isPrinterConnected = fiscalPrinter.OpenFpctrl(printerPort);
		if(isPrinterConnected) {
			log.debug("Printer opened on " + printerPort + " port");
			menuItemConnect.setEnabled(false);
			menuItemDisconnect.setEnabled(true);
			menu.setForeground(Color.GREEN);
		} else {
			log.error("Cannot open printer port " + printerPort);
			menuItemConnect.setEnabled(true);
			menuItemDisconnect.setEnabled(false);
			menu.setForeground(Color.BLACK);
		}
		
		CheckPrinterTask taskCheckPrinter = new CheckPrinterTask();
		
		Timer timerCheckPrinter = new Timer(true);
		timerCheckPrinter.scheduleAtFixedRate(taskCheckPrinter, 3000, 5000);
		
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

		JPanel wrapContainerPanel = new JPanel();
		
		wrapContainerPanel.setLayout(new BoxLayout(wrapContainerPanel, BoxLayout.Y_AXIS));
		
		JPanel theTab = new JPanel();

		theTab.setLayout(new GridLayout(0, 3));
		
		theTab.add(createSubPanelCharge());
		
		theTab.add(createSubPanelMiddle());
		
		theTab.add(createSubPanelRight());

		wrapContainerPanel.add(theTab);
		
		return wrapContainerPanel;
	}

	private JPanel createEntranceTab(){
		
		JPanel wrapContainerPanel = new JPanel();
		
		wrapContainerPanel.setLayout(new BoxLayout(wrapContainerPanel, BoxLayout.Y_AXIS));
		
		JPanel theTab = new JPanel();

		theTab.setLayout(new GridLayout(0, 2));		
		
		theTab.add(createSubPanelEntrance());

		wrapContainerPanel.add(theTab);
		
		return wrapContainerPanel;
	}
	
	private JPanel createSubPanelEntrance(){
		
		JPanel wrapEntrancePanel = new JPanel();
		wrapEntrancePanel.setLayout(new BoxLayout(wrapEntrancePanel, BoxLayout.X_AXIS));
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS)); // top to bottom

		JPanel entrancePanel = new JPanel();
		entrancePanel.setLayout(new BoxLayout(entrancePanel, BoxLayout.Y_AXIS));		

		JPanel entranceTitlePanel = new JPanel();
		
		JLabel labelEntranceTitle = new JLabel("Ingreso de Vehículos");
		labelEntranceTitle.setFont(new Font("Arial", Font.BOLD, 20));
		entranceTitlePanel.add(labelEntranceTitle);
		
		entrancePanel.add(entranceTitlePanel);
		
//		JPanel entrancePlatePanel = new JPanel();
//		
//		JLabel labelEntrancePlate = new JLabel("Placa:");
//		entrancePlatePanel.add(labelEntrancePlate);
//		textEntrancePlate = new JTextField(12);
//		entrancePlatePanel.add(textEntrancePlate);		
//		entrancePanel.add(entrancePlatePanel);
//		
		JPanel entranceButtonPanel = new JPanel();
		
		ButtonListener lForSwitchButton = new ButtonListener();
		
		buttonCarEntrance = new JButton("Ingresar (F2)");
		buttonCarEntrance.setPreferredSize(getPreferredSize());
		buttonCarEntrance.setActionCommand("entrance.vehicle.in.button");
		buttonCarEntrance.addActionListener(lForSwitchButton);
		
		entranceButtonPanel.add(buttonCarEntrance);
		
		entrancePanel.add(entranceButtonPanel);
				
		if(Db.getLevelPlaces(stationInfo.getLevelId()) > -1) {
			JPanel parkingSpacesPanel = new JPanel();
			
			int availablePlaces = Db.getAvailablePlaces(stationInfo.getLevelId());
			
			labelParkingCounter = new JLabel("Puestos Disponibles: " + String.valueOf(availablePlaces));
			parkingSpacesPanel.add(labelParkingCounter);		
			
			CheckPlacesTask taskCheckPlaces = new CheckPlacesTask();
			
			Timer timerCheckPlaces = new Timer(true);
			timerCheckPlaces.scheduleAtFixedRate(taskCheckPlaces, 1000, 3000);
			
			entrancePanel.add(parkingSpacesPanel);		
		}
		
//		JLabel labelReturnMessage = new JLabel("");	
		JLabel labelReturnMessage = new JLabel("ESPERE, NO HAY PUESTOS DISPONIBLES");	//Label set to show warning message to the user indicating parking spaces availability
		labelReturnMessage.setFont(new Font("Arial", Font.BOLD, 18));
		labelReturnMessage.setForeground(Color.RED);
		entrancePanel.add(labelReturnMessage);
		
		container.add(Box.createVerticalStrut(40));
		container.add(entrancePanel);		
		container.add(Box.createVerticalStrut(70));
				
		wrapEntrancePanel.add(container);
		wrapEntrancePanel.add(Box.createHorizontalStrut(40));
		
		return wrapEntrancePanel;
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
		
		TextFieldListener lForText	= new TextFieldListener();
		
		//added a title to the ticket data
		JLabel labelTitle = new JLabel("Datos del Ticket");
		labelTitle.setFont(new Font(null, Font.BOLD, 20));
		thePanel.add(labelTitle);
		
		JLabel labelTicket = new JLabel("Ticket No.:");
		thePanel.add(labelTicket);
		textTicket = new JTextField(14);
		textTicket.setActionCommand("multi.text.type");
		textTicket.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				Component oppositeComponent = e.getOppositeComponent();
				if(oppositeComponent instanceof JTextField) {
					JTextField textField = (JTextField) oppositeComponent;
					if(!textField.equals(textEntrancePlate)) {
						if(textTicket.isEnabled()) {
							textTicket.requestFocus();
						}
					}
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		textTicket.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
		textTicket.addKeyListener(lForText);
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
		textDateIn = new JFormattedTextField(mask);
		textDateIn.setEditable(false);
		textDateIn.setColumns(14);
		thePanel.add(textDateIn);

		JLabel labelEntrance = new JLabel("Entrada:");
		thePanel.add(labelEntrance);
		textEntrance = new JTextField(14);
		textEntrance.setEditable(false);
		thePanel.add(textEntrance);

		JLabel labelDuration = new JLabel("Duracion:");
		thePanel.add(labelDuration);
		textDuration = new JTextField(14);
		textDuration.setEditable(false);
		thePanel.add(textDuration);

		JLabel labelExpiration = new JLabel("Expiracion:");
		thePanel.add(labelExpiration);
		textExpiration = new JTextField(14);
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
	    
		picturePanel.setLayout(new FlowLayout() );		
		
		ImageIcon image = new ImageIcon("resources/image404.png");
		JLabel labelPicture = new JLabel(image);	
		labelPicture.setAlignmentY(TOP_ALIGNMENT);		
		picturePanel.add(labelPicture);		
		picturePanel.setMaximumSize(getMinimumSize());
		container.add(picturePanel);
		
		//payment panel
		GroupLayout payment = new GroupLayout(paymentPanel);
		paymentPanel.setLayout(payment);
		
		payment.setAutoCreateGaps(true);
		payment.setAutoCreateContainerGaps(true);
		
		JLabel labelPayment = new JLabel("Pago");
		labelPayment.setFont(new Font(null, Font.BOLD, 20));
		paymentPanel.add(labelPayment);
		
		JLabel labelTotal = new JLabel("Total");
		labelTotal.setFont(new Font(null, Font.BOLD, 18));
		paymentPanel.add(labelTotal);
		
		labelMoney = new JLabel("Bs.");
		Font labelFont = labelMoney.getFont();
		labelMoney.setFont(new Font(labelFont.getFontName(), Font.PLAIN, 36));
		labelMoney.setForeground(Color.RED);
		paymentPanel.add(labelMoney);
		
		JLabel labelCashed = new JLabel("Entregado");
		paymentPanel.add(labelCashed);
		textCashed = new JTextField(14);
		textCashed.setEnabled(false);
		textCashed.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				JTextField textCashed = (JTextField) e.getSource();
				String cashed = textCashed.getText();
				if(!cashed.isEmpty() && Numbers.isNumeric(cashed)) {
					double changeAmount = Double.valueOf(cashed) - transactionOutAmount;
					if(changeAmount < 0) {
						textChange.setText("Faltan " + changeAmount + " BsF");
					} else {
						textChange.setText("Devolver " + changeAmount + " BsF");
					}
				} else {
					textChange.setText("");
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
		paymentPanel.add(textCashed);
		
		JLabel labelChange = new JLabel("Vuelto");
		paymentPanel.add(labelChange);
		textChange = new JTextField(14);
		textChange.setEnabled(false);
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
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		ButtonListener lForButton = new ButtonListener();
		
		buttonCollectAccept = new JButton("Aceptar (F11)");
		buttonCollectAccept.setActionCommand("multi.accept.button");
		buttonCollectAccept.addActionListener(lForButton);
		buttonCollectAccept.setEnabled(false);
		
		buttonPanel.add(buttonCollectAccept);
		
		buttonCollectCancel = new JButton("Cancelar");
		buttonCollectCancel.setActionCommand("multi.cancel.button");
		buttonCollectCancel.addActionListener(lForButton);
		buttonCollectCancel.setEnabled(false);
		
		buttonPanel.add(buttonCollectCancel);	
		
		container.add(buttonPanel);
		
		return container;
	}

	private JPanel createSubPanelRight() {
		// 
		wrapRightContainerPanel = new JPanel();
		
		wrapRightContainerPanel.setLayout(new BoxLayout(wrapRightContainerPanel, BoxLayout.X_AXIS));
		
		JPanel container = new JPanel();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
				
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
		comboDirectionState.removeItem("");
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
		
		directionsPanel.setVisible(false);
		container.add(directionsPanel);		
		
		//create here new panel for the entrance		
		JPanel entrancePanel = new JPanel();
		
		entrancePanel.setLayout(new BoxLayout(entrancePanel, BoxLayout.Y_AXIS));		
//		JPanel entrancePanel = new JPanel(new BorderLayout(20,20));
		
		JPanel entranceTitlePanel = new JPanel();
		
		JLabel labelEntranceTitle = new JLabel("Ingreso de Vehículos");
		labelEntranceTitle.setFont(new Font(null, Font.BOLD, 20));
		entranceTitlePanel.add(labelEntranceTitle);
		
		entrancePanel.add(entranceTitlePanel);
		
		JPanel entrancePlatePanel = new JPanel();
		
		JLabel labelEntrancePlate = new JLabel("Placa:");
		entrancePlatePanel.add(labelEntrancePlate);
		textEntrancePlate = new JTextField(12);
		entrancePlatePanel.add(textEntrancePlate);
		
		entrancePanel.add(entrancePlatePanel);
		
		JPanel entranceButtonPanel = new JPanel();
		
		ButtonListener lForSwitchButton = new ButtonListener();
		
		buttonCarEntrance = new JButton("Ingresar");
		buttonCarEntrance.setPreferredSize(getPreferredSize());
		buttonCarEntrance.setActionCommand("vehicle.in.button");
		buttonCarEntrance.addActionListener(lForSwitchButton);
		
		entranceButtonPanel.add(buttonCarEntrance);
		
		entrancePanel.add(entranceButtonPanel);
		
		
		
		if(Db.getLevelPlaces(stationInfo.getLevelId()) > -1) {
			JPanel parkingSpacesPanel = new JPanel();
			
			int availablePlaces = Db.getAvailablePlaces(stationInfo.getLevelId());
			
			labelParkingCounter = new JLabel("Puestos Disponibles: " + String.valueOf(availablePlaces));
			labelParkingCounter.setFont(new Font(null, Font.BOLD, 14));
			parkingSpacesPanel.add(labelParkingCounter);		
			
			CheckPlacesTask taskCheckPlaces = new CheckPlacesTask();
			
			Timer timerCheckPlaces = new Timer(true);
			timerCheckPlaces.scheduleAtFixedRate(taskCheckPlaces, 1000, 3000);
			
			entrancePanel.add(parkingSpacesPanel);
		
		}
		
		container.add(Box.createVerticalStrut(20));
		container.add(entrancePanel);		
		container.add(Box.createVerticalStrut(20));
		
		JPanel exoneratePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		buttonCollectExonerate = new JButton("Exonerar");
		buttonCollectExonerate.setActionCommand("multi.exonerate.button");
		buttonCollectExonerate.addActionListener(lForSwitchButton);
		buttonCollectExonerate.setEnabled(false);		
		exoneratePanel.add(buttonCollectExonerate);
		
		container.add(exoneratePanel); 		
		container.add(Box.createVerticalStrut(10));
		
		wrapRightContainerPanel.add(container);
		wrapRightContainerPanel.add(Box.createHorizontalStrut(50));
		
		return wrapRightContainerPanel;
	}

	private JPanel createSubPanelMiddle() {
		middleContainer = new JPanel();
		
		middleContainer.setLayout(new BoxLayout(middleContainer, BoxLayout.Y_AXIS));
		
		JPanel topPanel = new JPanel();
		
		CheckBoxListener lForCheckBox = new CheckBoxListener();		

		checkBillsName = new JCheckBox(" Facturar a nombre de Empresa",false);
		checkBillsName.addItemListener(lForCheckBox);
		topPanel.add(checkBillsName);
		topPanel.setMaximumSize(getMinimumSize());

		
		middleContainer.add(topPanel);
		
		if(checkBillsName.isSelected()){
			companyPanel = companyPanel();
			middleContainer.add(companyPanel);
		}
		else{
			personalPanel = personalPanel();
			middleContainer.add(personalPanel);
		}
		//INSERT HERE CAR´S DATA PANEL
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
		
		middleContainer.add(carsDataPanel);
		middleContainer.setVisible(false);
		//
		return middleContainer;
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
		labelTitle.setFont(new Font(null, Font.BOLD, 20));
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
						.addComponent(textId, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textFirstName, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textLastName, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textEmail, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(textMobilePhone, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
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
		
//		personalPanel.setAlignmentX(LEFT_ALIGNMENT);
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
		labelTitleCompany.setFont(new Font(null, Font.BOLD, 20));
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
		
		ToolbarButtonListener lForToolbarButton = new ToolbarButtonListener();

		toolBar = new JToolBar("Barra de Herramientas");

		toolBarButtonCollect = new JButton("Sistema de Cobro", new ImageIcon("resources/cash_register.png"));
		
		toolBarButtonCollect.setActionCommand("bar.button.collet");
		
		toolBarButtonCollect.addActionListener(lForToolbarButton);

		toolBar.add(toolBarButtonCollect);

		toolBar.addSeparator();
		
		toolBarButtonManualTicket = new JButton("Ticket Manual", new ImageIcon("resources/new_ticket.png"));
		
		toolBarButtonManualTicket.setActionCommand("bar.button.manual.ticket");
		
		toolBarButtonManualTicket.addActionListener(lForToolbarButton);

		toolBar.add(toolBarButtonManualTicket);

		toolBar.addSeparator();

		toolBarButtonLostTicket = new JButton("Ticket Perdido (F8)", new ImageIcon("resources/lost_ticket.png"));

		toolBarButtonLostTicket.setActionCommand("bar.button.lost.ticket");
		
		toolBarButtonLostTicket.addActionListener(lForToolbarButton);
		
		toolBar.add(toolBarButtonLostTicket);

		toolBar.addSeparator();
		
		toolBarButtonCutoff = new JButton("Cierres", new ImageIcon("resources/lock.png"));
		
		toolBarButtonCutoff.setActionCommand("bar.button.cut.off");
		
		toolBarButtonCutoff.addActionListener(lForToolbarButton);

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
			t.setPriority(Thread.MAX_PRIORITY);
			t.start();
		}
		
	}
	
	private class MouseClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			DefaultMutableTreeNode reportNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if(null != reportNode) {
//				JOptionPane.showMessageDialog(null, "Report Node Level: " + reportNode.getLevel());
				if(SwingUtilities.isRightMouseButton(e) && reportNode.getUserObject() != null && reportNode.getLevel() == 3) {
					DefaultMutableTreeNode stationNode = new DefaultMutableTreeNode(reportNode.getParent().getParent());
					if(stationNode.getUserObject().toString().equalsIgnoreCase(stationInfo.getName())) {
						PopUpMenu popUpMenu = new PopUpMenu();
						popUpMenu.show(e.getComponent(), e.getX(), e.getY());
					}
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
	
	private class TextFieldListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent evt) {
			int keyCode = evt.getKeyCode();
			
			if(keyCode == KeyEvent.VK_ENTER) {
				preCheckOut();
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {

		}

		

	}
	
	private class ButtonListener implements ActionListener, KeyListener {

		@Override
		public void keyPressed(KeyEvent ev) {
			//SelectValetRun v = new SelectValetRun(KeyEvent.getKeyText(ev.getKeyCode()));
			//new Thread(v).start();
			
			//CheckOutRun v1 = new CheckOutRun(KeyEvent.getKeyText(ev.getKeyCode()));
			//Thread t = new Thread(v1);
			//t.setPriority(Thread.MAX_PRIORITY);
			//t.start();
			//new Thread(v1).start();
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
			} else {
				log.trace("Alternate key pressed int value = " + ev.getModifiers());
				if(ev.getModifiers() == KeyEvent.SHIFT_MASK) {
					shiftIsDown = true;
				} else {
					shiftIsDown = false;
				}
				if ((ev.getActionCommand().equalsIgnoreCase("vehicle.in.Button")) || (ev.getActionCommand().equalsIgnoreCase("entrance.vehicle.in.button")) ){
					CheckInRun v = new CheckInRun(ev.getActionCommand());
					Thread t = new Thread(v);
					t.setPriority(Thread.MAX_PRIORITY);
					t.start();
					//new Thread(v).start();
				}
				else if (ev.getActionCommand().equalsIgnoreCase("multi.accept.button"))  {				
					CheckOutRun out = new CheckOutRun(stationInfo.getType().getName());
					Thread t = new Thread(out);
					t.setPriority(Thread.MAX_PRIORITY);
					t.start();
					//new Thread(out).start();
				}
				else if (ev.getActionCommand().equalsIgnoreCase("multi.exonerate.button")){					
					int userResponse = JOptionPane.showConfirmDialog(null, "¿Desea exonerar este ticket?", "Confirme exoneracion de un ticket", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);					
					if(userResponse == JOptionPane.YES_OPTION) {
						CheckOutRun out = new CheckOutRun(stationInfo.getType().getName(), true);
						Thread t = new Thread(out);
						t.setPriority(Thread.MAX_PRIORITY);
						t.start();
						//new Thread(out).start();
					}
				}
				else if (ev.getActionCommand().equalsIgnoreCase("multi.cancel.button")) {
					textTicket.setEnabled(true);
					textTicket.setText("");
					textDateIn.setText("");
					textEntrance.setText("");
					textDuration.setText("");
					textExpiration.setText("");
					textCashed.setEnabled(false);
					textCashed.setText("");
					textChange.setText("");
					labelMoney.setText(" Bs.");
					buttonCarEntrance.setEnabled(true);
					textEntrancePlate.setEnabled(true);
					buttonCollectAccept.setEnabled(false);
					buttonCollectCancel.setEnabled(false);
					buttonCollectExonerate.setEnabled(false);
				}
//				else if (ev.getActionCommand().equalsIgnoreCase("entrance.vehicle.in.button")) {
//					CheckInRun v = new CheckInRun(ev.getActionCommand());
//					new Thread(v).start();
////					textEntrancePlate.setText("");
//				}
				
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
					Thread t = new Thread(o);
					t.setPriority(Thread.MAX_PRIORITY);
					t.start();
					//new Thread(o).start();
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
					Thread t = new Thread(o);
					t.setPriority(Thread.MAX_PRIORITY);
					t.start();
					//new Thread(o).start();
				} else {
					labelStatus.setText("No hay puerto COM activo");
				}
				break;
			case "disconnect":
				CloseCommPortRun c = new CloseCommPortRun();
				Thread t = new Thread(c);
				t.setPriority(Thread.MAX_PRIORITY);
				t.start();
				break;
			case "test":
				labelStatus.setText("Enviando prueba al puerto " + activePort);
				try {
					@SuppressWarnings("unused")
					boolean sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.printTest());
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
				t.setPriority(Thread.MAX_PRIORITY);
				t.start();
				break;
			case "aboutus":
				new AboutUs();
				break;
			}// END OF switch

		}// END OF method ActionPerformed

	}// END OF class MenuItemListener
	
	private class ToolbarButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			
			if(e.getActionCommand().equalsIgnoreCase("bar.button.collet")) {
				if(createSubPanelCharge().isVisible()){
					createSubPanelCharge();
				}
			}else if(e.getActionCommand().equalsIgnoreCase("bar.button.manual.ticket")) {
				createSubPanelMiddle();
			}else if(e.getActionCommand().equalsIgnoreCase("bar.button.lost.ticket")) {
				if 	(!createSubPanelMiddle().isVisible()){
					preCheckOutLost();
				}					

			}else if(e.getActionCommand().equalsIgnoreCase("bar.button.cut.off")) {
//				if(boardsFrame.isClosed()){
//					createBoardsFrame();
//				}
//			}else if(e.getActionCommand().equalsIgnoreCase("bar.button.budgets")) {
//				if(budgetsFrame.isClosed()){
//					createBudgetsFrame();
//				}
//			}else if(e.getActionCommand().equalsIgnoreCase("bar.button.starters")) {
//				if(startersFrame.isClosed()){
//					createStartersFrame();
//				}
//			}else if(e.getActionCommand().equalsIgnoreCase("bar.button.tracing")) {
//				if(tracingFrame.isClosed()){
//					createTracingFrame();
//				}
			}
		}
		
	}
	
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
					SwingUtilities.invokeLater(new Runnable(){
					     @Override
					     public void run() {
					      middleContainer.remove(personalPanel);
					      middleContainer.validate();
					      middleContainer.repaint();
					      personalPanel.setVisible(false);
					      
					      companyPanel.setVisible(true);
					      middleContainer.add(companyPanel);
					      middleContainer.validate();
					      middleContainer.repaint();
					      
					      middleContainer.getParent().validate();
					      middleContainer.getParent().repaint();
					     }
					    });
		        } else {//checkbox has been deselected
		        	SwingUtilities.invokeLater(new Runnable(){
					     @Override
					     public void run() {
					      middleContainer.remove(companyPanel);
					      middleContainer.validate();
					      middleContainer.repaint();
					      companyPanel.setVisible(false);
					      
					      personalPanel.setVisible(true);
					      middleContainer.add(personalPanel);
					      middleContainer.validate();
					      middleContainer.repaint();
					      
					      middleContainer.getParent().validate();
					      middleContainer.getParent().repaint();
					     }
					    });
		        };
			}// END OF else if

		}// END OF method itemStateChanged

	}// END OF class CheckBoxListener
	
	@SuppressWarnings("unused")
	private class SelectValetRun implements Runnable {

		String actionCommand;
		
		SelectValetRun(String actionCommand) {
			this.actionCommand = actionCommand;
		}
		
		@Override
		public synchronized void run() {
			if(stationInfo.getType().getName().equals("Valet")){
				if(transactions.size() > 0) {
					switch(actionCommand){
					case "valet-invoice":
						int transactionIndex = transactionSelected(transactions, allTransactions.get(0).getId());
						if(transactionIndex > -1) {
							transactions.remove(transactionIndex);
							buttonValetInvoice.setEnabled(true);
						}else{
							transactions.add(allTransactions.get(0));
							buttonValetInvoice.setEnabled(false);
							labelPrice.setText(String.valueOf(getSubTotal(transactions)));
						}
						break;
					case "valet-lost":
						transactions.add(allTransactions.get(1));
						buttonValetLostTicket.setEnabled(false);
						labelPrice.setText(String.valueOf(getSubTotal(transactions)));
						break;
					}
				}else{
					switch(actionCommand){
					case "valet-invoice":
						transactions.add(allTransactions.get(0));
						buttonValetInvoice.setEnabled(false);
						labelPrice.setText(String.valueOf(getSubTotal(transactions)));
						enableButtons();
						break;
					case "valet-lost":
						transactions.add(allTransactions.get(0));
						transactions.add(allTransactions.get(1));
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
					textTicket.setEnabled(false);
//					CheckOutRun co = new CheckOutRun(transactions);
//					Thread t = new Thread(co);
//					t.start();
				}
			}			
		}		
	}
	
	public String Relay() {
		Properties prop = new Properties();
		InputStream propertiesInput;
//		String relayPort = "";
		try{
			propertiesInput = getClass().getResourceAsStream("relay.properties");
			// load a properties file
			prop.load(propertiesInput);
			relayPort = prop.getProperty("relayPort");
			
			}catch(IOException ex){
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
		return relayPort;
	}
	
	private class CheckInRun implements Runnable{

		String actionCommand;
		boolean isPlateIn = false;
		
		CheckInRun(String actionCommand) {
			this.actionCommand = actionCommand;
		}
		@Override
		public void run() {
			tStart("checkinrun");
			log.debug("Starting CheckIn");
			printerChecker();
			if(isPrinterConnected){
				log.debug("Printer is connected");
				if (!printing){
					log.debug("No other printing job, starting to process print");
					printing = true;
					if (stationInfo.getType().getName().equals("Entrada/Salida")){
						log.debug("Process entrance ticket as E/S station");
						log.debug("actionCommand="+ actionCommand);
						if(actionCommand.equalsIgnoreCase("vehicle.in.button") || actionCommand.equalsIgnoreCase("vehicle.in.key")) {
							if(Db.getAvailablePlaces(stationInfo.getLevelId()) > 0) {
							//TODO Have to check the presence of the vehicle to allow print the ticket
								Db db = new Db();
								String plate = textEntrancePlate.getText();
								String regex = Db.getConfig("plate_regex", "plate");
								Pattern pattern = Pattern.compile(regex);
								Matcher match = pattern.matcher(plate);
								isPlateIn = Db.isPlateIn(plate);
								
								if (plate.isEmpty()){
									JOptionPane.showMessageDialog(null, "El Número de Placa no puede estar vacío", "Número de placa invalido", JOptionPane.WARNING_MESSAGE);
									textEntrancePlate.setText("");
									textEntrancePlate.requestFocus();
								} else if (!match.find()) {
									log.error("Erroneus plate #" + plate);
									JOptionPane.showMessageDialog(null, "Por favor ingrese un numero de placa valido", "Numero de placa invalido", JOptionPane.ERROR_MESSAGE);
									textEntrancePlate.setText("");
									textEntrancePlate.requestFocus();
								} else if (isPlateIn){
									JOptionPane.showMessageDialog(null, "El Número de Placa ya se encuentra ingresado", "Numero de placa invalido", JOptionPane.ERROR_MESSAGE);
									log.info("Plate #" + plate + " is already in");
									textEntrancePlate.setText("");
									textEntrancePlate.requestFocus();
								} else {
									@SuppressWarnings("unused")
									boolean sentCmd = false;
									int transactionId = db.preTransactionIn(stationInfo.getId(),plate);
									
									if(user.getLogin().equalsIgnoreCase("test")) {
										labelStatus.setText("Imprimiendo ticket de entrada");
									} else {
										tStart("printing");
										tStart("printingTicketNo");
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Ticket #: " + transactionId));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										log.debug(tEnd("printingTicketNo"));
//										tStart("getDateTimeFormat");
//										DateTime entranceDateTime = new DateTime(Db.getDbTime());
//										DateTimeFormatter tFormatter = DateTimeFormat.forPattern("HH:mm:ss");
//										DateTimeFormatter dFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
//										DateTimeFormatter tFormatter2 = DateTimeFormat.forPattern("HHmmss");
//										DateTimeFormatter dFormatter2 = DateTimeFormat.forPattern("ddMMyyyy");
//										log.debug(tEnd("getDateTimeFormat"));
//										tStart("printingTime");
//										try {
//											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Hora: " + entranceDateTime.toString(tFormatter)));
//										} catch (PrinterException ce) {
//											ce.printStackTrace();
//										}
//										log.debug(tEnd("printingTime"));
//										tStart("printingDate");
//										try {
//											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Fecha: " + entranceDateTime.toString(dFormatter)));
//										} catch (PrinterException ce) {
//											ce.printStackTrace();
//										}
//										log.debug(tEnd("printingDate"));
										tStart("printingEntryStation");
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Entrada: " + stationInfo.getId() + " " + stationInfo.getName()));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										log.debug(tEnd("printingEntryStation"));
//										tStart("printingCashierName");
//										try {
//											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Cajero: " + user.getName()));
//										} catch (PrinterException ce) {
//											ce.printStackTrace();
//										}
//										log.debug(tEnd("printingCashierName"));
										tStart("printingPlate");
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentEnd("Placa: " + plate));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										log.debug(tEnd("printingPlate"));
//										tStart("printingBarcode");
//										try {
//											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.setBarcode(entranceDateTime.toString(dFormatter2) + entranceDateTime.toString(tFormatter2) + StringTools.fillWithZeros(stationId, 3) + StringTools.fillWithZeros(transactionId,11)));
//										} catch (PrinterException ce) {
//											ce.printStackTrace();
//										}
//										log.debug(tEnd("printingBarcode"));
//										tStart("printingEndOfDocument");
//										try {
//											fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentEnd(Db.getConfig("client_name", "platform")));
//										} catch (PrinterException ce) {
//											ce.printStackTrace();
//										}
//										log.debug(tEnd("printingEndOfDocument"));
										log.debug(tEnd("printing"));
									}
									textEntrancePlate.setText("");
									
									//Check the second  entrance sensor if the state is inactive then send the INACTIVE_STATE<
									labelParkingCounter.setText("Puestos Disponibles: " + Db.getAvailablePlaces(stationInfo.getLevelId()));
								}//END of plate checkup
							
							} else {
								JOptionPane.showMessageDialog(null, "No hay puestos disponibles en este momento");
								labelParkingCounter.setText("Puestos Disponibles: " + Db.getAvailablePlaces(stationInfo.getLevelId()));
							}
							textEntrancePlate.setText("");
						}
					}//end of station mode= E/S
					printing = false;
				} else {
					JOptionPane.showMessageDialog(null, "Por favor espere, se esta imprimiendo el Ticket anterior");
				}
			} else {
				log.error("Printer disconnected. Could not print entrance ticket");
				JOptionPane.showMessageDialog(null, "La impresora esta desconectada", "Impresora desconectada", JOptionPane.ERROR_MESSAGE);
			}
			
			log.debug(tEnd("checkinrun"));
		}
	}
	
 	private void preCheckOut (){
		
		String ticketCode = "";
		boolean isTicketOut = true;
		boolean isTicketIn = false;
		
		try{
			ticketCode = textTicket.getText();
			textTicket.setEnabled(false);
			
			if(ticketCode.length() < 11 && ticketCode.length() > 0) {
//				ticketNumber = Integer.parseInt(ticketCode.substring(17));
				ticketInfo = Db.getTicketInfo(ticketCode);
				isTicketIn = Db.isTicketIn(ticketInfo.getId());
				if(isTicketIn) {
					isTicketOut = Db.isTicketOut(ticketInfo.getId());
					if (!isTicketOut){
						buttonCarEntrance.setEnabled(false);
						textEntrancePlate.setEnabled(false);
						buttonCollectAccept.setEnabled(true);
						buttonCollectCancel.setEnabled(true);
						textCashed.setEnabled(true);
						textChange.setEnabled(false);
						buttonCollectExonerate.setEnabled(true);
//							String day = ticketCode.substring(0,2);
//							String month = ticketCode.substring(2,4);
//							String year = ticketCode.substring(4,8);
//							String hour = ticketCode.substring(8,10);
//							String minutes = ticketCode.substring(10,12);
//							String seconds = ticketCode.substring(12,14);	
						
//							DateTime dtIn = new DateTime(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minutes), Integer.parseInt(seconds), 0);
						DateTime dtIn = new DateTime(ticketInfo.getEntranceDateTime());
						DateTimeFormatter dFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
						DateTimeFormatter tFormatter = DateTimeFormat.forPattern("HH:mm:ss");
						
						DateTime dtOut = new DateTime(Db.getDbTime());
						DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
						
						Period periodStayedIn = new Period(dtIn, dtOut);
						Db db = new Db();
						
						textDateIn.setText(dFormatter.print(dtIn) + " " + tFormatter.print(dtIn));
//							textDateIn.setText(day + month + year + hour + minutes + seconds);		//fecha y hora de entrada
						textEntrance.setText(String.valueOf(ticketInfo.getEntranceStationId()));	//estacion de entrada								
						int ticketTimeout = Integer.parseInt(Db.getConfig("ticket_timeout", "time"));
						textExpiration.setText(dtf.print(dtOut.plusMinutes(ticketTimeout)));
						int overnightType = Integer.parseInt((Db.getConfig("overnight_type", "billing")));
						if (dtIn.isBefore(dtOut)){
							//Check the overnight_type in the configs table from the DB
							if (overnightType == 0){
								//If the value = 0 then the charge will be by hours
								int hoursLapse = Hours.hoursBetween(dtIn, dtOut).getHours();
								DecimalFormat df = new DecimalFormat("00");
								String durationMinutes = df.format(periodStayedIn.getMinutes());
								String durationSeconds = df.format(periodStayedIn.getSeconds());														
								String durationTime = hoursLapse + ":" + durationMinutes+ ":" +durationSeconds;							
								textDuration.setText(durationTime);
								int spentMinutes = periodStayedIn.getMinutes();
								// TODO check if everything is working well here
								if ( spentMinutes > 29){
									transactionOutAmount = db.getHourRateByHours(hoursLapse + 1);
									labelMoney.setText(String.valueOf(transactionOutAmount) + " Bs.");
								} else {
									transactionOutAmount = db.getFractionRateByHours(hoursLapse);								
									labelMoney.setText(String.valueOf(transactionOutAmount) + "Bs.");
								}
								
							} else if (overnightType == 1){
								//If the value = 1 then the charge will be by night (overnights)
								int overnightOffset = Integer.parseInt(Db.getConfig("overnight_time", "time"));
								DateTime dtInOffset = dtIn.minusHours(overnightOffset);
								DateTime dtOutOffset = dtOut.minusHours(overnightOffset);
								int daysBetween = Days.daysBetween(dtInOffset, dtOutOffset).getDays();
								overnightDays = daysBetween;
								
								if (overnightDays > 0){
									JOptionPane.showMessageDialog(null, "Vehiculo con pernocta desde hace " + overnightDays + " dias", "Atención", JOptionPane.WARNING_MESSAGE);  //Add the exit hour to this message
									textDuration.setText(String.valueOf(daysBetween + " días "));
									transactionOutAmount = (db.getOvernightRates("ticket_pernocta") * overnightDays);
									labelMoney.setText(String.valueOf(transactionOutAmount) + " Bs.");
								} else {
									DecimalFormat df = new DecimalFormat("00");
									String durationHours = df.format(periodStayedIn.getHours());
									String durationMinutes = df.format(periodStayedIn.getMinutes());
									String durationSeconds = df.format(periodStayedIn.getSeconds());														
									String durationTime = durationHours + ":" + durationMinutes+ ":" +durationSeconds;							
									textDuration.setText(durationTime);

									int spentMinutes = periodStayedIn.getMinutes();
									int spentHours = periodStayedIn.getHours();
									if ( spentMinutes > 29){
										transactionOutAmount = db.getHourRates(spentHours + 1);
										labelMoney.setText(String.valueOf(transactionOutAmount) + " Bs.");
									}
									else{
										transactionOutAmount = db.getFractionRates(spentHours);								
										labelMoney.setText(String.valueOf(transactionOutAmount) + "Bs.");
									}								
								}								
							}	
							
						} else {
							JOptionPane.showMessageDialog(null, "La hora de ticket inválida", "Atención", JOptionPane.WARNING_MESSAGE);  //Add the exit hour to this message
						}
					} else {
						JOptionPane.showMessageDialog(null, "Ticket con salida", "Ticket  Procesado", JOptionPane.WARNING_MESSAGE);  //Add the exit hour to this message
						textTicket.setText("");
					}			
				} else {
					JOptionPane.showMessageDialog(null, "El Ticket  no ha sido generado, inserte el numero correcto","Ticket procesado", JOptionPane.ERROR_MESSAGE);
					textTicket.setText("");
				}
			} else {
				JOptionPane.showMessageDialog(null, "Numero de ticket invalido","Numero de ticket invalido", JOptionPane.ERROR_MESSAGE);
			}
			} catch(NumberFormatException ne) {
				JOptionPane.showMessageDialog(null, "Introduzca un numero de ticket valido", "Numero de ticket invalido", JOptionPane.WARNING_MESSAGE);
				textTicket.setText("");
			}		
	}
	
	public void tStart(String key) {
		logMap.put(key, new DateTime().getMillis());
	}
	
	public String tEnd(String key) {
		Long dt = new DateTime().getMillis();
		Long total = dt - logMap.get(key);
		return key + " elapsed " + total + " ms = " + (total / 1000) + " sec";
	}
	
	private class CheckOutRun implements Runnable {
		
		String stationMode;
		boolean exonerate;

		S1PrinterData statusS1;
//		S2PrinterData statusS2;
		@SuppressWarnings("unused")
		boolean sentCmd = false;
		
		public CheckOutRun(String stationMode) {
			this(stationMode,false);
		}
		
		public CheckOutRun(String stationMode, boolean exonerate) {
			this.stationMode = stationMode;
			this.exonerate = exonerate;
		}

		@SuppressWarnings("unused")
		private void log(String arg0) {
			
			Path file = Paths.get("log.txt");
			ArrayList<String> logLines = new ArrayList<String>();
			logLines.add(arg0);
			try {
				Files.write(file, logLines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		@Override
		public synchronized void run() {
			Db db = new Db();
			int insertedSummaryId = 0;
			boolean isTicketProcessed = false;
			
			//this.log("Init CheckOutRun");
			
			printerChecker();
			if (!printing){
				log.debug("Starting to print");
				printing = true;
				if(db.testConnection()){
					//this.log("Db connection is OK");
					if(isPrinterConnected){
						//this.log("Printer is connected");
						if(stationMode.equals("Valet")) {
							try{
								isTicketProcessed = Db.checkTicket(ticketInfo.getId());
								if(!isTicketProcessed) {
									if(!textTicket.getText().isEmpty()) {
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.setClientInfo(0, "Ticket #: " + ticketInfo.getId()));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										for(Transaction t: transactionsOut) {
											try {
												sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.setItem(
														TfhkaPrinter.TAX1, 
														t.getMaxAmount(), 
														1, 
														t.getName()));
											} catch (PrinterException ce) {
												ce.printStackTrace();
											}
										}
										
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.checkOut(
													TfhkaPrinter.PAYMENT_TYPE_EFECTIVO_01));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("TICKET VALET"));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Ticket Valet #: " + ticketInfo.getId()));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										DateTime dt = new DateTime();
										DateTimeFormatter tFormatter = DateTimeFormat.forPattern("HH:mm:ss");
										DateTimeFormatter dFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Hora: " + dt.toString(tFormatter)));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Fecha: " + dt.toString(dFormatter)));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Cajero: " + user.getName()));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentEnd("PAGADO"));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										
										if(summaryHasInvoice) {
	//										for(Transaction t: transactions) {
	//											db.insertTransaction(stationId, summaryId, ticketNumber, t.getMaxAmount(), 12, 
	//												t.getId(), payTypes.get(0).getId());
	//										}
										}else{
											if(summaryId > 0) {
												summaryHasInvoice = true;
	//											for(Transaction t: transactions) {
	//												db.insertTransaction(stationId, summaryId, ticketNumber, t.getMaxAmount(), 12, 
	//														t.getId(), payTypes.get(0).getId());
	//											}
											}else{
												try{
													statusS1 = fiscalPrinter.getS1PrinterData();
												} catch(PrinterException se) {
													se.printStackTrace();
												}
												int firstInvoiceNumber = statusS1.getLastInvoiceNumber();
												
												insertedSummaryId = db.insertSummary(stationId, user.getId(), firstInvoiceNumber);
												
												if(insertedSummaryId > 0) {
													summaryId = insertedSummaryId;
													summaryHasInvoice = true;
	//												for(Transaction t: transactions) {
	//													db.insertTransaction(stationId, summaryId, ticketNumber, t.getMaxAmount(), 12, 
	//															t.getId(), payTypes.get(0).getId());
	//												}
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
										transactionsOut.clear();
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
						} else if (stationInfo.getType().getName().equals("Entrada/Salida")) {
							log.debug("Processing ticket on E/S station");
							buttonCollectAccept.setEnabled(false);
							buttonCollectCancel.setEnabled(false);
							buttonCollectExonerate.setEnabled(false);
							log.debug("Buttons disabled");
							if(transactionsOut.size() > 0) {
								int transactionsOutIndex = transactionSelectedMulti(transactionsOut, allTransactions.get(2).getId());
								if(transactionsOutIndex > -1) {
									transactionsOut.remove(transactionsOutIndex);
								}else{
									transactionsOut.add(allTransactions.get(2));
								}
							} else {
								//this.log("Obtener transaccion de ticket de estacionamiento");
								log.debug("Getting type of transaction");
								transactionsOut.add(allTransactions.get(2));		
							}
	
								if(!exonerate && lostTicket == null){
									log.debug("Charging ticket as normal");
									if(summaryHasInvoice) {
										for(Transaction tOut: transactionsOut) {
											db.updateTransactionsOut(ticketInfo.getId(), stationInfo.getId(),  summaryId, transactionOutAmount, 12, 
												tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1));
											log.debug("Info updated for ticket #" + ticketInfo.getId());
										}
									}else{
										if(summaryId > 0) {
											summaryHasInvoice = true;
											for(Transaction tOut: transactionsOut) {
												db.updateTransactionsOut(ticketInfo.getId(),stationInfo.getId(), summaryId, transactionOutAmount, 12, 
														tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1));
											}
										}else{
											if(shiftIsDown) {
												log.trace("Shift is down");
												try{
													statusS1 = fiscalPrinter.getS1PrinterData();
												} catch(PrinterException se) {
													se.printStackTrace();
												}
												int firstInvoiceNumber = statusS1.getLastInvoiceNumber();
												db = new Db();
												insertedSummaryId = db.insertSummary(stationInfo.getId(), user.getId(), firstInvoiceNumber);
											} else {
												insertedSummaryId = db.insertSummary(stationInfo.getId(), user.getId(), 0);
											}
											if(insertedSummaryId > 0) {
												summaryId = insertedSummaryId;
												summaryHasInvoice = true;
												for(Transaction tOut: transactionsOut)  {
													db.updateTransactionsOut(ticketInfo.getId(),stationInfo.getId(), summaryId, transactionOutAmount, 12, 
															tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1));
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
									if(!shiftIsDown) {
										log.debug("Starting printing fiscal invoice");
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.setClientInfo(0, "Ticket #: " + ticketInfo.getId()));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										for(Transaction tOut: transactionsOut) {
											try {
												sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.setItem(
														TfhkaPrinter.TAX1, 
														transactionOutAmount, 
														1, 				
														tOut.getName()));
											} catch (PrinterException ce) {
												ce.printStackTrace();
											}
										}							
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.checkOut(
													TfhkaPrinter.PAYMENT_TYPE_EFECTIVO_01));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
									}
									log.debug("Finished printing fiscal invoice");
									
									Properties prop = new Properties();
									InputStream propertiesInput;
									String relayPort = "";
									int relay = 1;
									
									try {
										propertiesInput = getClass().getResourceAsStream("config.properties");
										prop.load(propertiesInput);
										relayPort = prop.getProperty("relaysport");
										relay = Integer.valueOf(prop.getProperty("relay"));
									} catch (IOException e) {
										e.printStackTrace();
									}
									
									RelayDriver rd = new RelayDriver(relayPort);
									log.trace("Opening barrier");
									try {
										log.trace("Connecting to relay board on port " + relayPort);
										if(rd.connect()) {
											log.trace("Connected to relay board");
										}
										log.trace("Opening relay #" + relay);
										if(rd.switchRelay(relay, OldRelayDriver.ACTIVE_STATE)) {
											log.trace("relay #" + relay + " was activated");
										}
										Thread.sleep(2000);
										if(rd.switchRelay(relay, OldRelayDriver.INACTIVE_STATE)) {
											log.trace("relay #" + relay + " was unactivated");
										}
										if(rd.disconnect()) {
											log.trace("Disconnected from relay board");
										}
									} catch (Exception e) {
										log.error("Error opening barrier (" + e.getMessage().toString() + ")");
										e.printStackTrace();
									}
									transactionsOut.clear();	//TODO check this...
									log.debug("Transactions cleared");
									//after print clear the textFields
								} else if (exonerate) {
									log.debug("Ticket exonerated");
									String plate = db.getPlate(ticketInfo.getId());																		
									if(summaryHasInvoice) {
										for(Transaction tOut: transactionsOut) {
											db.updateExonerated(ticketInfo.getId(), stationInfo.getId(),  summaryId, transactionOutAmount, 12, 
												tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1),true);			
										}
									}else{
										if(summaryId > 0) {
											summaryHasInvoice = true;
											for(Transaction tOut: transactionsOut) {
												db.updateExonerated(ticketInfo.getId(),stationInfo.getId(), summaryId, transactionOutAmount, 12, 
														tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1),true);
											}																
										}else{
											if(shiftIsDown) {
												try{
													statusS1 = fiscalPrinter.getS1PrinterData();
												} catch(PrinterException se) {
													se.printStackTrace();
												}
												int firstInvoiceNumber = statusS1.getLastInvoiceNumber();
												db = new Db();
												insertedSummaryId = db.insertSummary(stationInfo.getId(), user.getId(), firstInvoiceNumber);
											} else {
												insertedSummaryId = db.insertSummary(stationInfo.getId(), user.getId(), 0);
											}
											
											if(insertedSummaryId > 0) {
												summaryId = insertedSummaryId;
												summaryHasInvoice = true;
												for(Transaction tOut: transactionsOut)  {
													db.updateExonerated(ticketInfo.getId(),stationInfo.getId(), summaryId, transactionOutAmount, 12, 
															tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1),true);
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
									
									if(!shiftIsDown) {
										log.debug("Starting printing fiscal invoice");
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Ticket #: " + ticketInfo.getId()));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										
										DateTime exitDateTime = new DateTime(Db.getDbTime());
										DateTimeFormatter tFormatter = DateTimeFormat.forPattern("HH:mm:ss");
										DateTimeFormatter dFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
										
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Hora: " + exitDateTime.toString(tFormatter)));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Fecha: " + exitDateTime.toString(dFormatter)));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Cajero: " + user.getName()));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("Placa: " + plate));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentText("EXONERADO"));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.DnfDocumentEnd(Db.getConfig("client_name", "platform")));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
									}
									log.debug("Finished printing fiscal invoice");
									
									Properties prop = new Properties();
									InputStream propertiesInput;
									String relayPort = "";
									int relay = 1;
									
									try {
										propertiesInput = getClass().getResourceAsStream("config.properties");
										prop.load(propertiesInput);
										relayPort = prop.getProperty("relaysport");
										relay = Integer.valueOf(prop.getProperty("relay"));
									} catch (IOException e) {
										e.printStackTrace();
									}
									
									RelayDriver rd = new RelayDriver(relayPort);
									log.trace("Opening barrier");
									try {
										log.trace("Connecting to relay board on port " + relayPort);
										if(rd.connect()) {
											log.trace("Connected to relay board");
										}
										log.trace("Opening relay #" + relay);
										if(rd.switchRelay(relay, OldRelayDriver.ACTIVE_STATE)) {
											log.trace("relay #" + relay + " was activated");
										}
										Thread.sleep(2000);
										if(rd.switchRelay(relay, OldRelayDriver.INACTIVE_STATE)) {
											log.trace("relay #" + relay + " was unactivated");
										}
										if(rd.disconnect()) {
											log.trace("Disconnected from relay board");
										}
									} catch (Exception e) {
										log.error("Error opening barrier (" + e.getMessage().toString() + ")");
										e.printStackTrace();
									}
									
									transactionsOut.clear();	//TODO check this...
									log.debug("Transactions cleared");
								} else if (lostTicket != null){
									log.debug("Charging ticket as lost");
									if(summaryHasInvoice) {
										for(Transaction tOut: transactionsOut) {
											db.updateLostTicket(lostTicket.getId(),stationInfo.getId(),  summaryId, transactionOutAmount, 12, 
												tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1), true);			
										}
									}else{
										if(summaryId > 0) {
											summaryHasInvoice = true;
											for(Transaction tOut: transactionsOut) {
												db.updateLostTicket(lostTicket.getId(),stationInfo.getId(),  summaryId, transactionOutAmount, 12, 
														tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1), true);			
												}																
										}else{
											if(shiftIsDown) {
												try{
													statusS1 = fiscalPrinter.getS1PrinterData();
												} catch(PrinterException se) {
													se.printStackTrace();
												}
												int firstInvoiceNumber = statusS1.getLastInvoiceNumber();
												db = new Db();
												insertedSummaryId = db.insertSummary(stationInfo.getId(), user.getId(), firstInvoiceNumber);
											} else {
												insertedSummaryId = db.insertSummary(stationInfo.getId(), user.getId(), 0);
											}									
											if(insertedSummaryId > 0) {
												summaryId = insertedSummaryId;
												summaryHasInvoice = true;
												for(Transaction tOut: transactionsOut)  {
													db.updateLostTicket(lostTicket.getId(),stationInfo.getId(),  summaryId, transactionOutAmount, 12, 
															tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1), true);			
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
									
									if(!shiftIsDown) {
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.setClientInfo(0, " Ticket Perdido #  " + lostTicket.getId()));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
										for(Transaction tOut: transactionsOut) {
											try {
												sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.setItem(
														TfhkaPrinter.TAX1, 
														transactionOutAmount, 
														1, 				
														tOut.getName()));
											} catch (PrinterException ce) {
												ce.printStackTrace();
											}
										}
										try {
											sentCmd = fiscalPrinter.SendCmd(TfhkaPrinter.checkOut(
													TfhkaPrinter.PAYMENT_TYPE_EFECTIVO_01));
										} catch (PrinterException ce) {
											ce.printStackTrace();
										}
									}
									log.debug("Finished printing fiscal invoice");
									
									Properties prop = new Properties();
									InputStream propertiesInput;
									String relayPort = "";
									int relay = 1;
									
									try {
										propertiesInput = getClass().getResourceAsStream("config.properties");
										prop.load(propertiesInput);
										relayPort = prop.getProperty("relaysport");
										relay = Integer.valueOf(prop.getProperty("relay"));
									} catch (IOException e) {
										e.printStackTrace();
									}
									
									RelayDriver rd = new RelayDriver(relayPort);
									log.trace("Opening barrier");
									try {
										log.trace("Connecting to relay board on port " + relayPort);
										if(rd.connect()) {
											log.trace("Connected to relay board");
										}
										log.trace("Opening relay #" + relay);
										if(rd.switchRelay(relay, OldRelayDriver.ACTIVE_STATE)) {
											log.trace("relay #" + relay + " was activated");
										}
										Thread.sleep(2000);
										if(rd.switchRelay(relay, OldRelayDriver.INACTIVE_STATE)) {
											log.trace("relay #" + relay + " was unactivated");
										}
										if(rd.disconnect()) {
											log.trace("Disconnected from relay board");
										}
									} catch (Exception e) {
										log.error("Error opening barrier (" + e.getMessage().toString() + ")");
										e.printStackTrace();
									}
									
									transactionsOut.clear();	//TODO check this...
									log.debug("Transactions cleared");
								}
								lostTicket = null;
								log.debug("Finished processing ticket on E/S station");
						}//END of stationMode = "E/S"						
					} else {
						JOptionPane.showMessageDialog(null, "La impresora esta desconectada", "Impresora desconectada", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "La red esta desconectada, conectela de nuevo", "Red desconectada", JOptionPane.ERROR_MESSAGE);
				}			
				printing = false;
			} else {
				JOptionPane.showMessageDialog(null, "Por favor espere, se esta imprimiendo el Ticket anterior");
			}
			
			textTicket.setText("");
			textTicket.setEnabled(true);
			labelMoney.setText("Bs.");
			textDuration.setText("");
			textEntrance.setText("");			
			textDateIn.setText("");
			textCashed.setText("");
			textCashed.setEnabled(false);
			textChange.setText("");
			textExpiration.setText("");
			buttonCollectAccept.setEnabled(false);
			buttonCollectCancel.setEnabled(false);
			buttonCarEntrance.setEnabled(true);
			textEntrancePlate.setEnabled(true);
			buttonCollectExonerate.setEnabled(false);
		}		
	}
	
	private void preCheckOutLost(){
		String plateSearch= JOptionPane.showInputDialog("Introduzca la placa del vehículo");
		if(null != plateSearch && !plateSearch.isEmpty()) {
			Db db = new Db();			
			lostTicket = db.findTicketByPlate(plateSearch);
			if(lostTicket != null) {
				lostTicket.setTotalAmount(Db.getLostTicketRate(2));
				textTicket.setText(String.valueOf(lostTicket.getId()));
				labelMoney.setText(lostTicket.getTotalAmount() + " Bs.");
				buttonCarEntrance.setEnabled(false);
				textEntrancePlate.setEnabled(false);
				buttonCollectAccept.setEnabled(true);
				buttonCollectCancel.setEnabled(true);
				textCashed.setEnabled(true);
				buttonCollectExonerate.setEnabled(true);
			} else {
				JOptionPane.showMessageDialog(null, "Esta placa no esta registrada, intente de nuevo", "Placa no existe!", JOptionPane.ERROR_MESSAGE);
				labelMoney.setText("");
			}
		} else {
			buttonCarEntrance.setEnabled(true);
			textEntrancePlate.setEnabled(true);
			buttonCollectAccept.setEnabled(false);
			buttonCollectCancel.setEnabled(false);
			textCashed.setEnabled(false);
			textChange.setEnabled(false);
			buttonCollectExonerate.setEnabled(false);
			textEntrancePlate.requestFocus();
			JOptionPane.showMessageDialog(null, "Debe introducir la placa del vehículo", "Placa vacía", JOptionPane.ERROR_MESSAGE);
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
				log.debug("Printer connected to port " + activePort);
			} else {
				isPrinterConnected = false;
				log.error("Error connecting to printer on port " + activePort);
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
		double subTotal = 0.00;
		
		for(Transaction t: transactions) {
			subTotal += t.getMaxAmount();
		}
		return subTotal;
	}

 	public double getSubTotalMulti(ArrayList<Transaction> transactionsOut) {
 		double subTotal = 0.00;		
		for(Transaction tOut: transactionsOut) {
			subTotal += tOut.getMaxAmount();
		}
		return subTotal;
	}
	
	private int transactionSelectedMulti(ArrayList<Transaction> transactionsOut, int id) {
		int selectedId = -1;
		int i = 0;
		for(Transaction tOut: transactionsOut) {
			if(tOut.getId() == id) {
				selectedId = i;
				break;
			}
			i++;
		}
		return selectedId;
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
					if(loginDialog.isSucceeded() && supervisor.canPrintReportX()) {
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
				if(loginDialog.isSucceeded() && supervisor.canPrintReportX()) {
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
	
	private synchronized void printerChecker(){
		if(!printing) {
			boolean check = fiscalPrinter.CheckFprinter();
			//log.debug("CheckFprinter=" + check);
			if(check && !isPrinterConnected){
				isPrinterConnected = true;
				menuItemConnect.setEnabled(false);
				menuItemDisconnect.setEnabled(true);
				menu.setForeground(Color.GREEN);
			}else if(!check && isPrinterConnected){
				log.trace("Printer Status=" + fiscalPrinter.Estado);
				isPrinterConnected = false;
				menuItemConnect.setEnabled(true);
				menuItemDisconnect.setEnabled(false);
				menu.setForeground(Color.BLACK);
			}
		}
	}
	
	private class CheckPrinterTask extends TimerTask {

		@Override
		public void run() {
			printerChecker();
		}
		
	}
	
	private class CheckPlacesTask extends TimerTask {
		
		private int availablePlaces;
		
		public CheckPlacesTask(){
			this.setAvailablePlaces(Db.getAvailablePlaces(stationInfo.getLevelId()));
		}
		
		public  synchronized void run() {
			this.setAvailablePlaces(Db.getAvailablePlaces(stationInfo.getLevelId()));
			labelParkingCounter.setText("Puestos Disponibles: " + this.getAvailablePlaces());
		}

		public int getAvailablePlaces() {
			return this.availablePlaces;
		}

		public void setAvailablePlaces(int availablePlaces) {
			this.availablePlaces = availablePlaces;
		}
		
	}
	
}// END OF class SoftParkMultiView