package rev.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

/**
 * Created by i316946 on 20/9/19.
 */
public class DepositModel {
    private String amount;
    private String account_id;

    @Inject
    @JsonCreator
    public DepositModel(@JsonProperty("amount") String amount,
                         @JsonProperty("account_id") String account_id){
        this.amount = amount;
        this.account_id = account_id;
    }

    public String getAmount() {
        return amount;
    }

    public String getAccount_id() {
        return account_id;
    }

}
