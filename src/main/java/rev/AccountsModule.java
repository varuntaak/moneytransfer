package rev;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import rev.account.Account;
import rev.account.command.AccountCommand;
import rev.account.command.DepositCommand;
import rev.account.command.TransferMoneyCommand;
import rev.account.command.WithdrawalCommand;
import rev.account.storage.AccountStorage;
import rev.account.storage.InMemoryAccountStorage;

import javax.lang.model.element.Name;
import java.math.BigDecimal;

/**
 * Created by i316946 on 18/9/19.
 */
public class AccountsModule extends AbstractModule {

    public static final String INITIAL_BALANCE = "1000";

    @Override
    protected void configure(){
        bind(BigDecimal.class).toInstance(new BigDecimal("0"));
        bind(AccountCommand.class).to(TransferMoneyCommand.class);
        bind(String.class)
                .annotatedWith(Names.named("DEFAULT_VALUE"))
                .toInstance("0");
        bind(String.class)
                .annotatedWith(Names.named("DEFAULT_ACCOUNT_ID"))
                .toInstance("0");
        bind(String.class)
                .annotatedWith(Names.named("INITIAL_BALANCE"))
                .toInstance(INITIAL_BALANCE);
        bind(AccountStorage.class).to(InMemoryAccountStorage.class);
        bind(Account.class);
        bind(BigDecimal.class)
                .annotatedWith(Names.named("INITIAL_BALANCE"))
                .toInstance(new BigDecimal(INITIAL_BALANCE));

    }

    @Provides
    WithdrawalCommand provideWithdrawalCommand(@Named("DEFAULT_VALUE") String value, Account account){
        WithdrawalCommand wc = new WithdrawalCommand(new BigDecimal(value), account);
        return wc;
    }

    @Provides
    DepositCommand provideDepositCommand(@Named("DEFAULT_VALUE") String value, Account account){
        DepositCommand dc = new DepositCommand(new BigDecimal(value), account);
        return dc;
    }
}
