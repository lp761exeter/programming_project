package cityrescue;

import cityrescue.*;
import cityrescue.enums.*;
import cityrescue.exceptions.*;

public class Simulate
{
    public static int sizeX = 5;
    public static int sizeY = 5;

    public static void main(String[] args) throws InvalidGridException, InvalidCapacityException
    {
        CityRescue cr = new CityRescueImpl();
        cr.initialise(sizeX,sizeY) ;
        System.out.println(cr.getStatus());
    }
}