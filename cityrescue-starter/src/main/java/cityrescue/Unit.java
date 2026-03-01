package cityrescue;
import cityrescue.enums.*;

public class Unit 
{
	UnitType type;
	UnitStatus status = UnitStatus.IDLE;
	int stationId;
	int unitId;
	
	public Unit(UnitType t, int sId, int uId)
	{
		type = t;
		stationId = sId;
		unitId = uId;
	}
}
