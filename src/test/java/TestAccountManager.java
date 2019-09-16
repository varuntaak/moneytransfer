/**
 * Created by i316946 on 14/9/19.
 */
import org.junit.*;
import static org.junit.Assert.*;

import rev.accounts.Account;
import rev.accounts.AccountManager;

import java.math.BigDecimal;
import java.util.UUID;


public class TestAccountManager{

    @Test
    public void testCreateAccount(){
        Account newAccount = AccountManager.createNewAccount();
        assertTrue(new BigDecimal(0.0) == newAccount.getBalance());
        UUID uuid = UUID.fromString(newAccount.getId());
        assertTrue(uuid.toString().equals(newAccount.getId()));
        assertFalse(uuid.toString() == newAccount.getId());
    }

    @Test
    public void testGetAccount(){
        assertNull(AccountManager.getAccount("423423iurewr"));
        assertNull(AccountManager.getAccount(null));
        Account newAccount = AccountManager.createNewAccount();
        Account account1 = AccountManager.getAccount(newAccount.getId());
        assertTrue(account1 == newAccount);
        assertNotNull(AccountManager.getAccount(newAccount.getId()));
        assertEquals(newAccount, AccountManager.getAccount(newAccount.getId()));
    }


}
