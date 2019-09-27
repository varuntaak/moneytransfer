import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Created by i316946 on 27/9/19.
 */
public class RestAPITests {
    @Before
    public void setUp(){
        RestAssured.port = 8081;
    }

    @Test
    public void testGetServerStatusAPI(){
        get("/").then().body(containsString("Server is up and running!"));
    }

}
