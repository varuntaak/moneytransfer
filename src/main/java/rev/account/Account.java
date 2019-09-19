package rev.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import rev.account.exceptions.IllegalValueException;
import rev.account.exceptions.InsuffificentBalance;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Created by i316946 on 15/9/19.
 */
public class Account {
    /***
     * Account class to hold account with an id and balance
     */

    final static Logger logger = Logger.getLogger(Account.class.getName());

    private String id;
    private BigDecimal balance;

    @Inject
    @JsonCreator
    public Account(@JsonProperty("id") String id,
            @JsonProperty("balance") @Named("INITIAL_BALANCE") BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    /***
     * Factory method to get an account with default value as zero.
     * @param id: String unique id for the account
     * @return Account instance
     */
    public static Account getInstance(String id){
        return new Account(id, new BigDecimal("0"));
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /***
     * This is to deposit money in this account
     * @param value: BigDecimal value to deposit money
     */
    public void depositMoney(BigDecimal value) {
        validateValue(value);
        synchronized (this){
            this.balance = this.balance.add(value);
        }
    }

    /***
     * Validates the value against null, -ve and scale greater than 2.
     * @param value
     */
    private void validateValue(BigDecimal value) {
        if (value == null)
            throw new IllegalValueException("The value can not be null");
        if (value.scale() > 2)
            throw new IllegalValueException("The value scale can not be more than 2. Some legal values are: 2.22, 3.45.");
        if (value.compareTo(new BigDecimal(0)) == -1)
            throw new IllegalValueException("The value must be a positive value");
    }

    /***
     * This is to withdraw money from this account.
     * @param value: BigDecimal value to withdraw
     */
    public void withdrawMoney(BigDecimal value){
        validateValue(value);
        synchronized (this){
            if (value.compareTo(this.balance) > 0)
                throw new InsuffificentBalance("The balance is not sufficient for this withdrawal");
            this.balance = this.balance.subtract(value);
        }
    }

    public void setId(String id) {
        this.id = id;
    }
}
