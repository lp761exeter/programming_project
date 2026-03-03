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
		grid = new String[width][height];
		for (int i = 0; i<grid.length; i++)
		{
			for (int u = 0; u<grid[0].length; u++)
			{
				grid[i][u] = "-";
			}
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
		for (int i = 0; i<OBSTACLES.size(); i++)
		{
			Obstacle obs = OBSTACLES.get(i);
			int x = obs.getX();
			int y = obs.getY();
			grid[x][y] = "X";
		}
		
	}
	
	public boolean checkMove(int x, int y, char direction)
	{
		boolean legal = true;
		if (direction=='N')
		{
			if (y+1>height || grid[y][x]=="X")
			{
				legal = false;
			}
		}
		if (direction=='E')
		{
			if (x+1>width || grid[y][x]=="X")
			{
				legal = false;
			}
		}
		if (direction=='S')
		{
			if (y-1<0 || grid[y][x]=="X")
			{
				legal = false;
			}
		}
		if (direction=='W')
		{
			if (x-1<0 || grid[y][x]=="X")
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
