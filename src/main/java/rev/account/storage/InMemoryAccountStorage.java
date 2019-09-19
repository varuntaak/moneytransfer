package rev.account.storage;

import rev.account.Account;
import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.InvalidAccountId;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i316946 on 19/9/19.
 */
public class InMemoryAccountStorage implements AccountStorage{

    private final Map<String, Account> accountMap = new HashMap<>();// Final accountMap can not be change externally and internally again.

    @Override
    public Account getAccountById(String id) throws InvalidAccountId {
        if (accountMap.containsKey(id))
            return accountMap.get(id);
        else
            throw new InvalidAccountId("Account with id :" + id + " does not exist.");
    }

    @Override
    public Account createNewAccount(Account account, String id) throws DuplicateAccountIdException {
        if (account == null || id == null)
            throw new IllegalArgumentException("Account or id is invalid");
        if (accountMap.containsKey(id))
            throw new DuplicateAccountIdException("Account with Id :" + id + " already exists. Please choose a new one.");
        account.setId(id);
        accountMap.put(id, account);
        return account;
    }
}
