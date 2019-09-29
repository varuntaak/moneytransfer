package rev.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

import java.math.BigDecimal;

/**
 * Created by i316946 on 19/9/19.
 */
public class TransferMoney {
    String from;
    String to;
    String value;

    @Inject
    @JsonCreator
    public TransferMoney(@JsonProperty("from") String from,
                         @JsonProperty("to") String to,
                         @JsonProperty("value") String value){
        this.from = from;
        this.to = to;
        this.value = value;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
