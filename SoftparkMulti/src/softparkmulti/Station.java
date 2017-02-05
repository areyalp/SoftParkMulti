package softparkmulti;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Station
    {
		private Integer id;
        private Integer type;
        private String name;
        private Integer levelId;
        
        Station(Integer id, Integer type, String name, Integer levelId)
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
        
        public Integer getType()
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
    						rowStation.getInt("TypeId"), 
    						rowStation.getString("Name"),
    						rowStation.getInt("LevelId"));
    			}
    			
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    		
    		return stationInfo;
    	}
    }