package cityrescue;

import cityrescue.enums.IncidentType;
import cityrescue.enums.UnitType;

public class FireEngine extends Unit{
    
    public FireEngine(int sId, int uId, int x, int y)
    {
        super(UnitType.FIRE_ENGINE, sId, uId, x, y);
    }
    @Override
    public boolean canHandle(IncidentType type)
    {
        return type == IncidentType.FIRE;
    }
    @Override
    public int getTicksToResolve(int severity)
    {
        return 4;
    }
}
