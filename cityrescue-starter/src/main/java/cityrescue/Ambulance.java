package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitType;

public class Ambulance extends Unit
{

	public Ambulance(UnitType t, int sId, int uId, int x, int y) 
	{
		super(t, sId, uId, x, y);
	}

	@Override
	public boolean canHandle(IncidentType type) 
	{
		return (type==IncidentType.MEDICAL?true:false);
	}

	@Override
	public int getTicksToResolve(int severity) 
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
