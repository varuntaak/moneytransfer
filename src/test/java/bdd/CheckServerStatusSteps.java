package bdd;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by i316946 on 27/9/19.
 * JBehave Step to check server status
 */
public class CheckServerStatusSteps {

    Response response;

    @Given("the server is up")
    public void checkServer(){
        RestAssured.port = Configs.port;
        get(Configs.root).then().body(containsString("Server is up and running!"));
    }

    @When("the user hit the [URL]")
    public void getTheRootOnTheServer(@Named("URL") String root){
        response = get(root);
    }

    @Then("the server reply [status_code] and [body]")
    public void verifyTheServerMessage(@Named("status_code") int stats_code,
                                       @Named("body") String body){
        response.then().statusCode(stats_code);
        response.then().body(containsString(body));
    }
}
