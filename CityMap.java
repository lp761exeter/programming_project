package cityrescue;

import cityrescue.exceptions.InvalidGridException;

public class CityMap extends CityRescueImpl 
{
	public int width;
	public int height;
	public String[][] grid;
	
	public CityMap(int w, int h) throws InvalidGridException 
	{
		width = w;
		height = h;
		if (width<0 && height<0)
        {
        	throw new InvalidGridException("Width or Height less than 0");
        }
        else
        {
        	grid = new String[width][height];
        }
	}
	
	public void printGrid()
	{
		for (int i = 0; i<grid.length; i++)
		{
			for (int u = 0; u<grid[i].length; u++)
			{
				if (grid[i][u]==null)
				{
					System.out.print("[ ] ");
				}
				
			}
			System.out.println();
		}
	}
	
	public void updateGrid()
	{
		boolean[][] obstacles = super.OBSTACLES;
		for (int i = 0; i<obstacles.length; i++)
		{
			for (int u = 0; u<obstacles[i].length; u++)
			{
				if (obstacles[i][u]==true)
				{
					grid[i][u]="X";
				}
				
			}
		}
		
	}
	
	public boolean checkMove(int x, int y, char direction)
	{
		boolean legal = true;
		if (direction=='N')
		{
			if (y+1>height || grid[y][x]=="block")
			{
				legal = false;
			}
		}
		if (direction=='E')
		{
			if (x+1>width || grid[y][x]=="block")
			{
				legal = false;
			}
		}
		if (direction=='S')
		{
			if (y-1<0 || grid[y][x]=="block")
			{
				legal = false;
			}
		}
		if (direction=='W')
		{
			if (x-1<0 || grid[y][x]=="block")
			{
				legal = false;
			}
		}
		return legal;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}
