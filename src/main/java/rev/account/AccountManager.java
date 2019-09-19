package rev.account;

import com.google.inject.Inject;
import rev.account.command.AccountCommand;
import rev.account.command.DepositCommand;
import rev.account.command.TransferMoneyCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.CommandFailureException;
import rev.account.exceptions.InvalidAccountId;
import rev.account.storage.AccountStorage;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.List;
/**
 * Created by i316946 on 14/9/19.
 *
 * AccountManager to manage customer accounts of same currency.
 * It creates account with an initial balance (i.e 1000) and allow money to be transfer between
 * all managed accounts.
 * Accounts are managed using the final accountMap: Map<String, @Account> to enforce the security.
 *
 */
public class AccountManager {

    final private static Logger logger = Logger.getLogger(AccountManager.class.getName());
    public static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000");//Final initial balance can not be change externally

    private AccountStorage storage;

    @Inject
    private AccountManager(AccountStorage storage){
        this.storage = storage;
    }

    /***
     * Function to get account object.
     * @param id: String uuid.
     * @return BigDecimal: account balance with the id.
     */
    public BigDecimal getAccountBalance(String id) throws InvalidAccountId {
        logger.info("Account id: " + id);
        if ( this.storage.getAccountById(id) != null)
            return this.storage.getAccountById(id).getBalance();
        else
            throw new InvalidAccountId("Account with id: " + id + " is not found");
    }

    /***
     * Function to create new account with default deposit of 0.0$.
     * @return Account: returns newly created account.
     * @throws DuplicateAccountIdException
     * @param newAccount
     */
//    TODO: remove account for account id.
    public Account createNewAccount(Account newAccount) throws DuplicateAccountIdException {
        return createNewAccount(newAccount, UUID.randomUUID());
    }

    /**
     * Function to create new account with default deposit of 0.0$, with a given uuid value.
     * @param id: UUID to create a unique account
     * @return Account
     * @throws DuplicateAccountIdException throws if the account exists with the id.
     */
//    TODO: remove account for account id.
    public Account createNewAccount(Account account, UUID id) throws DuplicateAccountIdException {
        return this.storage.createNewAccount(account, id.toString());
    }

    /**
     * Transfer money between same currency account.
     * @param debitAccountId Account from which the value is debited.
     * @param beneficiaryAccountId Account to which the value is credited.
     * @param value The value to be debited/credited.
     * @return boolean
     * @throws InvalidAccountId throws if the account id is invalid.
     */
    public boolean transferMoney(String debitAccountId, String beneficiaryAccountId, String value, TransferMoneyCommand command) throws InvalidAccountId {
        if (debitAccountId == null || beneficiaryAccountId == null)
            throw new InvalidAccountId("Account id for debitAccount: " + debitAccountId + "or beneficiaryAccount: " + beneficiaryAccountId + " is invalid");
        command.getDepositCommand().setAccount(this.storage.getAccountById(beneficiaryAccountId));
        command.getDepositCommand().setValue(new BigDecimal(value));
        command.getWithdrawalCommand().setAccount(this.storage.getAccountById(debitAccountId));
        command.getWithdrawalCommand().setValue(new BigDecimal(value));
        try{
            command.execute();
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}
