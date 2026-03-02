package cityrescue;
import cityrescue.enums.*;
import java.util.ArrayList;

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
