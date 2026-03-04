package cityrescue;

import cityrescue.*;
import cityrescue.enums.*;
import cityrescue.exceptions.*;
import java.util.Random;

public class Simulate
{
    public static int sizeX = 5;
    public static int sizeY = 5;
    public static int stations = 1;
    public static int units = 5;
    public static int incidents = 1;
    public static int obstacles = 5;
    public static int maxTicks = 10;
    public static CityRescue cr;

    public static void main(String[] args) throws InvalidGridException, InvalidCapacityException, InvalidNameException, InvalidSeverityException, IDNotRecognisedException, InvalidLocationException, InvalidUnitException, IllegalStateException
    {
        cr = new CityRescueImpl();
        cr.initialise(sizeX,sizeY);
        addObstacles();
        addStations();
        addUnits();
        addIncidents();
        System.out.println(cr.getStatus()+"\n");
        
        for (int i = 0; i < maxTicks; i++)
        {
            cr.printMap();
            cr.dispatch();
            cr.tick();
            System.out.println(cr.getStatus()+"\n");
        }
        
    }

    public static void addStations()
    {
        // place stations at random locations within the grid
        Random rand = new Random();
        for (int i = 0; i < stations; i++)
        {
            int x = rand.nextInt(sizeX);   // 0 .. sizeX-1
            int y = rand.nextInt(sizeY);   // 0 .. sizeY-1
            try
            {
                cr.addStation("Station " + (i + 1), x, y);
            }
            catch (InvalidNameException | InvalidLocationException e)
            {
                System.out.println("failed to place station: " + e.getMessage());
                i--;
            }
        }
    }

    public static void addIncidents()
    {
        // create incidents at random locations with random type and severity
        Random rand = new Random();
        IncidentType[] types = IncidentType.values();
        for (int i = 0; i < incidents; i++)
        {
            int x = rand.nextInt(sizeX);
            int y = rand.nextInt(sizeY);
            IncidentType type = types[rand.nextInt(types.length)];
            int severity = rand.nextInt(5) + 1; // 1..5
            try
            {
                cr.reportIncident(type, severity, x, y);
            }
            catch (InvalidSeverityException | InvalidLocationException e)
            {
                // should rarely happen; retry this index
                System.out.println("failed to report incident: " + e.getMessage());
                i--;
            }
        }
    }

    public static void addUnits()
    {
        // add units at random stations using random types
        Random rand = new Random();
        UnitType[] types = UnitType.values();
        int[] stationIds = cr.getStationIds();
        if (stationIds.length == 0) {
            System.out.println("no stations available to add units");
            return;
        }
        for (int i = 0; i < units; i++)
        {
            int stationId = stationIds[rand.nextInt(stationIds.length)];
            UnitType type = types[rand.nextInt(types.length)];
            try
            {
                cr.addUnit(stationId, type);
            }
            catch (IDNotRecognisedException | InvalidUnitException | IllegalStateException e)
            {
                // If the chosen station is full or invalid, try again
                System.out.println("failed to add unit: " + e.getMessage());
                i--;
                // optionally refresh station list if we suspect removal
                stationIds = cr.getStationIds();
                if (stationIds.length == 0) break;
            }
        }
    }

    public static void addObstacles()
    {
        // place obstacles at random locations within the grid
        Random rand = new Random();
        for (int i = 0; i < obstacles; i++)
        {
            int x = rand.nextInt(sizeX);
            int y = rand.nextInt(sizeY);
            try
            {
                cr.addObstacle(x, y);
            }
            catch (InvalidLocationException e)
            {
                System.out.println("failed to add obstacle: " + e.getMessage());
                i--;
            }
        }
    }}