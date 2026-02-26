package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

/**
 * CityRescueImpl (Starter)
 *
 * Your task is to implement the full specification.
 * You may add additional classes in any package(s) you like.
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
	public Station[] STATIONS;
	
    @Override
    public void initialise(int width, int height) throws InvalidGridException 
    {
    	MAP = new CityMap(width,height);
    	TICK = 0;
    	NEXT_STATION_ID = 1;
    	OBSTACLES = new boolean[width][height];
    	STATIONS = new Station[MAX_STATIONS];
    }

    @Override
    public int[] getGridSize() 
    {
    	int width = MAP.getWidth();
    	int height = MAP.getHeight();
    	return new int[]{width,height};
    }

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

    @Override
    public int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException 
    {
        Station station = new Station(name,x,y);
        int id = station.stationID;
        STATIONS[id-1] = station;
        return id;
    }

    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int[] getStationIds() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
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
}
