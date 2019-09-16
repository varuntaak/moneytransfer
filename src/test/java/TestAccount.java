import akka.dispatch.ExecutorServiceFactory;
import org.junit.Test;
import static org.junit.Assert.*;

import rev.account.exceptions.IllegalValueException;
import rev.account.exceptions.InsuffificentBalance;
import rev.accounts.Account;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.*;

/**
 * Created by i316946 on 16/9/19.
 */
public class TestAccount {


    @Test
    public void testDepoistMoney(){
        Account ac = Account.getInstance("23werwer");
        BigDecimal zero = new BigDecimal(0.00);
        assertTrue(ac.getBalance().compareTo(zero) == 0);
        BigDecimal v = new BigDecimal(10.0);
        ac.depositMoney(v);
        assertTrue(ac.getBalance().compareTo(v) == 0);

        //check deposit of extra large value
        ac.depositMoney(new BigDecimal("10000000000000000000000000000000000000000"));
        assertTrue(ac.getBalance().equals(new BigDecimal("10000000000000000000000000000000000000010")));

    }

    @Test(expected = IllegalValueException.class)
    public void testDepositOfIllegalValue(){
        Account ac = Account.getInstance("24234");
        ac.depositMoney(null);
    }

    @Test(expected = IllegalValueException.class)
    public void testDepositOfIllegalValue2(){
        Account ac = Account.getInstance("24234");
        ac.depositMoney(new BigDecimal("1.3423423"));
    }

    @Test(expected = IllegalValueException.class)
    public void testDepositOfIllegalValue3(){
        Account ac = Account.getInstance("24234");
        ac.depositMoney(new BigDecimal("-1"));
    }


    @Test
    public void testMaxValue(){
        BigDecimal c = new BigDecimal("2323123.43");
        System.out.println(c.scale());
        System.out.println(c);
    }

    @Test
    public void testIfDepositThreadSafe(){
        Account ac = Account.getInstance("23werwer");
        ExecutorService exec = Executors.newFixedThreadPool(1000);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ac.depositMoney(BigDecimal.ONE);
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
        assertTrue(ac.getBalance().compareTo(new BigDecimal(100000)) == 0);
    }

    @Test(expected = IllegalValueException.class)
    public void testWithdrawalOfIllegalValue(){
        Account ac = Account.getInstance("24234");
        ac.withdrawMoney(null);
    }

    @Test(expected = IllegalValueException.class)
    public void testWithdrawalOfIllegalValue2(){
        Account ac = Account.getInstance("24234");
        ac.withdrawMoney(new BigDecimal(23423432.3423423));
    }

    @Test(expected = IllegalValueException.class)
    public void testWithdrawalOfIllegalValue3(){
        Account ac = Account.getInstance("24234");
        ac.withdrawMoney(new BigDecimal(-23423432.3423423));
    }

    @Test(expected = InsuffificentBalance.class)
    public void testWithdrawMoneyInsufficientBalance(){
        Account ac = Account.getInstance("34234");
        ac.withdrawMoney(BigDecimal.ONE);
    }

    @Test
    public void testWithdrawMoney(){
        Account ac = Account.getInstance("234234");
        ac.depositMoney(BigDecimal.ONE);
        ac.withdrawMoney(BigDecimal.ONE);
        assertTrue(ac.getBalance().compareTo(new BigDecimal(0)) == 0);

        //check for decimal value with scale at 2
        ac.depositMoney(new BigDecimal("10.23"));
        ac.withdrawMoney(new BigDecimal("9.21"));
        assertTrue(ac.getBalance().equals(new BigDecimal("1.02")));

        //test withdrawal of extra large vlaue
        ac = Account.getInstance("40968450");
        ac.depositMoney(new BigDecimal("10000000000000000000000000000000000000010.10"));
        ac.withdrawMoney(new BigDecimal("10000000000000000000000000000000000000000.09"));
        assertTrue(ac.getBalance().equals(new BigDecimal("10.01")));
    }

    @Test
    public void testIfWithdrawalThreadSafe(){
        Account ac = Account.getInstance("23werwer");
        BigDecimal v = new BigDecimal("100000");
        System.out.println(v.scale());
        ac.depositMoney(v);
        ExecutorService exec = Executors.newFixedThreadPool(1000);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ac.withdrawMoney(BigDecimal.ONE);
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
        assertTrue(ac.getBalance().compareTo(new BigDecimal("0")) == 0);
    }
}
