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
    void removeStations_test() throws Exception
    {
    	int id1 = cr.addStation("Central", 1, 1);
        int id2 = cr.addStation("North", 1, 2);
        cr.removeStation(id1);
        assertThrows(IDNotRecognisedException.class, () -> cr.removeStation(id1));
    }
}