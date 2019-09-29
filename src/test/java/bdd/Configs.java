package bdd;

import rev.AccountsModule;

/**
 * Created by i316946 on 28/9/19.
 * BDD configuration file to keep URLs and configuration variables.
 */
class Configs {

    static final String root = "/";
    static final int port = AccountsModule.PORT;
    static final String balance_url = "/balance/";
    static final String transfer_url = "/transfermoney";
    static final String create_account_url = "/createaccount";
}
