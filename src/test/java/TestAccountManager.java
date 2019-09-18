/**
 * Created by i316946 on 14/9/19.
 */
import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import rev.account.command.DepositCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.IllegalOperationException;
import rev.account.exceptions.InvalidAccountId;
import rev.accounts.Account;
import rev.accounts.AccountManager;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestAccountManager{

    /**
     * Test create new account with INITIAL_BALANCE.
     * @throws DuplicateAccountIdException
     */
    @Test
    public void testCreateAccount() throws DuplicateAccountIdException {
        // create a new account check initial balance
        Account newAccount = AccountManager.createNewAccount();
        assertTrue(newAccount.getBalance().equals(AccountManager.INITIAL_BALANCE));

        // test if the account has a valid uuid
        UUID uuid = UUID.fromString(newAccount.getId());
        assertTrue(uuid.toString().equals(newAccount.getId()));
        assertFalse(uuid.toString() == newAccount.getId());
    }

    @Test(expected = DuplicateAccountIdException.class)
    public void testCreateAccountWithUniqueness() throws DuplicateAccountIdException {
        //set up to have an account with the id
        UUID id = UUID.randomUUID();
        Account newAccount = AccountManager.createNewAccount(id);
        assertTrue(newAccount.getBalance().equals(AccountManager.INITIAL_BALANCE));
        UUID uuid = UUID.fromString(newAccount.getId());
        assertTrue(uuid.toString().equals(newAccount.getId()));

        //create an account with existing id throws a DuplicateAccountIdException
        AccountManager.createNewAccount(id);
    }

    /** Test the getAccountBalance for various edge cases **/
    @Test
    public void testGetAccountBalanceWithValidId() throws DuplicateAccountIdException, InvalidAccountId {
        // create a new account
        Account newAccount = AccountManager.createNewAccount();

        // get the balance of the new account and match to INITIAL_BALANCE
        BigDecimal balance = AccountManager.getAccountBalance(newAccount.getId());
        assertNotNull(balance);
        assertTrue(balance.equals(AccountManager.INITIAL_BALANCE));
        assertTrue(newAccount.getBalance().equals(AccountManager.getAccountBalance(newAccount.getId())));
    }

    /** test get balance with invalid account id throws exception **/
    @Test(expected = InvalidAccountId.class)
    public void testInvalidAccountIdAsNotUUID() throws InvalidAccountId {
        AccountManager.getAccountBalance("423423iurewr");
    }

    /** test get balance with invalid account id throws exception **/
    @Test(expected = InvalidAccountId.class)
    public void testInvalidAccountIdAsNull() throws InvalidAccountId {
        AccountManager.getAccountBalance(null);
    }

    /** Test the money transfer for null values **/
    @Test(expected = InvalidAccountId.class)
    public void testTransferMoneyInvalidInput1() throws InvalidAccountId {
        AccountManager.transferMoney(null, null, "12");
    }

    /** Test the money transfer for invalid account id for debit account. **/
    @Test(expected = InvalidAccountId.class)
    public void testTransferMoneyInvalidInput2() throws InvalidAccountId, DuplicateAccountIdException {
        UUID uuid = UUID.randomUUID();
        AccountManager.createNewAccount(uuid);
        AccountManager.transferMoney("dfsdf", uuid.toString(), "12");
    }

    /** Test the transfer money for invalid account id of the beneficiary account **/
    @Test(expected = InvalidAccountId.class)
    public void testTransferMoneyInvalidInput3() throws InvalidAccountId, DuplicateAccountIdException {
        UUID uuid = UUID.randomUUID();
        AccountManager.createNewAccount(uuid);
        AccountManager.transferMoney( uuid.toString(), "ewrewr", "12");
    }

    /** Test the transfer money when both accounts are have the invalid account id **/
    @Test(expected = InvalidAccountId.class)
    public void testTransferMoneyInvalidInput4() throws InvalidAccountId {
        AccountManager.transferMoney("dfsdf", "erwer", "12");
    }

    /** Test the transfer money for a valid case **/
    @Test
    public void testTransferMoney() throws DuplicateAccountIdException, InvalidAccountId {
        UUID u1 = UUID.randomUUID();
        UUID u2 = UUID.randomUUID();

        AccountManager.createNewAccount(u1);
        AccountManager.createNewAccount(u2);

        boolean status = AccountManager.transferMoney(u1.toString(), u2.toString(), "10");
        assertTrue(status);
        assertTrue(AccountManager.getAccountBalance(u2.toString()).equals(new BigDecimal("1010")));
        assertTrue(AccountManager.getAccountBalance(u1.toString()).equals(new BigDecimal("990")));
    }

    /** Test the transfer money when deposit to the account fails but withdrawal succeded.
     * The withdrawal money is rolledback.
     * @throws DuplicateAccountIdException
     * @throws InvalidAccountId
     * @throws IllegalOperationException
     */
    @Test
    public void testTransferMoneyAtomicityWhenDepositFails() throws DuplicateAccountIdException, InvalidAccountId, IllegalOperationException {
        UUID u1 = UUID.randomUUID();
        AccountManager.createNewAccount(u1);
        // deposit fails intentionally to test the rollback
        DepositCommand dc = mock(DepositCommand.class);
        doThrow(IllegalOperationException.class).when(dc).execute();


        UUID u2 = UUID.randomUUID();
        Account account = Account.getInstance(u2.toString());
        account.depositMoney(new BigDecimal(100));
        WithdrawalCommand wc = new WithdrawalCommand(new BigDecimal("23"), account);

        //function under test
        AccountManager.executeTransferMoneyCommands(wc, dc);

        //check if the deposit happens
        assertTrue(AccountManager.getAccountBalance(u1.toString()).compareTo(AccountManager.INITIAL_BALANCE) == 0);
        //check if the withdrawal rollback
        assertTrue(account.getBalance().compareTo(new BigDecimal("100")) == 0);

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

        AccountManager.createNewAccount(u1);
        AccountManager.createNewAccount(u2);

        ExecutorService exec = Executors.newFixedThreadPool(500);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    AccountManager.transferMoney(u1.toString(), u2.toString(), "0.01");
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
        assertTrue(AccountManager.getAccountBalance(u1.toString()).compareTo(new BigDecimal("0")) == 0);
        assertTrue(AccountManager.getAccountBalance(u2.toString()).compareTo(new BigDecimal("2000")) == 0);

    }


}
