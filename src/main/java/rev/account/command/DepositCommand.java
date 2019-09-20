package rev.account.command;

import com.google.inject.Inject;
import rev.account.exceptions.CommandFailureException;
import rev.account.Account;

import java.math.BigDecimal;

/**
 * Created by i316946 on 17/9/19.
 */
public class DepositCommand implements AccountCommand {

    private BigDecimal value;
    private Account account;
    private boolean canRollback = false;

    @Inject
    public DepositCommand(BigDecimal value, Account account){
        this.value = value;
        this.account = account;
    }

    @Override
    public void execute() throws CommandFailureException {
        try {
            this.account.depositMoney(value);
            this.canRollback = true;
        } catch (Error error){
            error.printStackTrace();
            throw new CommandFailureException(error.getMessage());
        }
    }

    @Override
    public void rollback() {
        if (canRollback){
            this.account.withdrawMoney(value);
            this.canRollback = false;
        }
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
