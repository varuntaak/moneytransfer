package rev.account.command;

import com.google.inject.Inject;
import rev.account.exceptions.CommandFailureException;
import rev.account.Account;

import java.math.BigDecimal;

/**
 * Created by i316946 on 17/9/19.
 */
public class WithdrawalCommand implements AccountCommand {
    private BigDecimal value;
    private Account account;
    private boolean canRollback = false;

    @Inject
    public WithdrawalCommand(BigDecimal value, Account account) {
        this.value = value;
        this.account = account;
    }

    @Override
    public void execute() throws CommandFailureException {
        try {
            this.account.withdrawMoney(this.value);
            this.canRollback = true;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new CommandFailureException(ex.getMessage());
        }
    }

    @Override
    public void rollback() {
        if (canRollback){
            this.account.depositMoney(this.value);
            canRollback = false;
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
