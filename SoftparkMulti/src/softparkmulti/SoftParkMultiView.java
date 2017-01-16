package softparkmulti;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import java.awt.event.TextEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
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
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import jssc.SerialPortList;
import tfhka.PrinterException;
import tfhka.ve.S1PrinterData;
import tfhka.ve.S2PrinterData;
import tfhka.ve.Tfhka;



@SuppressWarnings("serial")
public class SoftParkMultiView extends JFrame {

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
	private ArrayList<Transaction> transactionsType;
	private ArrayList<Transaction> transactions;
	private ArrayList<TransactionsIn> transactionsin;
	private ArrayList<TransactionsOut> transactionsOut = new ArrayList<TransactionsOut>();
	private ArrayList<TransactionsOut> transactionsOutType = new ArrayList<TransactionsOut>();
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
	private JPanel personalPanel, companyPanel, middleContainer, wrapRightContainerPanel;

	private JCheckBox checkBillsName;
	private JToolBar toolBar;
	
	private JComboBox<String> comboCompany, comboShop,comboColor, comboModel, comboState, comboBrand;

	private static JLabel labelStatus;
	
	private String activePort, relayPort="";

	private Boolean exonerate;
	private JTextField textTicket, textPlate, textOwnerId, textOwnerName, textOwnerLastName, textDescription;
	private JTextField textExpiration,textDuration, textEntrance,textCashed,textChange;
	private JTextField textEntrancePlate;
	
	private JLabel labelPrice, labelMoney;
	private JFormattedTextField textDateIn;
	
	private JTree tree;	
	private JButton buttonReloadReports;
	private JComboBox<String> comboCountry, comboDirectionState;
	private JButton buttonCollectAccept, buttonCollectCancel, buttonCarEntrance, buttonCollectExonerate;
	
	public Integer transactionId,ticketNumber,amount, overnightDays;
	public Timestamp entranceDateTime;
	
	
	public SoftParkMultiView(int stationId) {
		
		fiscalPrinter = new tfhka.ve.Tfhka("COM99");
		
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
		
		transactionsOutType =  Db.loadTransactionsOutTypes();
		
		transactions = new ArrayList<Transaction>();
		
		transactionsin = new ArrayList<TransactionsIn>();
		
		transactionsOut = new ArrayList<TransactionsOut>();
		
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
			stationMode = "E/S";
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
		labelMoney.setForeground(Color.RED);		
		paymentPanel.add(labelMoney);
		
		JLabel labelCashed = new JLabel("Entregado");
		paymentPanel.add(labelCashed);
		textCashed = new JTextField(14);
		textCashed.setEditable(false);
		paymentPanel.add(textCashed);
		
		JLabel labelChange = new JLabel("Vuelto");
		paymentPanel.add(labelChange);
		textChange = new JTextField(14);
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
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		ButtonListener lForSwitchButton = new ButtonListener();
		
		buttonCollectAccept = new JButton("Aceptar");
		buttonCollectAccept.setActionCommand("multi.accept.button");
		buttonCollectAccept.addActionListener(lForSwitchButton);
		
		buttonPanel.add(buttonCollectAccept);
		
		buttonCollectCancel = new JButton("Cancelar");
		buttonCollectCancel.setActionCommand("multi.cancel.button");
		buttonCollectCancel.addActionListener(lForSwitchButton);
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
		JLabel labelDirectionsTitle = new JLabel("Direcci�n     ");
		labelDirectionsTitle.setFont(new Font(null, Font.BOLD, 20));
		directionsPanel.add(labelDirectionsTitle);

		JLabel labelCountry = new JLabel("Pa�s:");
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
		
		container.add(directionsPanel);		
		
		//create here new panel for the entrance
		JPanel entrancePanel = new JPanel(new BorderLayout(20,20));
		
		JPanel entranceTitlePanel = new JPanel();
		
		JLabel labelEntranceTitle = new JLabel("Ingreso de Veh�culos");
		labelEntranceTitle.setFont(new Font(null, Font.BOLD, 20));
		entranceTitlePanel.add(labelEntranceTitle);
		
		entrancePanel.add(entranceTitlePanel, BorderLayout.NORTH);
		
		JPanel entranceButtonPanel = new JPanel();
		
		ButtonListener lForSwitchButton = new ButtonListener();
		
		buttonCarEntrance = new JButton("Ingresar");
		buttonCarEntrance.setPreferredSize(getPreferredSize());
		buttonCarEntrance.setActionCommand("vehicle.in.button");
		buttonCarEntrance.addActionListener(lForSwitchButton);
		
		entranceButtonPanel.add(buttonCarEntrance);
		
		entrancePanel.add(entranceButtonPanel, BorderLayout.CENTER);
		
		JPanel entrancePlatePanel = new JPanel();
		
		JLabel labelEntrancePlate = new JLabel("Placa:");
		entrancePlatePanel.add(labelEntrancePlate);
		textEntrancePlate = new JTextField(12);
		entrancePlatePanel.add(textEntrancePlate);
		
		entrancePanel.add(entrancePlatePanel, BorderLayout.SOUTH);		
		
		container.add(Box.createVerticalStrut(30));
		container.add(entrancePanel);		
		container.add(Box.createVerticalStrut(30));
		
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
		//INSERT HERE CAR�S DATA PANEL
		JPanel carsDataPanel = new JPanel();
		
		GroupLayout layout = new GroupLayout(carsDataPanel);
		carsDataPanel.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		//added a title to the ticket data
		JLabel labelTitle = new JLabel("Datos de Veh�culo");
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

		JLabel labelMobilePhone = new JLabel("M�vil:");
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

		toolBarButtonLostTicket = new JButton("Ticket Perdido", new ImageIcon("resources/lost_ticket.png"));

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

		menu = new JMenu("�");
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
	
	private class TextFieldListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent evt) {
			Integer keyCode = evt.getKeyCode();
			
