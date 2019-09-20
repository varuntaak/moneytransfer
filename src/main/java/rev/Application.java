package rev;

/**
 * Created by i316946 on 15/9/19.
 */
import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.google.inject.Guice;
import com.google.inject.Injector;
import rev.account.Account;
import rev.account.command.TransferMoneyCommand;
import rev.account.exceptions.DuplicateAccountIdException;
import rev.account.exceptions.InvalidAccountId;
import rev.account.AccountManager;
import rev.models.AccountModel;
import rev.models.TransferMoney;

import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.PathMatchers.remaining;

public class Application extends AllDirectives {

    Injector injector = Guice.createInjector(new AccountsModule());

    public static void main(String[] args) throws Exception {
        // boot up server using the route as defined below
        ActorSystem system = ActorSystem.create("routes");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        //In order to access all directives we need an instance where the routes are define.
        Application app = new Application();

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("localhost", AccountsModule.PORT), materializer);

        System.out.println("Server online at http://localhost:8081/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }

    public Route createRoute() {
        AccountManager accountManager = injector.getInstance(AccountManager.class);
        return concat(
                pathPrefix( "balance", () ->
                path(remaining(), (String id) ->
                        get(() -> {
                            try {
                                return complete(StatusCodes.OK, accountManager.getAccountBalance(id), Jackson.marshaller());
                            } catch (InvalidAccountId invalidAccountId) {
                                invalidAccountId.printStackTrace();
                                return complete(StatusCodes.BAD_REQUEST, invalidAccountId.getMessage());
                            }
                        }))),
                path("", () -> get( () -> complete(StatusCodes.OK, "Server is up and running!"))),
                post( () ->
                    path("trasfermoney", () ->
                        entity(Jackson.unmarshaller(TransferMoney.class),  transferMoneyModel -> {
                            TransferMoneyCommand command = injector.getInstance(TransferMoneyCommand.class);
                            boolean status = false;
                            try {
                                status = accountManager.transferMoney(transferMoneyModel, command);
                                return complete(StatusCodes.OK, "" +status);
                            } catch (InvalidAccountId invalidAccountId) {
                                invalidAccountId.printStackTrace();
                                return complete(StatusCodes.BAD_REQUEST, invalidAccountId.getMessage());
                            }
                        }))),
                post( () ->
                        path("createaccount", () ->
                                entity(Jackson.unmarshaller(AccountModel.class), accountModel -> {
                                    try {
                                        Account account = injector.getInstance(Account.class);
                                        account.setName(accountModel.getName());
                                        Account newAccount = accountManager.createNewAccount(account);
                                        return complete(StatusCodes.OK, newAccount.getId());
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                        return complete(StatusCodes.BAD_REQUEST, e.getMessage());
                                    }
                                }))));
    }
}