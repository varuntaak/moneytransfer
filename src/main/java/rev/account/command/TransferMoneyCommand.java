package rev.account.command;

import com.google.inject.Inject;
import rev.account.exceptions.CommandFailureException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i316946 on 18/9/19.
 */
public class TransferMoneyCommand implements AccountCommand {
    private List<AccountCommand> commands = new ArrayList<>();
    private WithdrawalCommand withdrawalCommand;
    private DepositCommand depositCommand;

    public TransferMoneyCommand(List<AccountCommand> commandList) {
        this.commands = commandList;
    }

    @Inject
    public TransferMoneyCommand(WithdrawalCommand withdrawalCommand, DepositCommand depositCommand){
        this.withdrawalCommand = withdrawalCommand;
        this.depositCommand = depositCommand;
        this.commands.add(this.withdrawalCommand);
        this.commands.add(this.depositCommand);
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
        for (AccountCommand command: commands
                ) {
            command.rollback();
        }
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
