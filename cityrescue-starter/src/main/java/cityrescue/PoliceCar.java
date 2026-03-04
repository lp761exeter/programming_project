package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitType;

public class PoliceCar extends Unit
{

	public PoliceCar(UnitType t, int sId, int uId, int x, int y) 
	{
		super(t, sId, uId, x, y);
	}

	@Override
	public boolean canHandle(IncidentType type) 
	{
		return (type==IncidentType.CRIME?true:false);
	}

	@Override
	public int getTicksToResolve(int severity) 
	{
		// police cars take three ticks to resolve any incident
		return 3;
	}

}