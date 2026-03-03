package cityrescue;
import cityrescue.enums.IncidentStatus;
import cityrescue.enums.IncidentType;

public class Incident {
	
    private IncidentType type;
	private IncidentStatus status;
	private int incidentId;
    private int assignedUnitId;
    private int severity;
	private int x;
	private int y;
	
	public Incident(int incidentId, IncidentType type, int severity, int x, int y)
	{
		this.type = type;
		this.status = IncidentStatus.REPORTED;
		this.incidentId = incidentId;
		this.assignedUnitId = -1;
		this.severity = severity;
		this.x = x;
		this.y = y;
	}

	// getters

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
	
	// setters

	public void setStatus(IncidentStatus s)
	{
		this.status = s;
	}

	public void setSeverity(int severity)
	{
		this.severity = severity;
	}
}
