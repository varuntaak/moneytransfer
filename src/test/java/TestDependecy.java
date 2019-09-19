import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import rev.AccountsModule;
import rev.account.Account;
import rev.account.command.DepositCommand;
import rev.account.command.TransferMoneyCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.CommandFailureException;
import static org.junit.Assert.*;

import java.math.BigDecimal;

/**
 * Created by i316946 on 18/9/19.
 */
public class TestDependecy {

    @Test
    public void testInstantiation() throws CommandFailureException {
        Injector injector = Guice.createInjector(new AccountsModule());
        Account ac = injector.getInstance(Account.class);
        ac.depositMoney(new BigDecimal("10"));
        assertTrue(ac.getBalance().compareTo(new BigDecimal("1010")) == 0);

        WithdrawalCommand withdrawalCommand = injector.getInstance(WithdrawalCommand.class);
        assertNotNull(withdrawalCommand);
        DepositCommand depositCommand = injector.getInstance(DepositCommand.class);
        assertNotNull(depositCommand);
        TransferMoneyCommand c = injector.getInstance(TransferMoneyCommand.class);
        c.execute();
    }
}
