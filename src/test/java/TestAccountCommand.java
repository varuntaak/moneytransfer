import org.junit.Before;
import org.junit.Test;
import rev.account.command.AccountCommand;
import rev.account.command.DepositCommand;
import rev.account.command.TransferMoneyCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.CommandFailureException;
import rev.accounts.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Created by i316946 on 17/9/19.
 */
public class TestAccountCommand {
    Account account1;
    Account account2;
    AccountCommand depositCommand;
    AccountCommand withdrawalCommand;

    @Before
    public void setup(){
        BigDecimal value = new BigDecimal("10");
        this.account1 = Account.getInstance("23423");
        account1.depositMoney(value);
        this.withdrawalCommand = new WithdrawalCommand(value, account1);
        this.account2 = Account.getInstance("23423");
        this.depositCommand = new DepositCommand(value, account2);
    }


    @Test
    public void testDepositCommand() throws CommandFailureException {
        BigDecimal value = new BigDecimal("10");
        Account account = Account.getInstance("23423");
        AccountCommand ac = new DepositCommand(value, account);
        ac.execute();
        assertTrue(value.equals(account.getBalance()));
    }

    @Test
    public void testWithdrawalCommand() throws CommandFailureException {
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
     * Also upon calling rollback multiple times does not have any side effects.
     * Rollback happens only once.
     * @throws CommandFailureException
     */
    @Test
    public void testWithdrawalCommandRollbackBeforeExecute() throws CommandFailureException {
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


    /**
     * It test a legitimate deposit rollback
     * The rollback function executed before the command execute does not have any side effects.
     * Also upon calling rollback multiple times does not have any side effects.
     * Rollback happens only once.
     */
    @Test
    public void testDepositCommandRollabackBeforeCommandExecute() throws CommandFailureException {
        BigDecimal value = new BigDecimal("10");
        Account account = Account.getInstance("23423");
        account.depositMoney(new BigDecimal("100"));
        AccountCommand dc = new DepositCommand(value, account);
        dc.rollback();
        assertTrue(new BigDecimal("100").equals(account.getBalance()));
        dc.rollback();
        assertTrue(new BigDecimal("100").equals(account.getBalance()));

        dc.execute();
        dc.rollback();
        assertTrue(new BigDecimal("100").equals(account.getBalance()));
        dc.rollback();
        assertTrue(new BigDecimal("100").equals(account.getBalance()));
    }

    @Test
    public void testTransferMoneyCommand() throws CommandFailureException {
        List<AccountCommand> commandList = new ArrayList<>();
        commandList.add(this.withdrawalCommand);
        commandList.add(this.depositCommand);
        AccountCommand transferMoneyCommand = new TransferMoneyCommand(commandList);
        transferMoneyCommand.execute();
        assertTrue(this.account1.getBalance().compareTo(new BigDecimal("0")) == 0);
        assertTrue(this.account2.getBalance().compareTo(new BigDecimal("10")) == 0);
    }

    @Test
    public void testTransferMoneyCommandForFailure() throws CommandFailureException {
        // deposit fails intentionally to test the rollback
        DepositCommand dc = mock(DepositCommand.class);
        doThrow(CommandFailureException.class).when(dc).execute();

        List<AccountCommand> commandList = new ArrayList<>();
        commandList.add(this.withdrawalCommand);
        commandList.add(dc);

        AccountCommand transferMoneyCommand = new TransferMoneyCommand(commandList);
        try {
            transferMoneyCommand.execute();
        } catch (Exception ex){}

        // Account1 balance is unchanged in case of partial failure.
        assertTrue(this.account1.getBalance().compareTo(new BigDecimal("10")) == 0);


    }
}
