package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitType;

public class FireEngine extends Unit
{

	public FireEngine(UnitType t, int sId, int uId, int x, int y) 
	{
		super(t, sId, uId, x, y);
	}

	@Override
	public boolean canHandle(IncidentType type) 
	{
		return (type==IncidentType.FIRE?true:false);
	}

	@Override
	public int getTicksToResolve(int severity) 
	{
		// fire engines require four ticks regardless of severity
		return 4;
	}

}