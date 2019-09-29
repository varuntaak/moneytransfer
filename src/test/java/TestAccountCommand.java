import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import rev.AccountsModule;
import rev.account.Account;
import rev.account.command.DepositCommand;
import rev.account.command.TransferMoneyCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.CommandFailureException;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Created by i316946 on 17/9/19.
 * Test account command
 */
public class TestAccountCommand {

    private Injector injector = Guice.createInjector(new AccountsModule());
    private DepositCommand depositCommand;
    private WithdrawalCommand withdrawalCommand;


    @Before
    public void setup(){
        withdrawalCommand = injector.getInstance(WithdrawalCommand.class);
        depositCommand = injector.getInstance(DepositCommand.class);
    }


    @Test
    public void testDepositCommand() throws CommandFailureException {
        BigDecimal value = new BigDecimal("10");
        DepositCommand depositCommand = injector.getInstance(DepositCommand.class);
        depositCommand.setValue(value);
        //execute should deposit the money
        depositCommand.execute();
        assertTrue(depositCommand.getAccount().getBalance().compareTo(new BigDecimal("1010")) == 0);
        //rollback should revert the value
        depositCommand.rollback();
        assertTrue(depositCommand.getAccount().getBalance().compareTo(new BigDecimal("1000")) == 0);
        //subsequent rollback should not change any thing
        depositCommand.rollback();
        assertTrue(depositCommand.getAccount().getBalance().compareTo(new BigDecimal("1000")) == 0);
    }

    @Test(expected = CommandFailureException.class)
    public void testDepoistCommandFailure() throws CommandFailureException {
        Account account = mock(Account.class);
        doThrow(Error.class).when(account).depositMoney(BigDecimal.ONE);
        DepositCommand depositCommand = injector.getInstance(DepositCommand.class);
        depositCommand.setAccount(account);
        depositCommand.setValue(BigDecimal.ONE);
        depositCommand.execute();
    }

    @Test
    public void testWithdrawalCommand() throws CommandFailureException {
        BigDecimal value = new BigDecimal("10");
        WithdrawalCommand withdrawalCommand = injector.getInstance(WithdrawalCommand.class);
        withdrawalCommand.setValue(value);
        //execute should withdraw the money
        withdrawalCommand.execute();
        assertTrue(new BigDecimal("990").compareTo(withdrawalCommand.getAccount().getBalance()) == 0);
        //rollback should revert the value
        withdrawalCommand.rollback();
        assertTrue(new BigDecimal("1000").compareTo(withdrawalCommand.getAccount().getBalance()) == 0);
        //subsequent rollback should not change any thing
        withdrawalCommand.rollback();
        assertTrue(new BigDecimal("1000").compareTo(withdrawalCommand.getAccount().getBalance()) == 0);

    }

    @Test(expected = CommandFailureException.class)
    public void testWithdrawalCommandFailure() throws CommandFailureException {
        Account account = mock(Account.class);
        doThrow(Error.class).when(account).withdrawMoney(BigDecimal.ONE);
        WithdrawalCommand withdrawalCommand = injector.getInstance(WithdrawalCommand.class);
        withdrawalCommand.setAccount(account);
        withdrawalCommand.setValue(BigDecimal.ONE);
        withdrawalCommand.execute();
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
