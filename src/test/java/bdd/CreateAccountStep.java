package bdd;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import rev.Application;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by i316946 on 27/9/19.
 * JBehave step for create account story.
 */
public class CreateAccountStep {
    private Response response;
    private Response get_response;
    private String account_id;

    @Given("the server is up")
    public void checkServer(){
        RestAssured.port = Configs.port;
        get(Configs.root).then().body(containsString("Server is up and running!"));
    }

    @When("the user submit a post request to [URL] with required payload")
    public void postCreateAccount(@Named("URL") String create_account_url){
        String payload = "{\n" +
                "\t\"name\" : \"User1\"\n" +
                "}";
        response = given().contentType(ContentType.JSON)
                .body(payload)
                .post(create_account_url);
    }

    @Then("the server reply with [status_code]")
    public void checkIfTheAccountCreatedSuccessfully(@Named("status_code") int status_code){
        response.then().statusCode(status_code);
        account_id = response.then().statusCode(status_code).extract().asString();
        assertThat(account_id, notNullValue());
    }

    @When("the user get the account balance of the account")
    public void getTheAccountBalanceOfNewlyCreatedAccount(){
        get_response = get(Configs.balance_url + account_id);
    }

    @Then("the account balance is shown as $balance")
    public void checkIfAccountExist(String balance){
        String s = get_response.body().asString();
        assertThat(s, equalTo(balance));
    }
}
