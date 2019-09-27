package bdd;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Created by i316946 on 27/9/19.
 */
public class CreateAccountStep {
    Response response;
    Response get_response;
    String account_id;

    @Given("the server is up")
    public void checkServer(){
        RestAssured.port = 8081;
    }

    @When("the user submit a post request to /createaccount with required payload")
    public void postCreateAccount(){
        String payload = "{\n" +
                "\t\"name\" : \"User1\"\n" +
                "}";
        response = given().contentType(ContentType.JSON)
                .body(payload)
                .post("/createaccount");
    }

    @Then("the server reply with status 200")
    public void checkIfTheAccountCreatedSuccessfully(){
        response.then().statusCode(200);
        account_id = response.then().statusCode(200).extract().asString();
        assertThat(account_id, notNullValue());
    }

    @When("the user get the account balance of the account")
    public void getTheAccountBalanceOfNewlyCreatedAccount(){
        get_response = get("/balance/" + account_id);
    }

    @Then("the account balance is shown as 1000")
    public void checkIfAccountExist(){
        String s = get_response.body().asString();
        assertThat(s, equalTo("1000"));
    }
}
