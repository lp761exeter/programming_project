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
	public int NEXT_INCIDENT_ID;
	public ArrayList<Obstacle> OBSTACLES;
	public Map<Integer, Station> STATIONS;
	public Map<Integer, Unit> UNITS;
	public Map<Integer, Incident> INCIDENTS;
	
	// graded methods
	
	/*
	 * Initializes the global variables with the given grid width and height
	 * Create a new city map and reset stations, units, incidents and obstacles
	 */
    @Override
    public void initialise(int width, int height) throws InvalidGridException 
    {
		// ensure the grid dimensions are valid
    	if (width<=0 || height<=0) 
    	{
    		throw new InvalidGridException("Invalid grid dimensions");
    	}
		// create new city map
    	MAP = new CityMap(width,height);

		// reset simulation time
    	TICK = 0;

		// reset ID counters
    	NEXT_STATION_ID = 1;
    	NEXT_UNIT_ID = 1;
		NEXT_INCIDENT_ID = 1;

		// initialise the system collections
		OBSTACLES = new ArrayList<Obstacle>();
    	STATIONS = new HashMap<Integer, Station>();
		UNITS = new HashMap<Integer, Unit>();
		INCIDENTS = new HashMap<Integer, Incident>();
    }
    
    /*
     * Returns an int array with the board width at 0 and the board height at 1
     */
    @Override
    public int[] getGridSize() 
    {	
		//retrieve grid dimensions
    	int width = MAP.getWidth();
    	int height = MAP.getHeight();

		// return width and height
    	return new int[]{width,height};
    }
    
    /* 
     * adds an obstacle to the board at (x,y). 
     * Throws an exception if the location is invalid
     */
    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException 
    {
		// check whether the obstacle coordinates are within the map bound
        if (x<0 || x>MAP.width || y<0 || y>MAP.height)
        {	
			// throw exception if the location is out of map bound 
        	throw new InvalidLocationException("x or y is out bounds");
        }
		// create new obstacle object
        Obstacle obs = new Obstacle(x,y);

		// add the obstacle into the obstacle list
		OBSTACLES.add(obs);

		// Update the map grid to reflect the new added obstacle
		MAP.updateGrid();
    }
    
    /*
     * removes an obstacle at (x,y). 
     * Throws an exception if the location is invalid. 
     * doesn't matter if there is an obstacle at (x,y), sets the square to empty.
     */
    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException 
	{
		//error if given coordinates are out of bound
    	if (x<0 || x>MAP.width || y<0 || y>MAP.height)
        {
        	throw new InvalidLocationException("x or y is out bounds");
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
		// check if the station name is empty
    	if (name.length()==0)
    	{
			// throw exception if the name is blank
    		throw new InvalidNameException("Name cannot be blank");
    	}

		// check whether the station coordinates are within the map bound
    	if (x<0 || x>MAP.getWidth() || y<0 || y>MAP.getHeight())
    	{	
			// if the station location is out of map bound then throw exception
    		throw new InvalidLocationException("Station out of Bounds");
    	}
		// create new station object with a different id
        Station station = new Station(name, NEXT_STATION_ID, x,y);
		// get the station id
        int id = station.getStationId();
		// add the station into the station map
        STATIONS.put(id, station);
		// increment the next available station id
        NEXT_STATION_ID++;
        
		// return the new station id
        return id;
    }
    
    /*
     * removes a station with a given ID.
     * throws exceptions if the ID is not recognized or the station is not empty
     */
    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException 
    {	
		// check whether the station id exists in the station map
    	if (!STATIONS.containsKey(stationId))
    	{	
			// if not found then throw exception
    		throw new IDNotRecognisedException("Station ID not found");
    	}
		// get station object corresponding to the id
		Station station = STATIONS.get(stationId);

		// check if the station is empty
    	if (station.getNumUnits()<=0)
    	{	
			// if empty, then remove the station from the map
    		STATIONS.remove(stationId);
    	}
    	else
    	{
			//if the station is not empty, throw IllegalStateException
    		throw new IllegalStateException("Station is not empty");
    	}
    	
    }
	/*Set the maximum number of units a station can hold
	 *Capacity cannot be negative or less than the number of units currently stored in the station
	 */
    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException 
    {	
		//check if the station id exists in the station map
    	if (!STATIONS.containsKey(stationId))
    	{	
			//if not, throw IDNotRecognisedException
    		throw new IDNotRecognisedException("Station ID not found");
    	}
		// get the station object corresponding to the id
		Station station = STATIONS.get(stationId);

		// check whether the new capacity is valid, it cannot be less than 0
	   	if (maxUnits<0)
    	{	
			// throw InvalidCapacityException if it less than 0
    		throw new InvalidCapacityException("Capacity cannot be less than 0");
    	}
		// check if the new capacity is less than current stored units
    	else if (station.getNumUnits()>=maxUnits)
    	{	
			//throw IllegalStateException if capacity cannot the existing units
    		throw new IllegalStateException("Capacity cannot be less than currently stored units");
    	}
    	else
    	{	// set new capacity for station
    		station.setCapacity(maxUnits);
    	}
    }
    
    /*
     * Returns a list of the ids of every active station in ascending order
     */
    @Override
    public int[] getStationIds()
    {	
		// obtain all station ids as an integer array
    	Integer[] temp = STATIONS.keySet().toArray(new Integer[0]);
		//create a corresponding primitive int array
    	int[] IDs = new int[temp.length];
    	
		// convert each integer into int
    	for (int i = 0; i < temp.length; i++) 
    	{
    	    IDs[i] = temp[i];
    	}
    	//return the arrays of station ids
        return IDs;
    }
	/*
	 * Create a new emergency unit of the given type
	 * Assign it to the specified station
	 * The unit starts at the station location
	 */
    @Override
    public int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException 
    {	
		// check if the unit type is null
    	if (type==null)
    	{	
			// throw exception if the unit type is invalid
    		throw new InvalidUnitException("Invalid Unit Type");
    	}
		// check if the station id exists in the station map
    	if (!STATIONS.containsKey(stationId))
    	{	
			// if station id is not found, throw IDNotRecognisedException
    		throw new IDNotRecognisedException("Station ID not found");
    	}
    	// get the station object corresponding to the id
    	Station station = STATIONS.get(stationId);

		// get the station coordinates
		int x = station.getX();
		int y = station.getY();
		// get the station id
		int stationID = station.getStationId();
		// create new unit object of the specific type
		Unit unit = type.create(type, stationID, NEXT_UNIT_ID, x, y);
		// add the unit into the global unit map
		UNITS.put(NEXT_UNIT_ID, unit);
		// add the unit to the station
		station.addUnit(unit);
		// increment the next valid unit id
		NEXT_UNIT_ID++;
        // return id of the new created unit
        return NEXT_UNIT_ID-1;
    }
	/*
	 * Remove a unit from the system
	 * A unit cannot be decommissioned when responding or working in an incident
	 *
	 */
    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException 
    {	
		// check if the unit id exists in the global unit map 
    	if (!UNITS.containsKey(unitId))
    	{	
			// if unit id is not found then throw IDNotRecognisedException
    		throw new IDNotRecognisedException("Station ID not found");
    	}
		//get the unit object corresponding to the id
    	Unit unit = UNITS.get(unitId);
		//get the current status of the unit
    	UnitStatus status = unit.getStatus();
		//if the unit is en-route or at scene, it cannot be decommission 
    	if (status==UnitStatus.EN_ROUTE || status==UnitStatus.AT_SCENE)
    	{	// if the unit is currently operating, throw IllegalStateException
    		throw new IllegalStateException("Cannot decomission unit while operating");
    	}
    	// remove the unit from the global unit map
    	UNITS.remove(unitId);
    }
	/*
	 * Transfer a idle unit from its current station to another station
	 * The station of destination must have available capacity
	 */
    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException 
    {
    	// error if unit or station not found
    	if (!UNITS.containsKey(unitId))
    	{
    		throw new IDNotRecognisedException("Unit ID not found");
    	}
    	if (!STATIONS.containsKey(newStationId))
    	{
    		throw new IDNotRecognisedException("Station ID not found");
    	}
    	// get the unit and the new station objects
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
    	// get the old station and remove the unit from it 
    	Station oldStation = STATIONS.get(unit.getStationId());
    	oldStation.removeUnit(unitId);
		// add the unit to the new station
    	newStation.addUnit(unit);
		// update the station id of unit
    	unit.setStationId(newStationId);
    	//update the unit location to the new station coordinates
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
    {	//error if unit id not found
    	if (!UNITS.containsKey(unitId))
    	{
    		throw new IDNotRecognisedException("Unit ID not found");
    	}
    	
    	Unit unit = UNITS.get(unitId);
    	if (outOfService)
    	{	
			// if we want to set the unit as out of service
    		if (unit.getStatus()==UnitStatus.IDLE)
    		{	
				//only IDLE units can be set to OUT_OF_SERVICE
    			unit.setStatus(UnitStatus.OUT_OF_SERVICE);
    		}
    		else
    		{	// throw new IllegalStateException if unit is not IDLE
    			throw new IllegalStateException("Unit must be IDLE to be set to OUT_OF_SERVICE");
    		}
    	}
    	else
    	{	// if setting back to service, set status to OUT_OF_SERVICE
    		unit.setStatus(UnitStatus.OUT_OF_SERVICE);
    	}
    }
	/*
	 * Return the ids of all units currently registered in the system
	 */
    @Override
    public int[] getUnitIds() 
    {	// obtain all unit ids as an integer array
    	Integer[] temp = UNITS.keySet().toArray(new Integer[0]);
		//create a corresponding primitive int array
    	int[] IDs = new int[temp.length];
    	//for each integer, convert to int 
    	for (int i = 0; i < temp.length; i++) 
    	{
    	    IDs[i] = temp[i];
    	}
    	// return array of unit ids
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
		// error if unit id not found
        if (!UNITS.containsKey(unitId))
		{
			throw new IDNotRecognisedException("Unit ID not found");
		}
		//get unit object corresponding to the id
		Unit unit = UNITS.get(unitId);
		//get unit type
		String type = unit.getType().name();
		//get unit'home station id
		int home = unit.getStationId();
		// get coordinates of unit's current location
		int x = unit.getX();
		int y = unit.getY();
		//get unit's current status
		String status = unit.getStatus().name();
		//get the incident id that the unit is handling
		int incident = unit.getIncidentId();
		//get the unit's total work
		int work = unit.getWork();
		// return formated string that shows the unit's complete information
		return String.format("U#%d TYPE=%s HOME=%d LOC=(%d,%d) STATUS=%s INCIDENT=%d WORK=%d",unitId,type,home,x,y,status,incident,work);
    }
	/* 
	 * Create and record a new incident at specific location with a type and severity
	 */
    @Override
    public int reportIncident(IncidentType type, int severity, int x, int y) throws InvalidSeverityException, InvalidLocationException 
	{	
		// error if the incident type is null
        if (type==null)
		{
			throw new InvalidSeverityException("Type cannot be null");
		}
		// error if the severity out of bound
		if (severity<1 || severity>5)
		{
			throw new InvalidSeverityException("Severity must be 1 to 5 inclusive");
		}
		// error if the location is out of map bound
		if (x<0 || x>MAP.getWidth() || y<0 || y>MAP.getHeight())
		{
			throw new InvalidLocationException("X and Y cannot be out of bounds");
		}
		//create new incident object with a different id
		Incident inc = new Incident(NEXT_INCIDENT_ID, type, severity, x, y);
		// add the incident to the global incidents map
		INCIDENTS.put(NEXT_INCIDENT_ID, inc);
		// update next valid incident id
		NEXT_INCIDENT_ID++;
		//return the id of the new created incident
		return NEXT_INCIDENT_ID-1;
    }
	/*
	 * Cancel an incident if it has not been resolved
	 * Any assigned unit will be released from the incident
	 */
    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException 
	{	
		//error if incident id not found
		if (!INCIDENTS.containsKey(incidentId))
		{
			throw new IDNotRecognisedException("ID not found in INCIDENTS");
		}
		//get incident object corresponding to the id
        Incident inc = INCIDENTS.get(incidentId);
		// if the incident status is reported, set the incident status to cancelled
		if (inc.getStatus()==IncidentStatus.REPORTED)
		{
			inc.setStatus(IncidentStatus.CANCELLED);
		}
		// if incident status is dispatched
		else if (inc.getStatus()==IncidentStatus.DISPATCHED)
		{	
			// get assigned unit id
			int unitId = inc.getAssignedUnitId();
			// get unit object
			Unit unit = UNITS.get(unitId);
			//clear the incident assignment from the unit
			unit.clearIncident();
			// set the status to cancelled
			inc.setStatus(IncidentStatus.CANCELLED);
		}
		else//error if incident cannot be cancelled
		{
			throw new IllegalStateException("Incident must be REPORTED or DISPATCHED to cancel");
		}
    }
	/*
	 * Update the serverity level of an active incident
	 * Resolve or cancel incidents that cannot be escalated
	 */
    @Override
    public void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException 
	{	
		// error if the severity is out of bound
		if (newSeverity<1 || newSeverity>5)
		{
			throw new InvalidSeverityException("Severity must be 1 to 5 inclusive");
		}
		//get incident object corresponding to the id
		Incident inc = INCIDENTS.get(incidentId);
		//get current status of incident
		IncidentStatus status = inc.getStatus();
		// Incident cannot be RESOLVED or CANCELLED when escalating
		if (status==IncidentStatus.RESOLVED || status==IncidentStatus.CANCELLED)
		{	// error if RESOLVED or CANCELLED
			throw new IllegalStateException("Incident cannot be RESOLVED or CANCELLED when escalating");
		}
		//set incident's severity to new level
		inc.setSeverity(newSeverity);
    }
	/*
	 * Return the ids of all incidents currently stored in the system
	 */
    @Override
    public int[] getIncidentIds() 
	{
		//get all incident idds as an integer array
        Integer[] temp = INCIDENTS.keySet().toArray(new Integer[0]);
		//create a corresponding primitive int array
    	int[] IDs = new int[temp.length];
    	
		// for each integer, convert to int 
    	for (int i = 0; i < temp.length; i++) 
    	{
    	    IDs[i] = temp[i];
    	}
    	//return the arraay of incident ids
        return IDs;
    }
	/*
	 * I#1 TYPE=FIRE SEV=4 LOC=(3,1) STATUS=IN_PROGRESS UNIT=2
	 */
    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException 
	{	
		// error if the incident id not found
        if (!INCIDENTS.containsKey(incidentId))
		{
			throw new IDNotRecognisedException("Incident ID not found");
		}
		// get incident object corresponding to the id
		Incident inc = INCIDENTS.get(incidentId);
		//get incident type
		String type = inc.getType().name();
		//get incident severity
		int severity = inc.getSeverity();
		//get incident coordinates
		int x = inc.getX();
		int y = inc.getY();
		//get incident status
		String status = inc.getStatus().name();

		String unit;
		// if no unit assigned, unit string is "-"
		if (inc.getAssignedUnitId()==-1)
		{
			unit = "-";
		}
		else
		{	//display the assigned unit id
			unit = ""+inc.getAssignedUnitId();
		}
		//return formatted string, show complete information
		return String.format("I#%d TYPE=%s SEV=%d LOC=(%d,%d) STATUS=%s UNIT=%s",incidentId,type,severity,x,y,status,unit);
    }
	/*
	 * Assings avalible idle units to reported incidents.
	 * The closest compatible unit is selected for each incident.
	 */
    @Override
    public void dispatch() 
	{	
		// obtain incident ids and process them in order
		Integer[] incidentIds = INCIDENTS.keySet().toArray(new Integer[0]);
		java.util.Arrays.sort(incidentIds);

		// attempt dispatch for each incident
		for (int incidentId : incidentIds)
		{	
			Incident incident = INCIDENTS.get(incidentId);
			// only the new reported incidents can receive units
			if(incident.getStatus() != IncidentStatus.REPORTED)
			{
				continue;
			}
			Unit bestUnit = null;

			// initialized to the max value
			int bestDistance = Integer.MAX_VALUE;

			// obtain all unit ids and order them
			Integer[] unitIds = UNITS.keySet().toArray(new Integer[0]);
			java.util.Arrays.sort(unitIds);

			// search through all registered units
			for(int unitId : unitIds)
			{
				Unit unit = UNITS.get(unitId);
				
				// skip the unit that are not idle
				if(unit.getStatus() != UnitStatus.IDLE)
				{
					continue;
				}

				// skip units that cannot handle this incident type
				if(!unit.canHandle(incident.getType()))
				{
					continue;
				}
				// use Shortest Manhattan distance (|𝑥 − 𝑥 | + |𝑦− 𝑦|) given in the instruction
				int distance = Math.abs(unit.getX() - incident.getX()) + Math.abs(unit.getY() - incident.getY());

				// if the distance is same then choose the unit that has smallar id
				if(bestUnit == null || distance < bestDistance || (distance == bestDistance && unit.getUnitId() < bestUnit.getUnitId()))
				{
					bestUnit = unit;
					bestDistance = distance;
				}
			}
			// Assign the incident if a suitable unit is found
			if(bestUnit != null)
			{
				bestUnit.assignIncident(incident.getIncidentId());
				incident.assignUnit(bestUnit.getUnitId());
			}

		}
    }
	/*
	 * Advances the simulation by one tick.
	 * Units move, work on incidents, and incidents can be resolved.
	 */
    @Override
    public void tick() 
	{	
		// iterate through every registered unit in the system
		for(Unit unit : UNITS.values())
		{	
			// obtain the incident assigned to the unit
			int incidentId = unit.getIncidentId();

			// skip units without incidents
			if(incidentId == -1)
			{
				continue;
			}

			Incident incident = INCIDENTS.get(incidentId);

			// move unit to the incident location
			if(unit.getStatus() == UnitStatus.EN_ROUTE)
			{
				unit.setLocation(incident.getX(), incident.getY());
				unit.arriveAtScene(incident.getSeverity()); // begin incident work
				incident.setStatus(IncidentStatus.DISPATCHED);
			}
			// perform work at incident
			else if(unit.getStatus() == UnitStatus.AT_SCENE)
			{
				unit.workTick();

				// resolve incident when finished
				if(unit.workFinished())
				{
					incident.setStatus(IncidentStatus.RESOLVED);
					unit.clearIncident();
				}
			}
		}
		// print updated map
    	MAP.printGrid();
        // advance simulation time
        TICK++;
    }
	/*
	 * TICK=7
	 * STATIONS=2 UNITS=3 INCIDENTS=2 OBSTACLES=5
	 * INCIDENTS
	 * I#1 TYPE=FIRE SEV=4 LOC=(3,1) STATUS=IN_PROGRESS UNIT=2
	 * I#2 TYPE=CRIME SEV=2 LOC=(0,4) STATUS=REPORTED UNIT=-
	 * UNITS
	 * U#1 TYPE=AMBULANCE HOME=1 LOC=(1,1) STATUS=IDLE INCIDENT=-
	 * U#2 TYPE=FIRE_ENGINE HOME=2 LOC=(3,1) STATUS=AT_SCENE INCIDENT=1 WORK=2
	 * U#3 TYPE=POLICE_CAR HOME=1 LOC=(1,2) STATUS=EN_ROUTE INCIDENT=2
	 */

	/*
	 * Generates a summary report of the simulation.
	 * Including stations, units, incidents and obstacles.
	 */
    @Override
    public String getStatus() 
    {	//create initial report string
		String report = String.format("TICK=%d\nSTATIONS=%d UNITS=%d INCIDENTS=%d OBSTACLES=%d\nINCIDENTS\n",TICK,STATIONS.size(),UNITS.size(),INCIDENTS.size(),OBSTACLES.size());
		//get all incident ids
		Integer[] incKeyset = INCIDENTS.keySet().toArray(new Integer[0]);
		for (int i=0; i<incKeyset.length; i++)
		{
			int key = incKeyset[i];

		}
		// return the report string
		return report;
    }
    
    // original methods
    /*
	 * Print information about all stations that currently active in the simulation
	 */
    @Override
    public void printStations()
    {	
		//for each station 
    	for (int i = 0; i<STATIONS.size(); i++)
    	{	
			//get the station object by i
    		Station station = STATIONS.get(i);
			// print information if the station is not null
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
