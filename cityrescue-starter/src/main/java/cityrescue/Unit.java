package cityrescue;
import cityrescue.enums.*;

public abstract class Unit 
{
	private UnitType type;
	private UnitStatus status = UnitStatus.IDLE;
	private int stationId;
	private int unitId;
	private int x;
	private int y;
	private int incidentId;
	private int workRemaining;
	
	public Unit(UnitType t, int sId, int uId, int x, int y)
	{
		this.type = t;
		this.stationId = sId;
		this.unitId = uId;
		this.x = x;
		this.y = y;
		this.incidentId = -1;
		this.workRemaining = 0;

	}

	// abstract methods
	public abstract boolean canHandle(IncidentType type);
	public abstract int getTicksToResolve(int severity);

	// methods

	public void assignIncident(int incidentId)
	{
		this.incidentId = incidentId;
		this.status = UnitStatus.EN_ROUTE;

	}

	public void arriveAtScene(int severity)
	{
		this.status = UnitStatus.AT_SCENE;
		this.workRemaining = getTicksToResolve(severity);
	}

	public void workTick()
	{
		if(workRemaining > 0)
		{
			workRemaining--;
		}
	}

	public boolean workFinished()
	{
		return workRemaining == 0;
	}

	public void clearIncident()
	{
		this.incidentId = -1;
		this.status = UnitStatus.IDLE;
		this.workRemaining = 0;
	}

	// getters
	public int getUnitId() 
	{
        return unitId;
    }
	
	public int getStationId() 
	{
        return stationId;
    }
	
	public UnitStatus getStatus()
	{
		return status;
	}

    public UnitType getType() 
    {
        return type;
    }

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getIncidentId()
	{
		return incidentId;
	}

	public int getWork()
	{
		return workRemaining;
	}

	// setters

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void setStatus(UnitStatus status)
	{
		this.status = status;
	}

	public void setStationId(int stationId)
	{
		this.stationId = stationId;
	}
}

