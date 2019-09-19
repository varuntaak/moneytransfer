/**
 * Created by i316946 on 14/9/19.
 */
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import rev.AccountsModule;
import rev.account.command.DepositCommand;
import rev.account.command.TransferMoneyCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.CommandFailureException;
import rev.account.exceptions.InvalidAccountId;
import rev.account.Account;
import rev.account.AccountManager;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestAccountManager{

    Injector injector = Guice.createInjector(new AccountsModule());
    AccountManager accountManager;
    Account newAccount;
    TransferMoneyCommand command;

    @Before
    public void setUP() {
        accountManager = injector.getInstance(AccountManager.class);
        newAccount = injector.getInstance(Account.class);
        command = injector.getInstance(TransferMoneyCommand.class);

    }


    /**
     * Test create new account with INITIAL_BALANCE.
     * @throws DuplicateAccountIdException
     */
    @Test
    public void testCreateAccount() throws DuplicateAccountIdException {
        // create a new account check initial balance
        accountManager.createNewAccount(newAccount);
        System.out.println(newAccount.getBalance());
        assertTrue(newAccount.getBalance().compareTo(new BigDecimal("1000")) == 0);

        // test if the account has a valid uuid
        UUID uuid = UUID.fromString(newAccount.getId());
        assertTrue(uuid.toString().equals(newAccount.getId()));
        assertFalse(uuid.toString() == newAccount.getId());
    }

    @Test(expected = DuplicateAccountIdException.class)
    public void testCreateAccountWithUniqueness() throws DuplicateAccountIdException {
        //set up to have an account with the id
        UUID id = UUID.randomUUID();
        accountManager.createNewAccount(newAccount, id);
        assertTrue(newAccount.getBalance().equals(AccountManager.INITIAL_BALANCE));
        UUID uuid = UUID.fromString(newAccount.getId());
        assertTrue(uuid.toString().equals(newAccount.getId()));

        //create an account with existing id throws a DuplicateAccountIdException
        accountManager.createNewAccount(newAccount, id);
    }

    /** Test the getAccountBalance for various edge cases **/
    @Test
    public void testGetAccountBalanceWithValidId() throws DuplicateAccountIdException, InvalidAccountId {
        // get the balance of the new account and match to INITIAL_BALANCE
        accountManager.createNewAccount(this.newAccount);
        BigDecimal balance = accountManager.getAccountBalance(this.newAccount.getId());
        assertNotNull(balance);
        assertTrue(balance.equals(AccountManager.INITIAL_BALANCE));
        assertTrue(this.newAccount.getBalance().equals(accountManager.getAccountBalance(this.newAccount.getId())));
    }

    /** test get balance with invalid account id throws exception **/
    @Test(expected = InvalidAccountId.class)
    public void testInvalidAccountIdAsNotUUID() throws InvalidAccountId {
        accountManager.getAccountBalance("423423iurewr");
    }

    /** test get balance with invalid account id throws exception **/
    @Test(expected = InvalidAccountId.class)
    public void testInvalidAccountIdAsNull() throws InvalidAccountId {
        accountManager.getAccountBalance(null);
    }

    /** Test the money transfer for null values **/
    @Test(expected = InvalidAccountId.class)
    public void testTransferMoneyInvalidInput1() throws InvalidAccountId {
        accountManager.transferMoney(null, null, "12", command);
    }

    /** Test the money transfer for invalid account id for debit account. **/
    @Test(expected = InvalidAccountId.class)
    public void testTransferMoneyInvalidInput2() throws InvalidAccountId, DuplicateAccountIdException {
        UUID uuid = UUID.randomUUID();
        accountManager.createNewAccount(newAccount, uuid);
        accountManager.transferMoney("dfsdf", uuid.toString(), "12", command);
    }

    /** Test the transfer money for invalid account id of the beneficiary account **/
    @Test(expected = InvalidAccountId.class)
    public void testTransferMoneyInvalidInput3() throws InvalidAccountId, DuplicateAccountIdException {
        UUID uuid = UUID.randomUUID();
        accountManager.createNewAccount(newAccount, uuid);
        accountManager.transferMoney( uuid.toString(), "ewrewr", "12", command);
    }

    /** Test the transfer money when both accounts are have the invalid account id **/
    @Test(expected = InvalidAccountId.class)
    public void testTransferMoneyInvalidInput4() throws InvalidAccountId {
        accountManager.transferMoney("dfsdf", "erwer", "12", command);
    }

    /** Test the transfer money for a valid case **/
    @Test
    public void testTransferMoney() throws DuplicateAccountIdException, InvalidAccountId {
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        accountManager.createNewAccount(newAccount, u1);
        Account newAccount2 = injector.getInstance(Account.class);
        accountManager.createNewAccount(newAccount2, u2);

        boolean status = accountManager.transferMoney(u1.toString(), u2.toString(), "10", command);
        assertTrue(status);
        assertTrue(accountManager.getAccountBalance(u2.toString()).equals(new BigDecimal("1010")));
        assertTrue(accountManager.getAccountBalance(u1.toString()).equals(new BigDecimal("990")));
    }

    /** Test the transfer money when deposit to the account fails but withdrawal succeeded.
     * The withdrawal money is rolled back.
     * @throws DuplicateAccountIdException
     * @throws InvalidAccountId
     * @throws CommandFailureException
     */
    @Test
    public void testTransferMoneyAtomicityWhenDepositFails() throws DuplicateAccountIdException, InvalidAccountId, CommandFailureException {
        // deposit fails intentionally to test the rollback
        DepositCommand dc = mock(DepositCommand.class);
        doThrow(CommandFailureException.class).when(dc).execute();

        command.setDepositCommand(dc);

        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        accountManager.createNewAccount(newAccount, u1);
        Account newAccount2 = injector.getInstance(Account.class);
        accountManager.createNewAccount(newAccount2, u2);

        //function under test
        boolean status = accountManager.transferMoney(u1.toString(), u2.toString(), "10", command);

        //check if the withdrawal rollback
        System.out.println(newAccount.getBalance());
        assertFalse(status);
        assertTrue(newAccount.getBalance().compareTo(new BigDecimal("1000")) == 0);
    }

    /**
     * Test the transfer money for thread safety.
     * It starts 500 thread and all try do the money transfer nearly same time
     * Scenario:
     * Create two account with balance as 1000
     * transfer from account 1 to account 2 one cent using one thread.
     * run the executer for 100000 times to complete the 1000$ transfer as .01 * 100000 is 1000.
     * @throws DuplicateAccountIdException
     * @throws InvalidAccountId
     */
    @Test
    public void testTransferMoneyThreadSafe() throws DuplicateAccountIdException, InvalidAccountId {
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        accountManager.createNewAccount(newAccount, u1);
        Account newAccount2 = injector.getInstance(Account.class);
        accountManager.createNewAccount(newAccount2, u2);

        ExecutorService exec = Executors.newFixedThreadPool(500);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    accountManager.transferMoney(u1.toString(), u2.toString(), "0.01", command);
                } catch (InvalidAccountId invalidAccountId) {
                    invalidAccountId.printStackTrace();
                }
            }
        };
        int i = 100000;
        while(i > 0){
            exec.execute(r);
            i--;
        }
        try {
            exec.shutdown();
            exec.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(accountManager.getAccountBalance(u1.toString()).compareTo(new BigDecimal("0")) == 0);
        assertTrue(accountManager.getAccountBalance(u2.toString()).compareTo(new BigDecimal("2000")) == 0);

    }

    @Test
    public void testTransferMoneyThreadSafeWithExceptionInDeposit() throws DuplicateAccountIdException, CommandFailureException, InvalidAccountId {
        // deposit fails intentionally to test the rollback
        DepositCommand dc = mock(DepositCommand.class);
        doThrow(CommandFailureException.class).when(dc).execute();

        command.setDepositCommand(dc);

        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        accountManager.createNewAccount(newAccount, u1);
        Account newAccount2 = injector.getInstance(Account.class);
        accountManager.createNewAccount(newAccount2, u2);

        newAccount2.depositMoney(new BigDecimal(1000));

        ExecutorService exec = Executors.newFixedThreadPool(1000);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // all threads will have their own WithdrawalCommand instance
                try {
                    accountManager.transferMoney(u1.toString(), u2.toString(), "0.01", command);
                } catch (InvalidAccountId invalidAccountId) {
                    invalidAccountId.printStackTrace();
                }
            }
        };
        int i = 100000;
        while(i > 0){
            exec.execute(r);
            i--;
        }
        try {
            exec.shutdown();
            exec.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //check if the withdrawal rollback
        assertTrue(newAccount2.getBalance().compareTo(new BigDecimal("2000")) == 0);
    }


}
