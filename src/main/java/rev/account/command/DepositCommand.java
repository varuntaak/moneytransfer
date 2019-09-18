package rev.account.command;

import rev.account.exceptions.CommandFailureException;
import rev.accounts.Account;

import java.math.BigDecimal;

/**
 * Created by i316946 on 17/9/19.
 */
public class DepositCommand implements AccountCommand {

    private BigDecimal value;
    private Account account;
    private boolean canRollback = false;

    public DepositCommand(BigDecimal value, Account account){
        this.value = value;
        this.account = account;
    }

    @Override
    public void execute() throws CommandFailureException {
        try {
            this.account.depositMoney(value);
            this.canRollback = true;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new CommandFailureException(ex.getMessage());
        }
    }

    @Override
    public void rollback() {
        if (canRollback){
            this.account.withdrawMoney(value);
            this.canRollback = false;
        }
    }

}
