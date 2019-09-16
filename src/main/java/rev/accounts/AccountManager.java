package rev.accounts;

import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.InvalidAccountId;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by i316946 on 14/9/19.
 *
 * AccountManager to manage customer accounts it has functions like
 * createAccount and getAccount.
 *
 */
public class AccountManager {

    final private static Logger logger = Logger.getLogger(AccountManager.class.getName());
    private static Map<String, Account> accountMap = new HashMap<>();

    private AccountManager(){}

    /***
     * Function to get account object.
     * @param id: String uuid.
     * @return Account: account associated with the id.
     */
    public static Account getAccount(String id) throws InvalidAccountId {
        if (id==null || !accountMap.keySet().contains(id))
            throw new InvalidAccountId("The account with id: " + id + " is not valid.");
        logger.info("Account id: " + id);
        return accountMap.get(id);
    }

    /***
     * Function to create new account with default deposit of 0.0$.
     * @return Account: returns newly created account.
     * @throws DuplicateAccountIdException
     */
    public static Account createNewAccount() throws DuplicateAccountIdException {
        return createNewAccount(UUID.randomUUID());
    }

    /**
     * Function to create new account with default deposit of 0.0$, with a given uuid value.
     * @param id: UUID to create a unique account
     * @return Account
     * @throws DuplicateAccountIdException
     */
    public static Account createNewAccount(UUID id) throws DuplicateAccountIdException {
        Account ac = new Account(id.toString(), new BigDecimal(0.0));
        if(accountMap.get(id.toString()) != null)
            throw new DuplicateAccountIdException("The account id is not unique");
        accountMap.put(id.toString(), ac);
        return ac;
    }
}