			if(keyCode == KeyEvent.VK_TAB) {
//				CheckOutRun out = new CheckOutRun("E/S");
//				new Thread(out).start();
				preCheckOut();
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		

	}
	
	private class ButtonListener implements ActionListener, KeyListener {

		private boolean exonerate;

		@Override
		public void keyPressed(KeyEvent ev) {
			SelectValetRun v = new SelectValetRun(KeyEvent.getKeyText(ev.getKeyCode()));
			new Thread(v).start();
			
			CheckOutRun v1 = new CheckOutRun(KeyEvent.getKeyText(ev.getKeyCode()));
			new Thread(v1).start();
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
			}
			else{
				if(ev.getModifiers() == 17) {
					shiftIsDown = true;
				} else {
					shiftIsDown = false;
				}
				if (ev.getActionCommand().equalsIgnoreCase("vehicle.in.Button")) {
					CheckInRun v = new CheckInRun(ev.getActionCommand());
					new Thread(v).start();
					textEntrancePlate.setText("");
				}
				else if (ev.getActionCommand().equalsIgnoreCase("multi.accept.button"))  {				
					CheckOutRun out = new CheckOutRun("E/S");
					new Thread(out).start();
				}
				else if (ev.getActionCommand().equalsIgnoreCase("multi.exonerate.button")){					
					int userResponse = JOptionPane.showConfirmDialog(null, "�Desea exonerar este ticket?", "Confirme exoneracion de un ticket", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);					
					if(userResponse == JOptionPane.YES_OPTION) {
						CheckOutRun out = new CheckOutRun("E/S", true);
						new Thread(out).start();
					}
					else{
						//TODO
					}
				}
				else if (ev.getActionCommand().equalsIgnoreCase("multi.cancel.button")) {
					textTicket.setEditable(true);
					textTicket.setText("");
					textDateIn.setEditable(false);
					textDateIn.setText("");
					textEntrance.setEditable(false);
					textEntrance.setText("");
					textDuration.setEditable(false);
					textDuration.setText("");
					textExpiration.setEditable(false);
					textExpiration.setText("");
					textCashed.setEditable(false);
					textCashed.setText("");
					textChange.setEditable(false);
					textChange.setText("");
				}
				
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
//				if(boxesFrame.isClosed()){
					createSubPanelRight();
//				}
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
//					CheckOutRun co = new CheckOutRun(transactions);
//					Thread t = new Thread(co);
//					t.start();
				}
			}			
		}		
	}
		
	public String Relay() {
		// TODO Auto-generated method stub
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
		CheckInRun(String actionCommand) {
			this.actionCommand = actionCommand;
		}
		@Override
		public synchronized void run() {
			if (stationMode.equals("E/S")){
					//TODO Finish this to print entrance ticket		
				//Have to check the presence of the vehicle to allow print the ticket
				if(actionCommand.equalsIgnoreCase("vehicle.in.button")) {
					Db db = new Db();
					String plate = textEntrancePlate.getText();		
					ticketNumber = 0;
					
					boolean sentCmd = false;
					int transactionId = db.preTransactionIn(stationId,plate);
					try {
						sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Ticket #: " + transactionId));
					} catch (PrinterException ce) {
						ce.printStackTrace();
					}
					DateTime entranceDateTime = new DateTime();
					DateTimeFormatter tFormatter = DateTimeFormat.forPattern("HH:mm:ss");
					DateTimeFormatter dFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
					DateTimeFormatter tFormatter2 = DateTimeFormat.forPattern("HHmmss");
					DateTimeFormatter dFormatter2 = DateTimeFormat.forPattern("ddMMyyyy");
					try {
						sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Hora: " + entranceDateTime.toString(tFormatter)));
					} catch (PrinterException ce) {
						ce.printStackTrace();
					}
					try {
						sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Fecha: " + entranceDateTime.toString(dFormatter)));
					} catch (PrinterException ce) {
						ce.printStackTrace();
					}
					try {
						sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Cajero: " + user.getName()));
					} catch (PrinterException ce) {
						ce.printStackTrace();
					}
					try {
						sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Placa: " + plate));
					} catch (PrinterException ce) {
						ce.printStackTrace();
					}
					try {
						sentCmd = fiscalPrinter.SendCmd(PrinterCommand.setBarcode(entranceDateTime.toString(dFormatter2) + entranceDateTime.toString(tFormatter2) + StringTools.fillWithZeros(stationId, 3) + StringTools.fillWithZeros(transactionId,11)));
					} catch (PrinterException ce) {
						ce.printStackTrace();
					}
					try {
						sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentEnd("C.C. El Paseo"));
					} catch (PrinterException ce) {
						ce.printStackTrace();
					}					
					int activateRelay = 0;
					int relay1 = 1;
					int relay2 = 2;
					RelayDriver rd = new RelayDriver();
						try {
//							rd.getSerialPort();
							rd.connect("COM8");
							rd.switchRelay(relay1, RelayDriver.INACTIVE_STATE);
							rd.switchRelay(relay1, RelayDriver.ACTIVE_STATE);
							//check the entrance sensor
//							if (entranceSensor.isActive){
//																
//							}
//							else{
//								rd.switchRelay(relay1, RelayDriver.INACTIVE_STATE);
//							}
							
							rd.switchRelay(relay1, RelayDriver.INACTIVE_STATE);
							//TODO check sensor
							//use timer.. en mercabar software

							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					//Check the second  entrance sensor if the state is inactive then send the INACTIVE_STATE<
						textEntrancePlate.setText("");
					
				}
			}		//end of station mode= E/S
			
		}
				
	}
	
	private void preCheckOut (){
		
		String ticketCode = "";
		ticketNumber = 0;
		Boolean isTicketOut = true;
		Boolean isTicketIn = false;
		
		try{							
			ticketCode = textTicket.getText();							
			ticketNumber = Integer.valueOf(ticketCode.substring(17));
			isTicketIn = Db.isTicketIn(ticketNumber);
			isTicketOut = Db.isTicketOut(ticketNumber);
			//verify  ticket in the DB...
			if(isTicketIn) {
				//Verify the ticket without an exit
				if (!isTicketOut){
					
					if(ticketNumber > 0) { 	
						
						String day = ticketCode.substring(0,2);
						String month = ticketCode.substring(2,4);
						String year = ticketCode.substring(4,8);
						String hour = ticketCode.substring(8,10);
						String minutes = ticketCode.substring(10,12);
						String seconds = ticketCode.substring(12,14);	
						
						DateTime dtIn = new DateTime(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(minutes), Integer.valueOf(seconds), 0);
						DateTimeFormatter tFormatter = DateTimeFormat.forPattern("HH:mm:ss");
						DateTimeFormatter dFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");	
																
						DateTime dtOut = new DateTime();
						DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
						DateTimeFormatter dtf2 = DateTimeFormat.forPattern("HH:mm:ss");
															
						Period period = new Period(dtIn, dtOut);
						Integer dayIn = dtIn.getDayOfMonth();
						Integer dayOut = dtOut.getDayOfMonth();
						Db db = new Db();
						
						textDateIn.setEditable(true);
						textDateIn.setText(day + month + year + hour + minutes + seconds);		//fecha y hora de entrada
						textEntrance.setEditable(true);
						textEntrance.setText(ticketCode.substring(14,17));	//estacion de entrada
														
						textDuration.setEditable(true);						
						textExpiration.setEditable(true);				
						Integer ticketTimeout = Integer.valueOf(db.getConfig("ticket_timeout", "time"));
						textExpiration.setText(String.valueOf(dtf2.print(dtOut.plusMinutes(ticketTimeout))));				
						Boolean overnightType = Boolean.valueOf(db.getConfig("overnight_type", "billing"));
						//dtIn mayor dtOut
						if (dtIn.isBefore(dtOut)){
							//Check the overnight_type in the configs table from the DB
							if (!overnightType){
								//If the value = 0 then the charge will be by hours
								Integer hoursLapse = Hours.hoursBetween(dtIn, dtOut).getHours();
								
								DecimalFormat df = new DecimalFormat("00");
//								String durationHours = df.format(period.getHours());
								String durationMinutes = df.format(period.getMinutes());
								String durationSeconds = df.format(period.getSeconds());														
								String durationTime = hoursLapse + ":" + durationMinutes+ ":" +durationSeconds;							
								textDuration.setText(durationTime);

								Integer spendMinutes = period.getMinutes();
//								Integer spendHours = period.getHours();
								amount = 0;
								if ( spendMinutes > 29){
									amount = db.getHourRates(hoursLapse + 1);
									labelMoney.setText(String.valueOf(amount) + " Bs.");
								}
								else{
									amount = db.getFractionRates(hoursLapse);								
									labelMoney.setText(String.valueOf(amount) + "Bs.");
								}
								
							}
							else{
								//If the value = 1 then the charge will be by night (overnights)
								Integer overnightOffset = Integer.valueOf(db.getConfig("overnight_time", "time"));
								DateTime dtInOffset = dtIn.minusHours(overnightOffset);
								DateTime dtOutOffset = dtOut.minusHours(overnightOffset);
								Integer daysBetween = Days.daysBetween(dtInOffset, dtOutOffset).getDays();
								overnightDays = daysBetween;
								
								if (overnightDays > 0){
									JOptionPane.showMessageDialog(null, "Vehiculo con pernocta", "Atenci�n", JOptionPane.WARNING_MESSAGE);  //Add the exit hour to this message
									textDuration.setText(String.valueOf(daysBetween + " d�as "));
									amount = (db.getOvernightRates("ticket_pernocta") * overnightDays);
									labelMoney.setText(String.valueOf(amount) + " Bs.");
								}
								else{
									DecimalFormat df = new DecimalFormat("00");
									String durationHours = df.format(period.getHours());
									String durationMinutes = df.format(period.getMinutes());
									String durationSeconds = df.format(period.getSeconds());														
									String durationTime = durationHours + ":" + durationMinutes+ ":" +durationSeconds;							
									textDuration.setText(durationTime);

									Integer spendMinutes = period.getMinutes();
									Integer spendHours = period.getHours();
									amount = 0;
									if ( spendMinutes > 29){
										amount = db.getHourRates(spendHours + 1);
										labelMoney.setText(String.valueOf(amount) + " Bs.");
									}
									else{
										amount = db.getFractionRates(spendHours);								
										labelMoney.setText(String.valueOf(amount) + "Bs.");
									}								
								}
								
							}
	
							buttonCollectCancel.setEnabled(true);
							buttonCollectExonerate.setEnabled(true);
						}
						else{
							JOptionPane.showMessageDialog(null, "La hora de ticket inv�lida", "Atenci�n", JOptionPane.WARNING_MESSAGE);  //Add the exit hour to this message

						}
											
						
					}else{
						JOptionPane.showMessageDialog(null, "El numero de ticket no puede estar vacio", "Numero de ticket invalido", JOptionPane.WARNING_MESSAGE);
					}
					
				}//END OF !ISTICKETOUT
				else{
					JOptionPane.showMessageDialog(null, "Ticket con salida", "Ticket  Procesado", JOptionPane.WARNING_MESSAGE);  //Add the exit hour to this message
					textTicket.setText("");
				}
			
			}else{
				JOptionPane.showMessageDialog(null, "El Ticket  no ha sido generado, inserte el numero correcto","Ticket procesado", JOptionPane.ERROR_MESSAGE);
				textTicket.setText("");
			}
					
			}catch(NumberFormatException ne) {
				JOptionPane.showMessageDialog(null, "Introduzca un numero de ticket valido", "Numero de ticket invalido", JOptionPane.WARNING_MESSAGE);
				textTicket.setText("");
			}
 
		
	}
	
	private class CheckOutRun implements Runnable {
		
		String stationMode;
		Boolean exonerate;
		ArrayList<Transaction> transactions;
		ArrayList<TransactionsOut> transactionsOut = new ArrayList<TransactionsOut>();
		ArrayList<TransactionsIn> transactionsin = new ArrayList<TransactionsIn>();
		S1PrinterData statusS1;
		S2PrinterData statusS2;
		boolean sentCmd = false;
		
		public CheckOutRun(String stationMode) {
			this(stationMode,false);
		}
		
		public CheckOutRun(String stationMode, Boolean exonerate) {
			this.stationMode = stationMode;
			this.exonerate = exonerate;
		}

		@Override
		public synchronized void run() {
			Db db = new Db();
			int insertedSummaryId = 0;
			boolean isTicketProcessed = false;
						
			printerChecker();
			
			if(db.testConnection()){
//				if(isPrinterConnected){
					if(stationMode.equals("Valet")) {					
						try{
//							ticketNumber = textTicket.getText();
//							isTicketProcessed = Db.checkTicket(ticketNumber);
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
											db = new Db();
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
					} else if (stationMode.equals("E/S")) {						
						if(!exonerate){
							if(!shiftIsDown) {
								if(transactionsOut.size() > 0) {
									Integer transactionsOutIndex = transactionSelectedMulti(transactionsOut, transactionsOutType.get(2).getId());
									if(transactionsOutIndex > -1) {
										transactionsOut.remove(transactionsOutIndex);
									}else{
										transactionsOut.add(transactionsOutType.get(2));
									}											
								}
								else{
									transactionsOut.add(transactionsOutType.get(2));		
								}								
								try {
									sentCmd = fiscalPrinter.SendCmd(PrinterCommand.setClientInfo(0, "Ticket #: " + ticketNumber));
								} catch (PrinterException ce) {
									ce.printStackTrace();
								}
								//TODO
								double price = 350;	
								for(TransactionsOut tOut: transactionsOut) {
									try {
										sentCmd = fiscalPrinter.SendCmd(PrinterCommand.setItem(
												PrinterCommand.TAX1, 
												price, 
												1, 				
												tOut.getName()));
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
							}						
							db = new Db();
	
							if(summaryHasInvoice) {
								for(TransactionsOut tOut: transactionsOut) {
									db.insertTransactionsOut(stationId,  summaryId, ticketNumber, amount, 12, 
										tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1));			
								}
							}else{
								if(summaryId > 0) {
									summaryHasInvoice = true;
									for(TransactionsOut tOut: transactionsOut) {
										db.insertTransactionsOut(stationId, ticketNumber, summaryId, amount, 12, 
												tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1));
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
										insertedSummaryId = db.insertSummary(stationId, user.getId(), firstInvoiceNumber);
									} else {
										insertedSummaryId = db.insertSummary(stationId, user.getId(), 0);
									}
									
									if(insertedSummaryId > 0) {
										summaryId = insertedSummaryId;
										summaryHasInvoice = true;
										for(TransactionsOut tOut: transactionsOut)  {
											db.insertTransactionsOut(stationId, ticketNumber, summaryId, amount, 12, 
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
							//after print clear the textFields						
						}
						else{
							String plate = db.getPlate(ticketNumber);
							try {
								sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Ticket #: " + ticketNumber));
							} catch (PrinterException ce) {
								ce.printStackTrace();
							}
							DateTime entranceDateTime = new DateTime();
							DateTimeFormatter tFormatter = DateTimeFormat.forPattern("HH:mm:ss");
							DateTimeFormatter dFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
							DateTimeFormatter tFormatter2 = DateTimeFormat.forPattern("HHmmss");
							DateTimeFormatter dFormatter2 = DateTimeFormat.forPattern("ddMMyyyy");
							try {
								sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Hora: " + entranceDateTime.toString(tFormatter)));
							} catch (PrinterException ce) {
								ce.printStackTrace();
							}
							try {
								sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Fecha: " + entranceDateTime.toString(dFormatter)));
							} catch (PrinterException ce) {
								ce.printStackTrace();
							}
							try {
								sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Cajero: " + user.getName()));
							} catch (PrinterException ce) {
								ce.printStackTrace();
							}
							try {
								sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("Placa: " + plate));
							} catch (PrinterException ce) {
								ce.printStackTrace();
							}
							try {
								sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentText("EXONERADO"));
							} catch (PrinterException ce) {
								ce.printStackTrace();
							}
							try {
								sentCmd = fiscalPrinter.SendCmd(PrinterCommand.DnfDocumentEnd("C.C. El Paseo"));
							} catch (PrinterException ce) {
								ce.printStackTrace();
							}			
							//TODO check the insertion in DB and calculate amount
							db = new Db();

							if(summaryHasInvoice) {
								for(TransactionsOut tOut: transactionsOut) {
									db.insertExonerated(stationId,  summaryId, ticketNumber, amount, 12, 
										tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1),exonerate);			
								}
							}else{
								if(summaryId > 0) {
									summaryHasInvoice = true;
									for(TransactionsOut tOut: transactionsOut) {
										db.insertExonerated(stationId, ticketNumber, summaryId, amount, 12, 
												tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1),exonerate);
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
										insertedSummaryId = db.insertSummary(stationId, user.getId(), firstInvoiceNumber);
									} else {
										insertedSummaryId = db.insertSummary(stationId, user.getId(), 0);
									}
									
									if(insertedSummaryId > 0) {
										summaryId = insertedSummaryId;
										summaryHasInvoice = true;
										for(TransactionsOut tOut: transactionsOut)  {
											db.insertExonerated(stationId, ticketNumber, summaryId, amount, 12, 
													tOut.getId(), payTypes.get(0).getId(), (shiftIsDown?0:1),exonerate);
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
						}
					}//END of stationMode = "E/S"						
//				}
//				else{
//					JOptionPane.showMessageDialog(null, "La impresora esta desconectada", "Impresora desconectada", JOptionPane.ERROR_MESSAGE);
//				}
			}else{
				JOptionPane.showMessageDialog(null, "La red esta desconectada, conectela de nuevo", "Red desconectada", JOptionPane.ERROR_MESSAGE);
			}
			
			textTicket.setText("");
			textTicket.setEditable(true);
			
			labelMoney.setText("Bs.");
			textDuration.setEditable(false);
			textDuration.setText("");

			textEntrance.setEditable(false);
			textEntrance.setText("");
			
			textDateIn.setEditable(false);
			textDateIn.setText("");

			textCashed.setEditable(false);
			textCashed.setText("");

			textChange.setEditable(false);
			textChange.setText("");
			
			textExpiration.setEditable(false);
			textExpiration.setText("");
			buttonCollectAccept.setEnabled(true);
			buttonCollectCancel.setEnabled(false);
			buttonCollectExonerate.setEnabled(false);

			amount = 0;
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

	public double getSubTotalMulti(ArrayList<TransactionsOut> transactionsOut) {
		double subTotal = 0;
		
		for(TransactionsOut tOut: transactionsOut) {
			subTotal += tOut.getMaxAmount();
		}
		return subTotal;
	}
	
	private int transactionSelectedMulti(ArrayList<TransactionsOut> transactionsOut, int id) {
		int selectedId = -1;
		int i = 0;
		for(TransactionsOut tOut: transactionsOut) {
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