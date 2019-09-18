package rev.accounts;

import rev.account.command.AccountCommand;
import rev.account.command.DepositCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.IllegalOperationException;
import rev.account.exceptions.InvalidAccountId;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by i316946 on 14/9/19.
 *
 * AccountManager to manage customer accounts
 * It creates account with an initial balance (i.e 1000) and allow money to be transfer between
 * all managed accounts.
 * Accounts are managed using the final accountMap: Map<String, @Account> to enforce the security.
 *
 */
public class AccountManager {

    final private static Logger logger = Logger.getLogger(AccountManager.class.getName());
    public static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000");//Final initial balance can not be change externally
    private static final Map<String, Account> accountMap = new HashMap<>();// Final accountMap can not be change externally and internally again.

    private AccountManager(){}

    /***
     * Function to get account object.
     * @param id: String uuid.
     * @return BigDecimal: account balance with the id.
     */
    public static BigDecimal getAccountBalance(String id) throws InvalidAccountId {
        if (id==null || !accountMap.keySet().contains(id))
            throw new InvalidAccountId("The account with id: " + id + " is not valid.");
        logger.info("Account id: " + id);
        return accountMap.get(id).getBalance();
    }

    /***
     * Function to create new account with default deposit of 0.0$.
     * @return Account: returns newly created account.
     * @throws DuplicateAccountIdException
     */
//    TODO: remove account for account id.
    public static Account createNewAccount() throws DuplicateAccountIdException {
        return createNewAccount(UUID.randomUUID());
    }

    /**
     * Function to create new account with default deposit of 0.0$, with a given uuid value.
     * @param id: UUID to create a unique account
     * @return Account
     * @throws DuplicateAccountIdException
     */
//    TODO: remove account for account id.
    public static Account createNewAccount(UUID id) throws DuplicateAccountIdException {
        Account ac = new Account(id.toString(), INITIAL_BALANCE);
        if(accountMap.get(id.toString()) != null)
            throw new DuplicateAccountIdException("The account id is not unique");
        accountMap.put(id.toString(), ac);
        return ac;
    }

    public static boolean transferMoney(String debitAccountId, String beneficiaryAccountId, String value) throws InvalidAccountId {
        if (debitAccountId == null || beneficiaryAccountId == null)
            throw new InvalidAccountId("Account id for debitAccount: " + debitAccountId + "or beneficiaryAccount: " + beneficiaryAccountId + " is invalid");
        if (!accountMap.keySet().contains(debitAccountId) || !accountMap.keySet().contains(beneficiaryAccountId))
            throw new InvalidAccountId("Account id for debitAccount: " + debitAccountId + "or beneficiaryAccount: " + beneficiaryAccountId + " is invalid");
        AccountCommand wac = new WithdrawalCommand(new BigDecimal(value), accountMap.get(debitAccountId));
        AccountCommand ac = new DepositCommand(new BigDecimal(value), accountMap.get(beneficiaryAccountId));
        try{
            executeTransferMoneyCommands(wac, ac);
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public static void executeTransferMoneyCommands(AccountCommand withdrawal, AccountCommand deposit) {
        try{
            withdrawal.execute();
            deposit.execute();
        } catch (IllegalOperationException e){
            e.printStackTrace();
            withdrawal.rollback();
            deposit.rollback();
        }
    }
}
