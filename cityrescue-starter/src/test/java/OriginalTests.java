import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import cityrescue.*;
import cityrescue.enums.*;
import cityrescue.exceptions.*;

public class  OriginalTests
{
    private CityRescue cr;

    @BeforeEach
    void setUp() throws Exception {
        cr = new CityRescueImpl();
        cr.initialise(5,5);
    }
    
    @Test
    void getGridSize_Test()
    {
    	int[] size = cr.getGridSize();
    	assertArrayEquals(new int[] {5,5},size);
    }

    @Test
    void addStation_basic_andInvalid() throws Exception
    {
        int id1 = cr.addStation("Central", 1, 1);
        int id2 = cr.addStation("North", 2, 2);
        assertEquals(1, id1);
        assertEquals(2, id2);
        // out of bounds or empty name
        assertThrows(InvalidLocationException.class, () -> cr.addStation("Bad", -1, 0));
        assertThrows(InvalidNameException.class, () -> cr.addStation("", 0, 0));
    }

    @Test
    void removeStations_test() throws Exception
    {
    	int id1 = cr.addStation("Central", 1, 1);
        cr.removeStation(id1);
        assertThrows(IDNotRecognisedException.class, () -> cr.removeStation(id1));
    }

    @Test
    void setStationCapacity_and_getStationIds() throws Exception
    {
        int id = cr.addStation("A", 0, 0);
        cr.setStationCapacity(id, 3);
        assertThrows(InvalidCapacityException.class, () -> cr.setStationCapacity(id, -1));
        // add units so capacity cannot be lowered below current
        int u1 = cr.addUnit(id, UnitType.AMBULANCE);
        assertThrows(IllegalStateException.class, () -> cr.setStationCapacity(id, 0));
        int[] ids = cr.getStationIds();
        assertArrayEquals(new int[]{id}, ids);
    }

    @Test
    void addObstacle_and_removeObstacle() throws Exception
    {
        String before = cr.getStatus();
        cr.addObstacle(0,0);
        assertTrue(cr.getStatus().contains("OBSTACLES=1"));
        assertThrows(InvalidLocationException.class, () -> cr.addObstacle(5,5));
        cr.removeObstacle(0,0);
        // removing even empty location should not throw
        cr.removeObstacle(0,0);
    }

    @Test
    void unit_lifecycle_and_exceptions() throws Exception
    {
        final int sid = cr.addStation("S",1,1);
        final int uid = cr.addUnit(sid, UnitType.FIRE_ENGINE);
        assertArrayEquals(new int[]{uid}, cr.getUnitIds());
        assertTrue(cr.viewUnit(uid).contains("TYPE=FIRE_ENGINE"));
        assertThrows(IDNotRecognisedException.class, () -> cr.viewUnit(uid+1));
        // idle unit can be decommissioned
        cr.decommissionUnit(uid);
        assertFalse(java.util.Arrays.stream(cr.getUnitIds()).anyMatch(i -> i == uid));
        // try out-of-service transitions
        final int uid2 = cr.addUnit(sid, UnitType.FIRE_ENGINE);
        cr.setUnitOutOfService(uid2, true);
        assertThrows(IllegalStateException.class, () -> cr.setUnitOutOfService(uid2, true));
        cr.setUnitOutOfService(uid2, false);
    }

    @Test
    void decommission_and_transfer_edge() throws Exception
    {
        int s1 = cr.addStation("A",0,0);
        int s2 = cr.addStation("B",1,1);
        int u = cr.addUnit(s1, UnitType.POLICE_CAR);
        // transfer idle unit should succeed
        cr.transferUnit(u, s2);
        // make unit busy by reporting and dispatching an incident
        int inc = cr.reportIncident(IncidentType.CRIME, 1, 0, 1);
        cr.dispatch();
        assertThrows(IllegalStateException.class, () -> cr.transferUnit(u, s1));
    }

    @Test
    void incident_and_dispatch_and_tick() throws Exception
    {
        int s = cr.addStation("A",0,0);
        int u = cr.addUnit(s, UnitType.FIRE_ENGINE);
        int inc = cr.reportIncident(IncidentType.FIRE, 1, 4, 4);
        assertTrue(cr.getIncidentIds().length>0);
        assertTrue(cr.viewIncident(inc).contains("STATUS=REPORTED"));
        cr.dispatch();
        cr.tick();
        String status = cr.getStatus();
        assertTrue(status.contains("INCIDENTS"));
        assertTrue(status.contains("UNITS"));
        // escalate invalid severity
        assertThrows(InvalidSeverityException.class, () -> cr.escalateIncident(inc, 0));
        assertThrows(IDNotRecognisedException.class, () -> cr.escalateIncident(999, 3));
        // cancel after dispatch should work
        cr.cancelIncident(inc);
        assertThrows(IllegalStateException.class, () -> cr.cancelIncident(inc));
    }

