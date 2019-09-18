package rev.account.command;

import rev.account.exceptions.IllegalOperationException;
import rev.accounts.Account;

import java.math.BigDecimal;

/**
 * Created by i316946 on 17/9/19.
 */
public class WithdrawalCommand implements AccountCommand {
    private BigDecimal value;
    private Account account;
    private boolean canRollback = false;

    public WithdrawalCommand(BigDecimal value, Account account) {
        this.value = value;
        this.account = account;
    }

    @Override
    public void execute() throws IllegalOperationException {
        try {
            this.account.withdrawMoney(this.value);
            this.canRollback = true;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new IllegalOperationException(ex.getMessage());
        }
    }

    @Override
    public void rollback() {
        if (canRollback){
            this.account.depositMoney(this.value);
            canRollback = false;
        }
    }
}
