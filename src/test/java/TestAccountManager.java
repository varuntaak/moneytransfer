/**
 * Created by i316946 on 14/9/19.
 */
import org.junit.*;
import static org.junit.Assert.*;

import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.InvalidAccountId;
import rev.accounts.Account;
import rev.accounts.AccountManager;

import java.math.BigDecimal;
import java.util.UUID;


public class TestAccountManager{

    @Test
    public void testCreateAccount() throws DuplicateAccountIdException {
        Account newAccount = AccountManager.createNewAccount();
        assertTrue(newAccount.getBalance().equals(new BigDecimal("0")));
        UUID uuid = UUID.fromString(newAccount.getId());
        assertTrue(uuid.toString().equals(newAccount.getId()));
        assertFalse(uuid.toString() == newAccount.getId());
    }

    @Test(expected = DuplicateAccountIdException.class)
    public void testCreateAccountWithUniqueness() throws DuplicateAccountIdException {
        UUID id = UUID.randomUUID();
        Account newAccount = AccountManager.createNewAccount(id);
        assertTrue(newAccount.getBalance().equals(new BigDecimal("0")));
        UUID uuid = UUID.fromString(newAccount.getId());
        assertTrue(uuid.toString().equals(newAccount.getId()));
        AccountManager.createNewAccount(id);
    }

    @Test
    public void testGetAccountWithValidId() throws DuplicateAccountIdException, InvalidAccountId {
        Account newAccount = AccountManager.createNewAccount();
        Account account1 = AccountManager.getAccount(newAccount.getId());
        assertTrue(account1 == newAccount);
        assertNotNull(AccountManager.getAccount(newAccount.getId()));
        assertEquals(newAccount, AccountManager.getAccount(newAccount.getId()));
    }

    @Test(expected = InvalidAccountId.class)
    public void testInvalidAccountIdAsNotUUID() throws InvalidAccountId {
        AccountManager.getAccount("423423iurewr");
    }
    @Test(expected = InvalidAccountId.class)
    public void testInvalidAccountIdAsNull() throws InvalidAccountId {
        AccountManager.getAccount(null);
    }


}
