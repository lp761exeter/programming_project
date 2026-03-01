package cityrescue;
import cityrescue.enums.*;

public abstract class Unit 
{
	private UnitType type;
	private UnitStatus status = UnitStatus.IDLE;
	private int stationId;
	private int unitId;
	
	public Unit(UnitType t, int sId, int uId)
	{
		type = t;
		stationId = sId;
		unitId = uId;
	}
	
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
}
