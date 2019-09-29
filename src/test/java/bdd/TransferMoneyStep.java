package bdd;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by i316946 on 27/9/19.
 * Transfer balance step for JBehave to test success transfer money API
 */
public class TransferMoneyStep {

    private String accountA_id;
    private String accountB_id;
    private Response transfer_response;

    @Given("the server is up")
    public void checkServer(){
        RestAssured.port = Configs.port;
        get(Configs.root).then().body(containsString("Server is up and running!"));
    }

    @Given("the account with name AccountA is created")
    public void createAccountA(){
        String payload = "{\n" +
                "\t\"name\" : \"AccountA\"\n" +
                "}";
        accountA_id = given().contentType(ContentType.JSON)
                .body(payload)
                .post(Configs.create_account_url)
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
                .post(Configs.create_account_url)
                .then()
                .statusCode(200).extract().asString();
    }

    @When("the user transfer $amount from AccountA to AccountB")
    public void executeTransfer(@Named("amount") String amount){
        String payload = "{\n" +
                "\t\"from\" : \"%s\",\n" +
                "\t\"to\" : \"%s\",\n" +
                "\t\"value\" : \"%s\"\n" +
                "}";
        payload = String.format(payload, accountA_id, accountB_id, amount);
        transfer_response = given().contentType(ContentType.JSON)
                .body(payload)
                .post(Configs.transfer_url);
    }

    @Then("the server response [status_code] and [body]")
    public void checkIfRequestOK(@Named("status_code") int status_code, @Named("body") String body){
        transfer_response.then().statusCode(status_code);
        String result = transfer_response.body().asString();
        assertThat(result, equalTo(body));
    }

    @Then("the account named AccountA has [new_balance_A]")
    public void checkAccountBalanceWithdrawal(@Named("new_balance_A") String new_balance){
        String balance = get(Configs.balance_url + accountA_id).body().asString();
        assertThat(balance, equalTo(new_balance));
    }

    @Then("the account named AccountB has [new_balance_B]")
    public void checkAccountBalanceDebit(@Named("new_balance_B") String new_balance){
        String balance = get(Configs.balance_url + accountB_id).body().asString();
        assertThat(balance, equalTo(new_balance));
    }
}
