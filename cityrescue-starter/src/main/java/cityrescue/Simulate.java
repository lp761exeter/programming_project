package cityrescue;

import cityrescue.*;
import cityrescue.enums.*;
import cityrescue.exceptions.*;

public class Simulate
{
    public static int sizeX = 5;
    public static int sizeY = 5;

    public static void main(String[] args) throws InvalidGridException, InvalidCapacityException, InvalidNameException, InvalidSeverityException, IDNotRecognisedException, InvalidLocationException, InvalidUnitException, IllegalStateException
    {
        CityRescue cr = new CityRescueImpl();
        cr.initialise(sizeX,sizeY);
        int s1 = cr.addStation("Station 1", 0, 0);
        int s2 = cr.addStation("Station 2", 4, 4);
        cr.reportIncident(IncidentType.FIRE, 3, 2, 2);
        cr.reportIncident(IncidentType.MEDICAL, 2, 1, 1);
        cr.addUnit(s1, UnitType.FIRE_ENGINE);
        cr.addUnit(s2, UnitType.AMBULANCE);
        cr.addUnit(s2, UnitType.FIRE_ENGINE);
        cr.initialise(sizeX,sizeY);
        System.out.println(cr.getStatus());
    }
}