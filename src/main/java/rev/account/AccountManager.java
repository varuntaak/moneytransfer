package rev.account;

import com.google.inject.Inject;
import rev.account.command.TransferMoneyCommand;
import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.InvalidAccountId;
import rev.account.generators.IdGenerator;
import rev.account.storage.AccountStorage;
import rev.models.TransferMoney;

import java.math.BigDecimal;
import java.util.logging.Logger;
/**
 * Created by i316946 on 14/9/19.
 *
 * AccountManager to manage customer accounts of same currency.
 * It creates account with an initial balance (i.e 1000) and allow money to be transfer between
 * all managed accounts.
 * Accounts are managed using AccountStorage.
 *
 */
public class AccountManager {

    final private static Logger logger = Logger.getLogger(AccountManager.class.getName());
    private AccountStorage storage;
    private IdGenerator idGenerator;

    @Inject
    private AccountManager(AccountStorage storage, IdGenerator idGenerator){
        this.storage = storage;
        this.idGenerator = idGenerator;
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
        return this.storage.createNewAccount(newAccount, this.idGenerator.generateId());
    }

    /**
     * Transfer money between same currency account.
     * @param model: TransferModel unmarshalled from the request.
     * @param command: TransferMoneyCommand
     * @return boolean
     * @throws InvalidAccountId throws if the account id is invalid.
     */
    public boolean transferMoney(TransferMoney model, TransferMoneyCommand command) throws InvalidAccountId {
        if (model.getFrom() == null || model.getTo() == null)
            throw new InvalidAccountId("Account id for debitAccount: " + model.getFrom() + "or beneficiaryAccount: " + model.getTo() + " is invalid");
        command.getDepositCommand().setAccount(this.storage.getAccountById(model.getTo()));
        command.getDepositCommand().setValue(new BigDecimal(model.getValue()));
        command.getWithdrawalCommand().setAccount(this.storage.getAccountById(model.getFrom()));
        command.getWithdrawalCommand().setValue(new BigDecimal(model.getValue()));
        try{
            command.execute();
            return true;
        } catch (Exception ex){
            return false;
        }
    }

    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }
}
