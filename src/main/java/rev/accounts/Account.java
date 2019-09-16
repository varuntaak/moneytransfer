package rev.accounts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import rev.account.exceptions.IllegalValueException;
import rev.account.exceptions.InsuffificentBalance;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Created by i316946 on 15/9/19.
 */
public class Account {

    final static Logger logger = Logger.getLogger(Account.class.getName());

    final String id;
    private BigDecimal balance;

    @JsonCreator
    Account(@JsonProperty("id") String id,
            @JsonProperty("balance") BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public static Account getInstance(String id){
        return new Account(id, new BigDecimal(0.00));
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void depositMoney(BigDecimal v) {
        validateValue(v);
        synchronized (this){
            this.balance = this.balance.add(v);
        }
        logger.info("Account Update - new balance: " + this.balance);
    }

    private void validateValue(BigDecimal value) {
        if (value == null)
            throw new IllegalValueException("The value can not be null");
        if (value.scale() > 2)
            throw new IllegalValueException("The value scale can not be more than 2. Some legal values are: 2.22, 3.45.");
        if (value.compareTo(new BigDecimal(0)) == -1)
            throw new IllegalValueException("The value must be a positive value");
    }

    public void withdrawMoney(BigDecimal v){
        validateValue(v);
        synchronized (this){
            if (v.compareTo(this.balance) > 0)
                throw new InsuffificentBalance("The balance is not sufficient for this withdrawal");
            this.balance = this.balance.subtract(v);
        }
        logger.info("Account Update - account: " + this.id + ", balance: " + this.balance);
    }
}
