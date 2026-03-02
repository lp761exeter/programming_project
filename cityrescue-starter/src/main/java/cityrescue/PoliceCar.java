package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitType;

public class PoliceCar extends Unit{
    
    public PoliceCar(int sId, int uId, int x, int y)
    {
        super(UnitType.POLICE_CAR, sId, uId, x, y);
    }
    @Override
    public boolean canHandle(IncidentType type)
    {
        return type == IncidentType.CRIME;
    }
    @Override
    public int getTicksToResolve(int severity)
    {
        return 3;
    }
}