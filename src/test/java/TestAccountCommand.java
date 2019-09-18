import org.junit.Test;
import rev.account.command.AccountCommand;
import rev.account.command.DepositCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.IllegalOperationException;
import rev.accounts.Account;

import java.math.BigDecimal;
import static org.junit.Assert.*;

/**
 * Created by i316946 on 17/9/19.
 */
public class TestAccountCommand {

    @Test
    public void testDepositCommand() throws IllegalOperationException {
        BigDecimal value = new BigDecimal("10");
        Account account = Account.getInstance("23423");
        AccountCommand ac = new DepositCommand(value, account);
        ac.execute();
        assertTrue(value.equals(account.getBalance()));
    }

    @Test
    public void testWithdrawalCommand() throws IllegalOperationException {
        BigDecimal value = new BigDecimal("10");
        Account account = Account.getInstance("23423");
        AccountCommand dac = new DepositCommand(value, account);
        dac.execute();
        AccountCommand wac = new WithdrawalCommand(value, account);
        wac.execute();
        assertTrue(new BigDecimal("0").equals(account.getBalance()));
    }

    /**
     * It test a legitimate withdrawal rollback
     * The rollback function executed before the command execute does not have any side effects.
     * Also upon calling multiple times does not have any side effects.
     * Rollback is happens only once.
     * @throws IllegalOperationException
     */
    @Test
    public void testWithdrawalCommandRollbackBeforeExecute() throws IllegalOperationException {
        BigDecimal value = new BigDecimal("10");
        Account account = Account.getInstance("23423");
        account.depositMoney(new BigDecimal("100"));
        AccountCommand wac = new WithdrawalCommand(value, account);
        wac.rollback();
        assertTrue(new BigDecimal("100").equals(account.getBalance()));
        wac.rollback();
        assertTrue(new BigDecimal("100").equals(account.getBalance()));

        wac.execute();
        wac.rollback();
        assertTrue(new BigDecimal("100").equals(account.getBalance()));
        wac.rollback();
        assertTrue(new BigDecimal("100").equals(account.getBalance()));
    }
}
