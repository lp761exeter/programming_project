package cityrescue;

public class Station extends CityRescueImpl
{
	public int stationID;
	public int x;
	public int y;
	public String name;
	
	public Station(String n, int xIn, int yIn)
	{
		stationID = super.NEXT_STATION_ID;
		super.NEXT_STATION_ID++;
		x = xIn;
		y = yIn;
		name = n;
	}
}
