package rev.account.storage;

import rev.account.Account;
import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.InvalidAccountId;

/**
 * Created by i316946 on 19/9/19.
 */
public interface AccountStorage {

    public Account getAccountById(String id) throws InvalidAccountId;
    public Account createNewAccount(Account account, String id) throws DuplicateAccountIdException;
}
