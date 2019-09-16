package rev.account.exceptions;

/**
 * Created by i316946 on 16/9/19.
 */
public class InsuffificentBalance extends RuntimeException{

    /***
     * InsufficientBalance exception throws when the balance is not sufficient to do a withdrawal.
     * @param message
     */
    public InsuffificentBalance(String message){
        super(message);
    }
}
