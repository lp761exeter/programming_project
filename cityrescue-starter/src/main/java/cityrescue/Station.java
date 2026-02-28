package cityrescue;

public class Station extends CityRescueImpl
{
	private int stationId;
	private int x;
	private int y;
	private String name;
	private int capacity = 0;
	
	public Station(String n, int id, int xIn, int yIn)
	{
		stationId = id;
		x = xIn;
		y = yIn;
		name = n;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	public int getStationId()
	{
		return stationId;
	}
	
	public void setCapacity(int c)
	{
		capacity = c;
	}
	
	public String toString()
	{
		return String.format("Station[ID=%d,X=%d,Y=%d,Name=%s,Capacity=%d]", stationId,x,y,name,capacity);
	}
}