    @Test
    void reportIncident_edgeCases() throws Exception
    {
        assertThrows(InvalidSeverityException.class, () -> cr.reportIncident(null, 1, 0, 0));
        assertThrows(InvalidSeverityException.class, () -> cr.reportIncident(IncidentType.CRIME, 6, 0, 0));
        assertThrows(InvalidLocationException.class, () -> cr.reportIncident(IncidentType.CRIME, 3, -1, 0));
    }

    @Test
    void cannot_spawn_on_obstacle() throws Exception 
    {
        cr.addObstacle(2,2);
        assertThrows(InvalidLocationException.class, () -> cr.addStation("Bad", 2, 2));
        assertThrows(InvalidLocationException.class, () -> cr.reportIncident(IncidentType.FIRE, 1, 2, 2));
    }

    @Test
    void enRouteMovement_towardsIncident() throws Exception {
        // fresh implementation to control placement
        cr = new CityRescueImpl();
        cr.initialise(5,5);
        int sid = cr.addStation("S", 0, 0);
        int uid = cr.addUnit(sid, UnitType.POLICE_CAR);
        int inc = cr.reportIncident(IncidentType.CRIME, 1, 0, 3);
        cr.dispatch();
        cr.tick();
        String status = cr.viewUnit(uid);
        assertTrue(status.contains("LOC=(0,1)"));
        cr.tick();
        status = cr.viewUnit(uid);
        assertTrue(status.contains("LOC=(0,2)"));
    }

    @Test
    void getNextStationId_incrementing() throws InvalidNameException, InvalidLocationException
    {
        assertEquals(1, cr.getNextStationId());
        cr.addStation("X",0,0);
        assertEquals(2, cr.getNextStationId());
    }

    @Test
    void enRoute_detour_via_fallback_order() throws Exception {
        // reproduce reported stuck pattern with obstacles blocking direct route
        cr = new CityRescueImpl();
        cr.initialise(5,5);
        int s = cr.addStation("Base",4,4);
        int u = cr.addUnit(s, UnitType.FIRE_ENGINE);
        cr.addObstacle(0,0);
        cr.addObstacle(1,2);
        cr.addObstacle(3,2);
        cr.addObstacle(3,3);
        cr.addObstacle(4,3);
        int inc = cr.reportIncident(IncidentType.FIRE, 3, 3, 0);
        cr.dispatch();
        boolean reached = false;
        for (int k = 0; k < 30; k++) {
            cr.tick();
            if (cr.viewUnit(u).contains("LOC=(3,0)")) {
                reached = true;
                break;
            }
        }
        assertTrue(reached, "unit should eventually make a detour around obstacles");
    }

    @Test
    void cannot_step_on_obstacle() throws Exception {
        // ensure a unit will not enter a cell occupied by a static obstacle
        cr = new CityRescueImpl();
        cr.initialise(3,3);
        int s = cr.addStation("S", 2, 0);
        cr.addObstacle(1,0);
        int u = cr.addUnit(s, UnitType.FIRE_ENGINE);
        int inc = cr.reportIncident(IncidentType.FIRE, 1, 0, 0);
        cr.dispatch();
        cr.tick();
        assertTrue(cr.viewUnit(u).contains("LOC=(2,0)"));
    }

    @Test
    void dispatch_does_not_assign_multiple_units() throws Exception {
        cr = new CityRescueImpl();
        cr.initialise(4,4);
        int s = cr.addStation("Base", 0, 0);
        int u1 = cr.addUnit(s, UnitType.AMBULANCE);
        int u2 = cr.addUnit(s, UnitType.AMBULANCE);
        int inc = cr.reportIncident(IncidentType.MEDICAL, 1, 2, 2);
        cr.dispatch();
        String status = cr.viewIncident(inc);
        assertTrue(status.contains("UNIT=" + u1) || status.contains("UNIT=" + u2));
        // change situation: make second unit idle still and call dispatch again
        cr.dispatch();
        // incident should still list same unit id as before
        String status2 = cr.viewIncident(inc);
        assertEquals(status, status2);
    }
}
