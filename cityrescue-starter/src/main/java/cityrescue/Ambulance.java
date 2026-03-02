package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitType;

public class Ambulance extends Unit{
    
    public Ambulance(int sId, int uId, int x, int y)
    {
        super(UnitType.AMBULANCE, sId, uId, x, y);
    }
    @Override
    public boolean canHandle(IncidentType type)
    {
        return type == IncidentType.MEDICAL;
    }
    @Override
    public int getTicksToResolve(int severity)
    {
        return 2;
    }
}
