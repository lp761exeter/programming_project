package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * Main executor class that handles the CityRescure simulation
 */
public class CityRescueImpl implements CityRescue 
{
	// limits
	public final int MAX_STATIONS = 20;
	public final int MAX_UNITS = 50;
	public final int MAX_INCIDENTS = 200;
	
	// global variables
	public CityMap MAP;
	public int TICK;
	public int NEXT_STATION_ID;
	public int NEXT_UNIT_ID;
	public boolean[][] OBSTACLES;
	public Map<Integer, Station> STATIONS = new HashMap<Integer, Station>();
	public Map<Integer, Unit> UNITS = new HashMap<Integer, Unit>();
	
	// graded methods
	
	/*
	 * initializes the global variables with the given grid width and height
	 */
    @Override
    public void initialise(int width, int height) throws InvalidGridException 
    {
    	if (width<=0 || height<=0) 
    	{
    		throw new InvalidGridException("Invalid grid dimensions");
    	}
    	MAP = new CityMap(width,height);
    	TICK = 0;
    	NEXT_STATION_ID = 1;
    	NEXT_UNIT_ID = 1;
    	OBSTACLES = new boolean[width][height];
    }
    
    /*
     * returns an int array with the board width at 0 and the board height at 1
     */
    @Override
    public int[] getGridSize() 
    {
    	int width = MAP.getWidth();
    	int height = MAP.getHeight();
    	return new int[]{width,height};
    }
    
    /* 
     * adds an obstacle to the board at (x,y). 
     * Throws an exception if the location is invalid
     */
    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException 
    {
        if (x<0 || x>MAP.width || y<0 || y>MAP.height)
        {
        	throw new InvalidLocationException("x or y is out bounds");
        }
        else
        {
        	OBSTACLES[y][x] = true;
        }
    }
    
    /*
     * removes an obstacle at (x,y). 
     * Throws an exception if the location is invalid. 
     * doesn't matter if there is an obstacle at (x,y), sets the square to empty.
     */
    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException {
    	if (x<0 || x>MAP.width || y<0 || y>MAP.height)
        {
        	throw new InvalidLocationException("x or y is out bounds");
        }
        else
        {
        	MAP.grid[y][x]="";
        }
    }
    
    /*
     * Adds a station to the MAP with a name and at (x,y).
     * Throws exceptions if the name is blank or the location is out of bounds.
     * iterates NEXT_STATION_ID.
     * returns the ID of the new station
     */
    @Override
    public int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException 
    {
    	if (name.length()==0)
    	{
    		throw new InvalidNameException("Name cannot be blank");
    	}
    	if (x<0 || x>MAP.getWidth() || y<0 || y>MAP.getHeight())
    	{
    		throw new InvalidLocationException("Station out of Bounds");
    	}
        Station station = new Station(name, NEXT_STATION_ID, x,y);
        int id = station.getStationId();
        STATIONS.put(id, station);
        NEXT_STATION_ID++;
        
        return id;
    }
    
    /*
     * removes a station with a given ID.
     * throws exceptions if the ID is not recognized or the station is not empty
     */
    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException 
    {
    	if (STATIONS.containsKey(stationId))
    	{
    		Station station = STATIONS.get(stationId);
    		if (station.getNumUnits()<=0)
    		{
    			STATIONS.remove(stationId);
    		}
    		else
    		{
    			throw new IllegalStateException("Station is not empty");
    		}
    	}
    	else
    	{
    		throw new IDNotRecognisedException("Station ID not found");
    	}
    	
    }

    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException 
    {
    	if (STATIONS.containsKey(stationId))
    	{
    		Station station = STATIONS.get(stationId);
    		if (maxUnits<0)
    		{
    			throw new InvalidCapacityException("Capacity cannot be less than 0");
    		}
    		else if (station.getNumUnits()>=maxUnits)
    		{
    			throw new IllegalStateException("Capacity cannot be less than currently stored units");
    		}
    		else
    		{
    			station.setCapacity(maxUnits);
    		}
    	}
    	else
    	{
    		throw new IDNotRecognisedException("Station ID not found");
    	}
    }
    
    /*
     * returns a list of the ids of every active station in ascending order
     */
    @Override
    public int[] getStationIds()
    {
    	Integer[] temp = STATIONS.keySet().toArray(new Integer[0]);
    	int[] IDs = new int[temp.length];
    	
    	for (int i = 0; i < temp.length; i++) 
    	{
    	    IDs[i] = temp[i];
    	}
    	
        return IDs;
    }

