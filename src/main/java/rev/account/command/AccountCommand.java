package rev.account.command;

import rev.account.exceptions.CommandFailureException;

/**
 * Created by i316946 on 17/9/19.
 */
public interface AccountCommand {
    public void execute() throws CommandFailureException;
    public void rollback();
}
