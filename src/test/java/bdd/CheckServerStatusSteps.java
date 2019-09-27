package bdd;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by i316946 on 27/9/19.
 */
public class CheckServerStatusSteps {

    Response response;

    @Given("the server is up")
    public void checkServer(){
        RestAssured.port = 8081;
    }

    @When("the user hit the root URL")
    public void getTheRootOnTheServer(){
        response = get("/");
    }

    @Then("the server reply saying it is up an running.")
    public void verifyTheServerMessage(){
        response.then().body(containsString("Server is up and running!"));
    }
}