    @Override
    public int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException 
    {
    	if (type==null)
    	{
    		throw new InvalidUnitException("Invalid Unit Type");
    	}
    	if (STATIONS.containsKey(stationId))
    	{
    		throw new IDNotRecognisedException("Station ID not found");
    	}
    	
    	Station station = STATIONS.get(stationId);
		int x = station.getX();
		int y = station.getY();
		int stationID = station.getStationId();
		Unit unit = type.create(type, stationID, NEXT_UNIT_ID, x, y);
		UNITS.put(NEXT_UNIT_ID, unit);
		station.addUnit(unit);
		NEXT_UNIT_ID++;
        
        return NEXT_UNIT_ID-1;
    }

    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException 
    {
    	if (UNITS.containsKey(unitId))
    	{
    		throw new IDNotRecognisedException("Station ID not found");
    	}
    	Unit unit = UNITS.get(unitId);
    	UnitStatus status = unit.getStatus();
    	if (status==UnitStatus.EN_ROUTE || status==UnitStatus.AT_SCENE)
    	{
    		throw new IllegalStateException("Cannot decomission unit while operating");
    	}
    	
    	UNITS.remove(unitId);
    }

    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException 
    {
    	// error if unit or station not found
    	if (UNITS.containsKey(unitId))
    	{
    		throw new IDNotRecognisedException("Unit ID not found");
    	}
    	if (STATIONS.containsKey(newStationId))
    	{
    		throw new IDNotRecognisedException("Station ID not found");
    	}
    	
    	Unit unit = UNITS.get(unitId);
    	Station newStation = STATIONS.get(newStationId);
    	
    	// error if unit is not idle or station is full
    	if (unit.getStatus() != UnitStatus.IDLE)
    	{
    		throw new IllegalStateException("Unit must be IDLE to transfer");
    	}
    	if (newStation.getNumUnits()>=newStation.getCapacity())
    	{
    		throw new IllegalStateException("Station is full");
    	}
    	
    	Station oldStation = STATIONS.get(unit.getStationId());
    	oldStation.removeUnit(unitId);
    	newStation.addUnit(unit);
    	unit.setStationId(newStationId);
    	
    	int x = newStation.getX();
    	int y = newStation.getY();
    	unit.setLocation(x, y);
    	
    	
    }
    
    /*
     * the specification for this method is very bad.
     * i assume they want that if the bool is true, the unit must be idle to be set OUT_OF_SERVICE, and if it's not IDLE the method does nothing.
     * if the bool is false, the unit is set OUT_OF_SERVICE no matter what
     */
    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException 
    {
    	if (UNITS.containsKey(unitId))
    	{
    		throw new IDNotRecognisedException("Unit ID not found");
    	}
    	
    	Unit unit = UNITS.get(unitId);
    	if (outOfService)
    	{
    		if (unit.getStatus()==UnitStatus.IDLE)
    		{
    			unit.setStatus(UnitStatus.OUT_OF_SERVICE);
    		}
    		else
    		{
    			throw new IllegalStateException("Unit must be IDLE to be set to OUT_OF_SERVICE");
    		}
    	}
    	else
    	{
    		unit.setStatus(UnitStatus.OUT_OF_SERVICE);
    	}
    }

    @Override
    public int[] getUnitIds() 
    {
    	Integer[] temp = UNITS.keySet().toArray(new Integer[0]);
    	int[] IDs = new int[temp.length];
    	
    	for (int i = 0; i < temp.length; i++) 
    	{
    	    IDs[i] = temp[i];
    	}
    	
        return IDs;
    }
    
    /*
     * prints a string that describes the unit
     * format example:
     * U#2 TYPE=FIRE_ENGINE HOME=2 LOC=(3,1) STATUS=AT_SCENE INCIDENT=1 WORK=2
     */
    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException 
    {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int reportIncident(IncidentType type, int severity, int x, int y) throws InvalidSeverityException, InvalidLocationException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int[] getIncidentIds() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void dispatch() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void tick() {
    	MAP.printGrid();
        
        TICK++;
    }

    @Override
    public String getStatus() 
    {
       String report = "";
       report+="TICK="+TICK+"\n";
       return report;
    }
    
    // original methods
    
    @Override
    public void printStations()
    {
    	for (int i = 0; i<STATIONS.size(); i++)
    	{
    		Station station = STATIONS.get(i);
    		if (station!=null)
    		{
    			System.out.println(station);
    		}
    	}
    }
    
    // getters and setters
    
    @Override
    public int getNextStationId()
    {
    	return NEXT_STATION_ID;
    }
}
