package cityrescue;
import cityrescue.enums.*;
import java.util.ArrayList;
import java.util.Map;

public class Station extends CityRescueImpl
{
	private int stationId;
	private int x;
	private int y;
	private String name;
	private int capacity = 10;
	private ArrayList<Unit> units = new ArrayList<Unit>();
	
	public Station(String n, int id, int xIn, int yIn)
	{
		stationId = id;
		x = xIn;
		y = yIn;
		name = n;
	}
	
	public void addUnit(Unit unit)
	{
		units.add(unit);
	}
	
	public void removeUnit(int unitId)
	{
		for (int i = 0; i<units.size(); i++)
		{
			if (units.get(i).getUnitId()==unitId)
			{
				units.remove(i);
				break;
			}
		}
	
	}
	
	// getters
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public int getStationId()
	{
		return stationId;
	}
	
	public int getNumUnits()
	{
		return units.size();
	}
	
	//setters
	
	public void setCapacity(int c)
	{
		capacity = c;
	}
	
	public String toString()
	{
		return String.format("Station[ID=%d,X=%d,Y=%d,Name=%s,Capacity=%d]", stationId,x,y,name,capacity);
	}
}
