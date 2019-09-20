import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import rev.AccountsModule;
import rev.account.Account;
import rev.account.command.DepositCommand;
import rev.account.command.TransferMoneyCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.exceptions.CommandFailureException;
import rev.models.AccountModel;
import rev.models.DepositModel;
import rev.models.TransferMoney;

import static org.junit.Assert.*;

import java.math.BigDecimal;

/**
 * Created by i316946 on 18/9/19.
 */
public class TestDependecy {

    @Test
    public void testInjections() throws CommandFailureException {
        Injector injector = Guice.createInjector(new AccountsModule());
        Account ac = injector.getInstance(Account.class);
        assertNotNull(ac);
        ac.depositMoney(new BigDecimal("10"));
        assertTrue(ac.getBalance().compareTo(new BigDecimal("1010")) == 0);
        assertTrue(ac.getId().equals(""));
        assertTrue(ac.getName().equals(""));
        ac.setName("name");
        assertTrue(ac.getName().equals("name"));

        WithdrawalCommand withdrawalCommand = injector.getInstance(WithdrawalCommand.class);
        assertNotNull(withdrawalCommand);
        DepositCommand depositCommand = injector.getInstance(DepositCommand.class);
        assertNotNull(depositCommand);
        TransferMoneyCommand c = injector.getInstance(TransferMoneyCommand.class);
        c.execute();
        AccountModel am = injector.getInstance(AccountModel.class);
        assertNotNull(am);
        assertTrue(am.getName().equals(""));
        TransferMoney tm = injector.getInstance(TransferMoney.class);
        assertNotNull(tm);
        assertTrue(tm.getFrom().equals(""));
        assertTrue(tm.getTo().equals(""));
        assertTrue(tm.getValue().equals(""));
        DepositModel dc = injector.getInstance(DepositModel.class);
        assertNotNull(dc);
        assertTrue(dc.getAccount_id().equals(""));
        assertTrue(dc.getAmount().equals(""));
    }
}
