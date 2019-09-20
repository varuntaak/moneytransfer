package rev.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;

/**
 * Created by i316946 on 19/9/19.
 */
public class AccountModel {
    String name;

    @Inject
    @JsonCreator
    public AccountModel(@JsonProperty("name") String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
