package bdd;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by i316946 on 27/9/19.
 */
public class TransferBalanceStep {

    String accountA_id;
    String accountB_id;
    Response transfer_response;

    @Given("the server is up")
    public void checkServer(){
        RestAssured.port = 8081;
    }

    @Given("the account with name AccountA is created")
    public void createAccountA(){
        String payload = "{\n" +
                "\t\"name\" : \"AccountA\"\n" +
                "}";
        accountA_id = given().contentType(ContentType.JSON)
                .body(payload)
                .post("/createaccount")
                .then()
                .statusCode(200).extract().asString();
    }

    @Given("the account with name AccountB is created")
    public void createAccountB(){
        String payload = "{\n" +
                "\t\"name\" : \"AccountB\"\n" +
                "}";
        accountB_id = given().contentType(ContentType.JSON)
                .body(payload)
                .post("/createaccount")
                .then()
                .statusCode(200).extract().asString();
    }

    @When("the user transfer amount of 10 from AccountA to AccountB")
    public void executeTransfer(){
        String payload = "{\n" +
                "\t\"from\" : \"%s\",\n" +
                "\t\"to\" : \"%s\",\n" +
                "\t\"value\" : \"10\"\n" +
                "}";
        payload = String.format(payload, accountA_id, accountB_id);
        transfer_response = given().contentType(ContentType.JSON)
                .body(payload)
                .post("/transfermoney");
    }

    @Then("the server full fill the request as Success")
    public void checkIfRequestOK(){
        transfer_response.then().statusCode(200);
        String result = transfer_response.body().asString();
        assertThat(result, equalTo("true"));
    }

    @Then("the account named AccountA is debited of amount 10")
    public void checkAccountBalanceWithdrawal(){
        String balance = get("/balance/" + accountA_id).body().asString();
        assertThat(balance, equalTo("990"));
    }

    @Then("the account named AccountB is credit with amount 10")
    public void checkAccountBalanceDebit(){
        String balance = get("/balance/" + accountB_id).body().asString();
        assertThat(balance, equalTo("1010"));
    }
}
