package rev.account.command;

import com.google.inject.Inject;
import rev.account.exceptions.CommandFailureException;

/**
 * Created by i316946 on 18/9/19.
 */
public class TransferMoneyCommand implements AccountCommand {
    private WithdrawalCommand withdrawalCommand;
    private DepositCommand depositCommand;

    @Inject
    public TransferMoneyCommand(WithdrawalCommand withdrawalCommand, DepositCommand depositCommand){
        this.withdrawalCommand = withdrawalCommand;
        this.depositCommand = depositCommand;
    }

    @Override
    public void execute() throws CommandFailureException {
        try {
            withdrawalCommand.execute();
            depositCommand.execute();
        } catch (Exception ex){
            ex.printStackTrace();
            this.rollback();
            throw new CommandFailureException(ex.getMessage());
        }
    }

//    TODO: to throw unrecoverable exception
    @Override
    public void rollback() {
        withdrawalCommand.rollback();
        depositCommand.rollback();
    }

    public WithdrawalCommand getWithdrawalCommand() {
        return withdrawalCommand;
    }

    public void setWithdrawalCommand(WithdrawalCommand withdrawalCommand) {
        this.withdrawalCommand = withdrawalCommand;
    }

    public DepositCommand getDepositCommand() {
        return depositCommand;
    }

    public void setDepositCommand(DepositCommand depositCommand) {
        this.depositCommand = depositCommand;
    }
}
