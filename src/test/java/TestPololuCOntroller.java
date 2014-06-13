import com.skoky.pololu.maestro.MaestroController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Thread.sleep;

/**
 * Created by ladislav.skokan on 13.6.2014.
 */
public class TestPololuController {

    private MaestroController controller;

    @Before
    public void setUp() throws Exception {
        controller = new MaestroController("COM29",true);
    }

    @Test
    public void testServoPositions() throws Exception {
        controller.setPosition(6,900);
        sleep(1000);
        controller.setPosition(6,1500);
        sleep(1000);
        assert true;
    }

    @After
    public void tearDown() throws Exception {
        if (controller!=null)
            controller.close();
    }
}
