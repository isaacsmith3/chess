package server;

public class PreLoginClient {

    private final ServerFacade serverFacade;
    private final String serverUrl;

    public PreLoginClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        return "";
    }



}
