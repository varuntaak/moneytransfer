package rev.account.command;

import rev.account.exceptions.CommandFailureException;

import java.util.List;

/**
 * Created by i316946 on 18/9/19.
 */
public class TransferMoneyCommand implements AccountCommand {
    private List<AccountCommand> commands;
    public TransferMoneyCommand(List<AccountCommand> commandList) {
        this.commands = commandList;
    }

    @Override
    public void execute() throws CommandFailureException {
        try {
            for (AccountCommand command: commands
                 ) {
                command.execute();
            }
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
}
