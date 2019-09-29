import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import rev.AccountsModule;
import rev.account.exceptions.IllegalValueException;
import rev.account.exceptions.InsuffificentBalance;
import rev.account.Account;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Created by i316946 on 16/9/19.
 */
public class TestAccount {
    Injector injector = Guice.createInjector(new AccountsModule());
    Account account;

    @Before
    public void setup(){
        account = injector.getInstance(Account.class);
    }

    @Test
    public void testDepoistMoney(){
        BigDecimal zero = new BigDecimal(0.00);
        account.depositMoney(zero);
        assertTrue(account.getBalance().compareTo(new BigDecimal(AccountsModule.INITIAL_BALANCE)) == 0);
        BigDecimal v = new BigDecimal(10.0);
        account.depositMoney(v);
        assertTrue(account.getBalance().compareTo(new BigDecimal("1010")) == 0);

        //check deposit of extra large value
        account.depositMoney(new BigDecimal("10000000000000000000000000000000000000000"));
        assertTrue(account.getBalance().equals(new BigDecimal("10000000000000000000000000000000000001010")));

    }

    @Test(expected = IllegalValueException.class)
    public void testDepositOfNullValue(){
        account.depositMoney(null);
    }

    @Test(expected = IllegalValueException.class)
    public void testDepositOfDecimalLimit(){
        account.depositMoney(new BigDecimal("1.3423423"));
    }

    @Test(expected = IllegalValueException.class)
    public void testDepositOfNegativeValue(){
        account.depositMoney(new BigDecimal("-1"));
    }

    @Test
    public void testIfDepositThreadSafe(){
        account.withdrawMoney(new BigDecimal(AccountsModule.INITIAL_BALANCE));
        ExecutorService exec = Executors.newFixedThreadPool(1000);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                account.depositMoney(BigDecimal.ONE);
            }
        };
        int i = 100000;
        while(i > 0){
            exec.execute(r);
            i--;
        }
        try {
            exec.shutdown();
            exec.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(account.getBalance().compareTo(new BigDecimal(100000)) == 0);
    }

    @Test(expected = IllegalValueException.class)
    public void testWithdrawalOfNullValue(){
        account.withdrawMoney(null);
    }

    @Test(expected = IllegalValueException.class)
    public void testWithdrawalOfDecimalLimit(){
        account.withdrawMoney(new BigDecimal(23423432.3423423));
    }

    @Test(expected = IllegalValueException.class)
    public void testWithdrawalOfIllegalValue3(){
        account.withdrawMoney(new BigDecimal(-23423432.3423423));
    }

    @Test(expected = InsuffificentBalance.class)
    public void testWithdrawMoneyInsufficientBalance(){
        account.withdrawMoney(new BigDecimal("1000"));
        account.withdrawMoney(BigDecimal.ONE);
    }

    @Test
    public void testWithdrawMoney(){
        account.depositMoney(BigDecimal.ONE);
        account.withdrawMoney(BigDecimal.ONE);
        assertTrue(account.getBalance().compareTo(new BigDecimal(AccountsModule.INITIAL_BALANCE)) == 0);

        //check for decimal value with scale at 2
        account = injector.getInstance(Account.class);
        account.depositMoney(new BigDecimal("10.23"));
        account.withdrawMoney(new BigDecimal("9.21"));
        assertTrue(account.getBalance().equals(new BigDecimal("1001.02")));

        //test withdrawal of extra large vlaue
        account = injector.getInstance(Account.class);
        account.depositMoney(new BigDecimal("10000000000000000000000000000000000000010.10"));
        account.withdrawMoney(new BigDecimal("10000000000000000000000000000000000000000.09"));
        assertTrue(account.getBalance().equals(new BigDecimal("1010.01")));
    }

    @Test
    public void testIfWithdrawalThreadSafe(){
        BigDecimal v = new BigDecimal("100000");
        account.withdrawMoney(new BigDecimal("1000"));
        account.depositMoney(v);
        ExecutorService exec = Executors.newFixedThreadPool(1000);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                account.withdrawMoney(BigDecimal.ONE);
            }
        };
        int i = 100000;
        while(i > 0){
            exec.execute(r);
            i--;
        }
        try {
            exec.shutdown();
            exec.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(account.getBalance().compareTo(new BigDecimal("0")) == 0);
    }
}
