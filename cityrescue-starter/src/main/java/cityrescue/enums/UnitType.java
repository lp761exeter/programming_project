package cityrescue.enums;

import cityrescue.Ambulance;
import cityrescue.FireEngine;
import cityrescue.PoliceCar;
import cityrescue.Unit;

public enum UnitType 
{ 
	AMBULANCE 
	{
        public Unit create(UnitType t, int sId, int uId, int x, int y) 
        { 
        	return new Ambulance(t, sId, uId, x, y); 
        }
    }, 
	FIRE_ENGINE
	{
        public Unit create(UnitType t, int sId, int uId, int x, int y) 
        { 
        	return new FireEngine(t, sId, uId, x, y); 
        }
    },  
	POLICE_CAR 
	{
        public Unit create(UnitType t, int sId, int uId, int x, int y)
        { 
        	return new PoliceCar(t, sId, uId, x, y); 
        }
    };
	
	public abstract Unit create(UnitType t, int sId, int uId, int x, int y);
}
