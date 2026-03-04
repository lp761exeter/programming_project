package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * Main executor class that handles all city rescue operations and maintains the global state of the system
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

		// make sure map is cleared
		refreshMap();
    }

	/*
	 * Returns the size of the grid as an array of two integers [width, height]
	 */
	@Override
	public int[] getGridSize()
	{
		int[] size = new int[2];
		size[0] = MAP.getWidth();
		size[1] = MAP.getHeight();
		return size;
	}

	/*
	 * Adds an obstacle to the CityMap at (x,y).
	 * Throws an exception if the location is invalid or occupied by a station/unit/incident
	 */
    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException 
    {
        // check whether the obstacle coordinates are within the map bound
        if (x<0 || x>=MAP.getWidth() || y<0 || y>=MAP.getHeight())
        {   
            throw new InvalidLocationException("x or y is out bounds");
        }
        // ensure no station/unit/incident occupies this square
        for (Station s : STATIONS.values()) {
            if (s.getX()==x && s.getY()==y) {
                throw new InvalidLocationException("Cannot place obstacle on station");
            }
        }
        for (Unit u : UNITS.values()) {
            if (u.getX()==x && u.getY()==y) {
                throw new InvalidLocationException("Cannot place obstacle on unit");
            }
        }
        for (Incident inc : INCIDENTS.values()) {
            if (inc.getX()==x && inc.getY()==y) {
                throw new InvalidLocationException("Cannot place obstacle on incident");
            }
        }
        // create new obstacle object and add
        Obstacle obs = new Obstacle(x,y);
        OBSTACLES.add(obs);
        // refresh map to show obstacle
        refreshMap();
    }

    /*
     * removes an obstacle at (x,y). 
     * Throws an exception if the location is invalid. 
     * doesn't matter if there is an obstacle at (x,y), sets the square to empty.
     */
    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException 
	{
		// error if given coordinates are out of bound
    	if (x<0 || x>=MAP.getWidth() || y<0 || y>=MAP.getHeight())
        {
        	throw new InvalidLocationException("x or y is out bounds");
        }
        // if there is an obstacle at the location, remove the first one found
        for (int i = 0; i < OBSTACLES.size(); i++) {
            Obstacle o = OBSTACLES.get(i);
            if (o.getX() == x && o.getY() == y) {
                OBSTACLES.remove(i);
                break;
            }
        }
        // refresh map so any removed obstacle disappears
        refreshMap();
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
            throw new InvalidNameException("Name cannot be blank");
        }
        // check whether the station coordinates are within the map bound
        if (x<0 || x>=MAP.getWidth() || y<0 || y>=MAP.getHeight())
        {    
            throw new InvalidLocationException("Station out of Bounds");
        }
        // ensure no obstacle already occupies that square
        for (Obstacle o : OBSTACLES) 
		{
            if (o.getX()==x && o.getY()==y) 
			{
                throw new InvalidLocationException("Cannot place station on obstacle");
            }
        }
        Station station = new Station(name, NEXT_STATION_ID, x,y);
        int id = station.getStationId();
        STATIONS.put(id, station);
        NEXT_STATION_ID++;
        
        // update map representation
        refreshMap();

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
    		throw new IDNotRecognisedException("Station ID not found");
    	}

		Station station = STATIONS.get(stationId);
		// check if the station is empty
    	if (station.getNumUnits()>0)
    	{	
			throw new IllegalStateException("Station is not empty");
    	}

    	STATIONS.remove(stationId);
    }
	/*
	 * Set the maximum number of units a station can hold
	 * Capacity cannot be negative or less than the number of units currently stored in the station
	 */
    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException 
    {	
		//check if the station id exists in the station map
    	if (!STATIONS.containsKey(stationId))
    	{	
    		throw new IDNotRecognisedException("Station ID not found");
    	}

		Station station = STATIONS.get(stationId);
		// check whether the new capacity is valid, it cannot be less than 0
	   	if (maxUnits<0)
    	{	
    		throw new InvalidCapacityException("Capacity cannot be less than 0");
    	}
		// check if the new capacity is less than current stored units
    	if (maxUnits<station.getNumUnits())
    	{	
    		throw new InvalidCapacityException("Capacity cannot be less than currently stored units");
    	}
    	
    	station.setCapacity(maxUnits);
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
    		throw new InvalidUnitException("Invalid Unit Type");
    	}
		// check if the station id exists in the station map
    	if (!STATIONS.containsKey(stationId))
    	{	
    		throw new IDNotRecognisedException("Station ID not found");
    	}

    	Station station = STATIONS.get(stationId);

		// get the station coordinates
		int x = station.getX();
		int y = station.getY();
		// cannot create unit on obstacle
		for (Obstacle o : OBSTACLES) 
		{
			if (o.getX() == x && o.getY() == y) {
				throw new IllegalStateException("Cannot add unit on obstacle");
			}
		}

		int stationID = station.getStationId();
		Unit unit = type.create(type, stationID, NEXT_UNIT_ID, x, y);
		UNITS.put(NEXT_UNIT_ID, unit);
		station.addUnit(unit);
		NEXT_UNIT_ID++;
        
		// update map so the unit appears
        refreshMap();

        return NEXT_UNIT_ID-1;
    }

	/*
	 * Remove a unit from the system
	 * A unit cannot be decommissioned when responding or working in an incident
	 */
    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException 
    {	
		// check if the unit id exists in the global unit map 
    	if (!UNITS.containsKey(unitId))
    	{
    		throw new IDNotRecognisedException("Unit ID not found");
    	}

    	Unit unit = UNITS.get(unitId);
    	UnitStatus status = unit.getStatus();
		//if the unit is en-route or at scene, it cannot be decommission 
    	if (status==UnitStatus.EN_ROUTE || status==UnitStatus.AT_SCENE)
    	{
    		throw new IllegalStateException("Cannot decomission unit while operating");
    	}

    	UNITS.remove(unitId);

		// also need to remove the unit from its station
		int stationId = unit.getStationId();
		Station station = STATIONS.get(stationId);
		station.removeUnit(unitId);
		// if the unit is assigned to an incident, clear the assignment
		int incidentId = unit.getIncidentId();
		if (incidentId != -1)
		{
			Incident incident = INCIDENTS.get(incidentId);
			incident.clearUnit();
		}

		refreshMap();
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
     * the specification for this method is unclear
     * i assume they want that if the bool is true, the unit must be idle to be set OUT_OF_SERVICE, and if it's not IDLE the method does nothing.
     * if the bool is false, the unit is set OUT_OF_SERVICE no matter what
     */
    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException 
    {	
		//error if unit id not found
    	if (!UNITS.containsKey(unitId))
    	{
    		throw new IDNotRecognisedException("Unit ID not found");
    	}
    	
    	Unit unit = UNITS.get(unitId);
		// if the outOfService flag is true, only IDLE units can be set to OUT_OF_SERVICE
    	if (outOfService)
    	{	
    		if (unit.getStatus()!=UnitStatus.IDLE)
    		{	
    			throw new IllegalStateException("Unit must be IDLE to be set to OUT_OF_SERVICE");
    		}
			unit.setStatus(UnitStatus.OUT_OF_SERVICE);
    	}
		// if the outOfService flag is false, the unit is set to OUT_OF_SERVICE regardless of its current status
    	else
    	{	
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

		Unit unit = UNITS.get(unitId);
		String type = unit.getType().name();
		int home = unit.getStationId();
		// get coordinates of unit's current location
		int x = unit.getX();
		int y = unit.getY();

		String status = unit.getStatus().name();
		String incident = ""+unit.getIncidentId();
		if (incident.equals("-1"))
		{
			incident = "-";
		}

		int work = unit.getWork();
		// return formated string that shows the unit's complete information
		return String.format("U#%d TYPE=%s HOME=%d LOC=(%d,%d) STATUS=%s INCIDENT=%s WORK=%d",unitId,type,home,x,y,status,incident,work);
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
		if (x<0 || x>=MAP.getWidth() || y<0 || y>=MAP.getHeight())
		{
			throw new InvalidLocationException("X and Y cannot be out of bounds");
		}
		// cannot report incident on top of obstacle
		for (Obstacle o : OBSTACLES) {
			if (o.getX() == x && o.getY() == y) {
				throw new InvalidLocationException("Cannot report incident on obstacle");
			}
		}
		
		Incident inc = new Incident(NEXT_INCIDENT_ID, type, severity, x, y);
		INCIDENTS.put(NEXT_INCIDENT_ID, inc);
		NEXT_INCIDENT_ID++;

		// update map with new incident
		refreshMap();
		
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

        Incident inc = INCIDENTS.get(incidentId);
		switch (inc.getStatus())
		{	
			case REPORTED:
				inc.setStatus(IncidentStatus.CANCELLED);
				break;
			case DISPATCHED:
				int unitId = inc.getAssignedUnitId();
				Unit unit = UNITS.get(unitId);
				unit.clearIncident();
				inc.setStatus(IncidentStatus.CANCELLED);
				inc.assignUnit(-1);
				break;
			default:
				throw new IllegalStateException("Incident status must be REPORTED or DISPATCHED to cancel");
		}
	}

	/*
	 * Update the severity level of an active incident
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
		// make sure incident exists
		if (!INCIDENTS.containsKey(incidentId)) 
		{
			throw new IDNotRecognisedException("Incident ID not found");
		}

		Incident inc = INCIDENTS.get(incidentId);
		IncidentStatus status = inc.getStatus();
		// Incident cannot be RESOLVED or CANCELLED when escalating
		if (status==IncidentStatus.RESOLVED || status==IncidentStatus.CANCELLED)
		{
			throw new IllegalStateException("Incident cannot be RESOLVED or CANCELLED when escalating");
		}

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
    	
        return IDs;
    }

	/*
	 * prints a string that describes the incident
	 * format example:
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

		Incident inc = INCIDENTS.get(incidentId);
		String type = inc.getType().name();
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

			// if the incident is not in REPORTED status, it has a unit already assigned or is resolved/cancelled, so skip it
			if(incident.getStatus() != IncidentStatus.REPORTED)
			{
				continue;
			}

			Unit bestUnit = null;

			// set to max_value temporarily
			int bestDistance = Integer.MAX_VALUE;

			// obtain all unit ids and order them
			Integer[] unitIds = UNITS.keySet().toArray(new Integer[0]);
			java.util.Arrays.sort(unitIds);

			// search through all registered units
			for (int unitId : unitIds)
			{
				Unit unit = UNITS.get(unitId);
				
				// skip the unit that are not IDLE or cannot handle this incident type
				if(unit.getStatus() != UnitStatus.IDLE || !unit.canHandle(incident.getType()))
				{
					continue;
				}

				// use Shortest Manhattan distance (|𝑥 − 𝑥 | + |𝑦− 𝑦|) given in the instruction
				int distance = Math.abs(unit.getX() - incident.getX()) + Math.abs(unit.getY() - incident.getY());

				// if the distance is same then choose the unit that has smallar id
				if (bestUnit == null || distance < bestDistance || (distance == bestDistance && unit.getUnitId() < bestUnit.getUnitId()))
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
				// mark the incident as dispatched immediately so it can't be re-dispatched
				incident.setStatus(IncidentStatus.DISPATCHED);
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
			int incidentId = unit.getIncidentId();

			// skip units without incidents
			if(incidentId == -1)
			{
				continue;
			}

			Incident incident = INCIDENTS.get(incidentId);

			// move unit towards the incident location if en route
			if(unit.getStatus() == UnitStatus.EN_ROUTE)
			{
				int tx = incident.getX();
				int ty = incident.getY();
				int ux = unit.getX();
				int uy = unit.getY();
				int currDist = Math.abs(ux - tx) + Math.abs(uy - ty);

				// direction order N,E,S,W 
                char[] dirs = {'N','E','S','W'};
                int[] dx = {-1,0,1,0};
                int[] dy = {0,1,0,-1};

                boolean moved = false;
                // first pass: look for legal move that reduces Manhattan distance
                for(int i=0; i<4 && !moved; i++)
                {
                    char d = dirs[i];
                    int nx = ux + dx[i];
                    int ny = uy + dy[i];
                    // check if the move is legal; no moving into obstacles or out of bounds
                    if (!MAP.checkMove(ux, uy, d))
					{
						continue;
					} 
                    int newDist = Math.abs(nx-tx)+Math.abs(ny-ty);
                    if(newDist < currDist)
                    {
                        unit.setLocation(nx, ny);
                        moved = true;
                    }
                }
			// second pass: take first legal move in N,E,S,W order
			if(!moved)
			{
				for(int i=0; i<4 && !moved; i++)
				{
					char d = dirs[i];
						int nx = ux + dx[i];
						int ny = uy + dy[i];
						if(nx<0||nx>=MAP.getWidth()||ny<0||ny>=MAP.getHeight()) continue;
						if(!MAP.checkMove(ux, uy, d)) continue;
						unit.setLocation(nx, ny);
						moved = true;
					}
				}

				// if arrival reached
				ux = unit.getX(); uy = unit.getY();
				if(ux==tx && uy==ty)
				{
					unit.arriveAtScene(incident.getSeverity()); // begin incident work
					incident.setStatus(IncidentStatus.DISPATCHED);
				}
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
        // update map (units may have moved)
        refreshMap();
        // advance simulation time
        TICK++;
    }

	/*
	 * Generates a summary report of the simulation.
	 * Including stations, units, incidents and obstacles.
	 * Format example:
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
	@Override
    public String getStatus()
    {
		String report = String.format("TICK=%d\nSTATIONS=%d UNITS=%d INCIDENTS=%d OBSTACLES=%d\nINCIDENTS\n",TICK,STATIONS.size(),UNITS.size(),INCIDENTS.size(),OBSTACLES.size());
		Integer[] incKeyset = INCIDENTS.keySet().toArray(new Integer[0]);
		for (int i=0; i<incKeyset.length; i++)
		{
			int key = incKeyset[i];
			try 
			{
				report+=viewIncident(INCIDENTS.get(key).getIncidentId())+"\n";
			}
			catch (Exception IDNotRecognisedException)
			{
				System.out.println("ID not recoginised");
			}
			
		}

		report+="UNITS\n";
		Integer[] unitKeyset = UNITS.keySet().toArray(new Integer[0]);
		for (int i=0; i<unitKeyset.length; i++)
		{
			int key = unitKeyset[i];
			try 
			{
				report+=viewUnit(UNITS.get(key).getUnitId())+"\n";
			}
			catch (Exception IDNotRecognisedException)
			{
				System.out.println("ID not recoginised");
			}
		}
		// take off the last newline character
		return report.substring(0,report.length()-1);
    }

	// keep the map representation in sync with the current model state
	private void refreshMap()
	{
		if (MAP != null) {
			MAP.updateGrid(OBSTACLES, STATIONS, UNITS, INCIDENTS);
		}
	}

	@Override
	public void printMap()
	{
		MAP.printGrid();
	}
    
    // getters and setters
    
    @Override
    public int getNextStationId()
    {
    	return NEXT_STATION_ID;
    }
}
