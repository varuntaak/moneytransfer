package rev.accounts;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by i316946 on 14/9/19.
 *
 * AccountManager to manage customer accounts it has functions like createAccount and getAccount.
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
    public static Account getAccount(String id) {
        if (id == null)
            return null;
        logger.info("Account id: " + id);
        return accountMap.get(id);
    }

    /***
     * Function to create new account with default deposit of 0.0$.
     * @return Account: returns newly created account.
     */
    public static Account createNewAccount() {
        UUID id = UUID.randomUUID();
        Account ac = new Account(id.toString(), new BigDecimal(0.0));
        accountMap.put(id.toString(), ac);
        return ac;
    }

    public boolean isValidAccount(long id) {

        return false;
    }

    public static void depositMoney(String id, double money) {

    }
}
