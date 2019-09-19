import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import rev.AccountsModule;
import rev.account.command.AccountCommand;
import rev.account.command.DepositCommand;
import rev.account.command.TransferMoneyCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.CommandFailureException;
import rev.account.Account;

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

    Injector injector = Guice.createInjector(new AccountsModule());
    Account account1;
    Account account2;
    DepositCommand depositCommand;
    WithdrawalCommand withdrawalCommand;


    @Before
    public void setup(){
        BigDecimal value = new BigDecimal("10");
        this.account1 = injector.getInstance(Account.class);
        account1.depositMoney(value);
        this.withdrawalCommand = injector.getInstance(WithdrawalCommand.class);
        this.withdrawalCommand = new WithdrawalCommand(value, account1);
        this.account2 = Account.getInstance("23423");
        this.depositCommand = new DepositCommand(value, account2);

        withdrawalCommand = injector.getInstance(WithdrawalCommand.class);
        depositCommand = injector.getInstance(DepositCommand.class);

    }


    @Test
    public void testDepositCommand() throws CommandFailureException {
        BigDecimal value = new BigDecimal("10");
        DepositCommand depositCommand = injector.getInstance(DepositCommand.class);
        depositCommand.setValue(value);
        depositCommand.execute();
        assertTrue(depositCommand.getAccount().getBalance().compareTo(new BigDecimal("1010")) == 0);
    }

    @Test
    public void testWithdrawalCommand() throws CommandFailureException {
        BigDecimal value = new BigDecimal("10");
        WithdrawalCommand withdrawalCommand = injector.getInstance(WithdrawalCommand.class);
        withdrawalCommand.setValue(value);
        withdrawalCommand.execute();
        assertTrue(new BigDecimal("990").compareTo(withdrawalCommand.getAccount().getBalance()) == 0);
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
        Account account = withdrawalCommand.getAccount();
        withdrawalCommand.setValue(value);
        withdrawalCommand.rollback();
        assertTrue(new BigDecimal("1000").equals(account.getBalance()));
        withdrawalCommand.rollback();
        assertTrue(new BigDecimal("1000").equals(account.getBalance()));

        withdrawalCommand.execute();
        withdrawalCommand.rollback();
        assertTrue(new BigDecimal("1000").equals(account.getBalance()));
        withdrawalCommand.rollback();
        assertTrue(new BigDecimal("1000").equals(account.getBalance()));
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
        depositCommand.setValue(value);
        Account account = depositCommand.getAccount();
        depositCommand.rollback();
        assertTrue(new BigDecimal("1000").equals(account.getBalance()));
        depositCommand.rollback();
        assertTrue(new BigDecimal("1000").equals(account.getBalance()));

        depositCommand.execute();
        depositCommand.rollback();
        assertTrue(new BigDecimal("1000").equals(account.getBalance()));
        depositCommand.rollback();
        assertTrue(new BigDecimal("1000").equals(account.getBalance()));
    }

    @Test
    public void testTransferMoneyCommand() throws CommandFailureException {
        BigDecimal value = new BigDecimal("1000");
        TransferMoneyCommand command = injector.getInstance(TransferMoneyCommand.class);
        Account account1 = command.getWithdrawalCommand().getAccount();
        Account account2 = command.getDepositCommand().getAccount();
        command.getWithdrawalCommand().setValue(value);
        command.getDepositCommand().setValue(value);
        command.execute();
        assertTrue(account1.getBalance().compareTo(new BigDecimal("0")) == 0);
        assertTrue(account2.getBalance().compareTo(new BigDecimal("2000")) == 0);
    }

    @Test
    public void testTransferMoneyCommandForFailure() throws CommandFailureException {
        // deposit fails intentionally to test the rollback
        DepositCommand dc = mock(DepositCommand.class);
        doThrow(CommandFailureException.class).when(dc).execute();

        BigDecimal value = new BigDecimal("1000");
        TransferMoneyCommand command = injector.getInstance(TransferMoneyCommand.class);
        command.getWithdrawalCommand().setValue(value);
        command.setDepositCommand(dc);

        Account account1 = command.getWithdrawalCommand().getAccount();

        try {
            command.execute();
        } catch (Exception ex){}

        // Account1 balance is unchanged in case of partial failure.
        assertTrue(account1.getBalance().compareTo(new BigDecimal("1000")) == 0);


    }
}
