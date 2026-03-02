package cityrescue;
import cityrescue.enums.IncidentStatus;
import cityrescue.enums.IncidentType;

public class Incident {
	
    private IncidentType type;
	private IncidentStatus status;
    private int severity;
	private int x;
	private int y;
	private int incidentId;
    private int assignedUnitId;
	
	public Incident(int incidentId, IncidentType type, int severity, int x, int y)
	{

        this.incidentId = incidentId;
        this.type = type;
		this.x = x;
		this.y = y;
		this.assignedUnitId = -1;
        this.status = IncidentStatus.REPORTED;
        this.severity = severity;

	}

	public int getIncidentId() 
	{
        return incidentId;
    }
	
	public IncidentType getType() 
	{
        return type;
    }
	
	public int getSeverity()
	{
		return severity;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

    public IncidentStatus getStatus()
	{
		return status;
	}

    public int getAssignedUnitId()
	{
		return assignedUnitId;
	}

	public void assignUnit(int unitId)
	{
		this.assignedUnitId = unitId;
		this.status = IncidentStatus.DISPATCHED;
	}

	public void startWork()
	{
		this.status = IncidentStatus.IN_PROGRESS;
	}

	public void resolve()
	{
		this.status = IncidentStatus.RESOLVED;
	}

	public void cancel()
	{
		this.status = IncidentStatus.CANCELLED;
	}

	public void setSeverity(int severity)
	{
		this.severity = severity;
	}
}

