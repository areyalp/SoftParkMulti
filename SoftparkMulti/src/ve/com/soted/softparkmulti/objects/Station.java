package ve.com.soted.softparkmulti.objects;

import java.sql.ResultSet;
import java.sql.SQLException;

import ve.com.soted.softparkmulti.db.Db;

public class Station
    {
		private Integer id;
        private StationType type;
        private String name;
        private Integer levelId;
        
        public Station(Integer id, StationType type, String name, Integer levelId)
        {
        	this.id = id;
        	this.type = type;
        	this.name = name;
        	this.levelId = levelId;
        }
        
        public Integer getId()
        {
            return this.id;
        }
        
        public StationType getType()
        {
            return this.type;
        }
        
        public String getName()
        {
            return this.name;
        }
        
        public Integer getLevelId() 
        {
        	return this.levelId;
        }
        
        @Override 
        public String toString()
        {
            return name;
        }
        
        public static Station getStationInfo(Integer stationId) {
    		Station stationInfo = null;
    		Db db = new Db();
    		ResultSet rowStation = db.select("SELECT Id, TypeId, Name, LevelId FROM Stations WHERE Id = " + stationId);
    		
    		try {
    			
    			if(rowStation.next()) {
    				
    				stationInfo = new Station(
    						rowStation.getInt("Id"), 
    						new StationType(rowStation.getInt("TypeId")), 
    						rowStation.getString("Name"),
    						rowStation.getInt("LevelId"));
    			}
    			
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    		
    		return stationInfo;
    	}
    }