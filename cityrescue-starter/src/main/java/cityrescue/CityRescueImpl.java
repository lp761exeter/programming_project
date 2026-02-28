package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

import java.util.ArrayList;

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
	public boolean[][] OBSTACLES;
	public ArrayList<Station> STATIONS;
	
	// graded methods
	
	/*
	 * initializes the global variables with the given grid width and height
	 */
    @Override
    public void initialise(int width, int height) throws InvalidGridException 
    {
    	MAP = new CityMap(width,height);
    	TICK = 0;
    	NEXT_STATION_ID = 1;
    	OBSTACLES = new boolean[width][height];
    	STATIONS = new ArrayList<Station>();
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
        NEXT_STATION_ID++;
        int id = station.getStationId();
        STATIONS.add(station);
        return id;
    }
    
    /*
     * removes a station with a given ID.
     * throws exceptions if the ID is not recognized or the station is not empty
     */
    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException 
    {
    	boolean found = false;
    	for (int i = 0; i<STATIONS.size(); i++)
    	{
    		Station s = STATIONS.get(i);
    		if (s.getStationId()==stationId)
    		{
    			found = true;
    			if (s.getCapacity()>0)
    	        {
    	        	throw new IllegalStateException("Cannot close non-empty station");
    	        }
    			STATIONS.remove(i);
    			break;
    		}
    	}
        if (!found)
        {
        	throw new IDNotRecognisedException("Invalid station ID");
        }
    }

    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /*
     * returns a list of the ids of every active station in ascending order
     */
    @Override
    public int[] getStationIds()
    {
        int length = 0;
        for (int i = 0; i<STATIONS.size(); i++)
        {
        	if (STATIONS.get(i)!=null)
        	{
        		length++;
        	}
        }
        int[] result = new int[length];
        int index = 0;
        for (int i = 0; i<STATIONS.size(); i++)
        {
        	Station station = STATIONS.get(i);
        	if (station!=null)
        	{
        		result[index] = station.getStationId();
        	}
        }
        return result;
    }

    @Override
    public int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int[] getUnitIds() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException {
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
