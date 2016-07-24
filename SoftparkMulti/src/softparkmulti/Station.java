package softparkmulti;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Station
    {
        int id;
        int type;
        String name;
        
        Station(int id, int type, String name)
        {
        	this.id = id;
        	this.type = type;
        	this.name = name;
        }
        
        public int getId()
        {
            return id;
        }
        
        public int getType()
        {
            return type;
        }
        
        public String getName()
        {
            return name;
        }
        
        @Override 
        public String toString()
        {
            // very important.  this is what shows in combobox
            return name;
        }
        
        public static Station getStationInfo(int stationId) {
    		Station stationInfo = null;
    		Db db = new Db();
    		ResultSet rowStation = db.select("SELECT Id, TypeId, Name FROM Stations WHERE Id = " + stationId);
    		
    		try {
    			
    			if(rowStation.next()) {
    				
    				stationInfo = new Station(
    						rowStation.getInt("Id"), 
    						rowStation.getInt("TypeId"), 
    						rowStation.getString("Name"));
    				
    			}
    			
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    		
    		return stationInfo;
    	}
    }