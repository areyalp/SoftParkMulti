package ve.com.soted.softparkmulti.views;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ve.com.soted.softparkmulti.comm.GetNetworkAddress;
import ve.com.soted.softparkmulti.db.Db;
import ve.com.soted.softparkmulti.objects.Station;
import ve.com.soted.softparkmulti.objects.StationType;

@SuppressWarnings("serial")
public class SelectStationView extends JFrame{

	JLabel labelStation;
	JComboBox<Station> comboStations;
	JButton buttonSelect, buttonCancel;
	
	public int userId;
	
	public SelectStationView(){
		this.setSize(350, 150);
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dim = tk.getScreenSize();
		int x = (dim.width / 2) - (this.getWidth() / 2);
		int y = (dim.height / 2) - (this.getHeight() / 2);
		this.setLocation(x, y);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Seleccionar Estación");
		JPanel thePanel = new JPanel() {
            //Don't allow us to stretch vertically.
            public Dimension getMaximumSize() {
                Dimension pref = getPreferredSize();
                return new Dimension(Integer.MAX_VALUE,
                                     pref.height);
            }
        };
        thePanel.setLayout(new BoxLayout(thePanel,
                BoxLayout.PAGE_AXIS));
        thePanel.add(createStationCombo());
        thePanel.add(createButtons());
        this.add(thePanel);
        this.setVisible(true);
	}

	private JComponent createButtons() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		ButtonListener lForButton = new ButtonListener();
        JButton buttonSelect = new JButton("Seleccionar");
        buttonSelect.addActionListener(lForButton);
        buttonSelect.setActionCommand("select");
        panel.add(buttonSelect);
        buttonCancel = new JButton("Cancelar");
        buttonCancel.addActionListener(lForButton);
        buttonCancel.setActionCommand("cancel");
        panel.add(buttonCancel);
        return panel;
	}
	
	private class ButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if("select".equals(e.getActionCommand())){
				Station stationData = (Station) comboStations.getSelectedItem();
				int stationId = stationData.getId();
				String macAddress = GetNetworkAddress.GetAddress("mac");
				try {
					Db db = new Db();
					if(db.update("UPDATE Stations SET MacAddress = '" + macAddress + "' WHERE Id = " + stationId + ";")){
						dispose();
						new SoftParkMultiView(stationId);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}else if("cancel".equals(e.getActionCommand())){
				System.exit(0);
			}//END OF if("select".equals(e.getActionCommand()))
		}//END OF public void actionPerformed
	}//END OF class ButtonListener
	
	private JPanel createStationCombo() {
		ArrayList<Station> stationsList = new ArrayList<Station>();
		stationsList = this.getStations();
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		labelStation = new JLabel("Seleccione Estación:");
		panel.add(labelStation);
		comboStations = new JComboBox<Station>(new Vector<>(stationsList));
		panel.add(comboStations);
		return panel;
	}

	private ArrayList<Station> getStations() {
		ArrayList<Station> stations = new ArrayList<Station>();
		try{
			Db db = new Db();
			ResultSet rowsStations = db.select("SELECT Id, TypeId, Name, LevelId FROM Stations WHERE Active=1 AND MacAddress = ''");
//			rowsStations.last();
//			int rowsCount = rowsStations.getRow();
//			rowsStations.beforeFirst();
//			stations = new Station[rowsCount];
//			int i = 0;
			while(rowsStations.next()){
				stations.add(new Station(
						rowsStations.getInt("Id"), 
						new StationType(rowsStations.getInt("TypeId")), 
						rowsStations.getString("Name"),
						rowsStations.getInt("LevelId")
						));
//				i++;
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return stations;
	}//END OF private Item[] getStations()
}//END OF class SelectStationView