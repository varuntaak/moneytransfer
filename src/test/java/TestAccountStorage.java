import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import rev.AccountsModule;
import rev.account.Account;
import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.InvalidAccountId;
import rev.account.storage.AccountStorage;

import java.math.BigDecimal;

/**
 * Created by i316946 on 19/9/19.
 */
public class TestAccountStorage {

    Injector injector = Guice.createInjector(new AccountsModule());
    AccountStorage storage;

    @Before
    public void setup(){
        storage = injector.getInstance(AccountStorage.class);
    }

    @Test(expected = InvalidAccountId.class)
    public void testGetAccountByIdInvalid() throws InvalidAccountId {
        assertNull(storage.getAccountById("test_id"));
    }

    @Test
    public void testGetAccountByIdValid() throws DuplicateAccountIdException, InvalidAccountId {
        storage.createNewAccount(injector.getInstance(Account.class), "test_id");
        assertNotNull(storage.getAccountById("test_id"));
    }

    @Test
    public void testCreateNewAccount() throws DuplicateAccountIdException, InvalidAccountId {
        Account account = injector.getInstance(Account.class);
        storage.createNewAccount(account, "test_id");
        assertNotNull(storage.getAccountById("test_id"));
        assertTrue(storage.getAccountById("test_id").getBalance().compareTo(new BigDecimal("1000")) == 0);

    }

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalInputId() throws DuplicateAccountIdException {
        storage.createNewAccount(injector.getInstance(Account.class), null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testIllegalInputAccount() throws DuplicateAccountIdException {
        storage.createNewAccount(null, "test");
    }

    @Test(expected = DuplicateAccountIdException.class)
    public void testDuplicateId() throws DuplicateAccountIdException {
        AccountStorage storage = injector.getInstance(AccountStorage.class);
        Account account = injector.getInstance(Account.class);
        storage.createNewAccount(account, "test_id");
        storage.createNewAccount(account, "test_id");
    }
}
