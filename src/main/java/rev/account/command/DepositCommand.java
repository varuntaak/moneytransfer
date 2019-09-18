package rev.account.command;

import rev.account.exceptions.IllegalOperationException;
import rev.accounts.Account;

import java.math.BigDecimal;

/**
 * Created by i316946 on 17/9/19.
 */
public class DepositCommand implements AccountCommand {

    private BigDecimal value;
    private Account account;

    public DepositCommand(BigDecimal value, Account account){
        this.value = value;
        this.account = account;
    }

    @Override
    public void execute() throws IllegalOperationException {
        try {
            this.account.depositMoney(value);
        } catch (Exception ex){
            ex.printStackTrace();
            throw new IllegalOperationException(ex.getMessage());
        }
    }

    @Override
    public void rollback() {
        this.account.withdrawMoney(value);
    }

}
