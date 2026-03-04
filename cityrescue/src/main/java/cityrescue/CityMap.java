package cityrescue;

import cityrescue.exceptions.InvalidGridException;
import java.util.List;
import java.util.Map;

public class CityMap
{
    public int width;
    public int height;
    public String[][] grid;
    // keep last obstacle list so checkMove can consult it directly
    private java.util.List<Obstacle> obstacles;

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
                String cell = grid[i][u];
                if (cell == null || cell.equals("-"))
                {
                    // empty space
                    System.out.print("[-]");
                }
                else
                {
                    // show the marker inside brackets
                    System.out.print("[" + cell + "]");
                }
                // separator between squares
                if (u < grid[i].length - 1) System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void updateGrid(List<Obstacle> obstacles, Map<Integer, Station> stations, Map<Integer, Unit> units, Map<Integer, Incident> incidents)
    {
        // keep a copy of the obstacle list for checkMove
        this.obstacles = obstacles;
        // reset all cells to empty symbol
        for (int i = 0; i<width; i++)
        {
            for (int j = 0; j<height; j++)
            {
                grid[i][j] = "-";
            }
        }

        // obstacles (lowest display priority but block spawning)
        for (Obstacle obs : obstacles)
        {
            grid[obs.getX()][obs.getY()] = "X";
        }
        // units (can be under stations/incidents)
        for (Unit u : units.values())
        {
            int ux = u.getX();
            int uy = u.getY();
            if (!grid[ux][uy].equals("X")) // do not overwrite an obstacle
                grid[ux][uy] = "U";
        }
        // stations (override units, but not obstacles)
        for (Station s : stations.values())
        {
            int sx = s.getX();
            int sy = s.getY();
            if (!grid[sx][sy].equals("X"))
                grid[sx][sy] = "S";
        }
        // incidents (highest priority)
        for (Incident inc : incidents.values())
        {
            // only display incidents that are still in progress or reported; resolved/cancelled disappear
            if (inc.getStatus() == cityrescue.enums.IncidentStatus.REPORTED ||
                inc.getStatus() == cityrescue.enums.IncidentStatus.DISPATCHED)
            {
                grid[inc.getX()][inc.getY()] = "!";
            }
        }
    }

    public boolean checkMove(int x, int y, char direction)
    {
        int nx = x;
        int ny = y;
        // orientation must match CityRescueImpl.tick():
        // N decrements x, E increments y, S increments x, W decrements y
        switch(direction) 
		{
            case 'N': nx = x-1; break;
            case 'E': ny = y+1; break;
            case 'S': nx = x+1; break;
            case 'W': ny = y-1; break;
            default: return false;
        }
        // boundary check
        if (nx < 0 || nx >= width || ny < 0 || ny >= height) 
		{
            return false;
        }
        // obstacle check using maintained list (safer than relying solely on grid)
        if (obstacles != null) 
		{
            for (Obstacle o : obstacles) 
			{
                if (o.getX() == nx && o.getY() == ny) 
				{
                    return false;
                }
            }
        }
        // also fall back to grid text just in case
        if ("X".equals(grid[nx][ny])) 
		{
            return false;
        }
        return true;
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
