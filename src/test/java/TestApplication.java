import akka.http.javadsl.server.Route;
import org.junit.Test;
import static org.junit.Assert.*;
import rev.Application;

/**
 * Created by i316946 on 20/9/19.
 */
public class TestApplication {

    @Test
    public void testRouteCreations() throws Exception {
        Application app = new Application();
        Route route = app.createRoute();
        assertNotNull(route);
    }
}
