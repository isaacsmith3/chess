package server;

public class PostLoginClient {
    private final ServerFacade serverFacade;
    private String authToken;

    public PostLoginClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String help() {
        var output = new StringBuilder();
        output.append("Help Menu:\n");
        output.append("help - print this message again :)\n");
        output.append("logout - logout of your account :)\n");
        output.append("create <NAME> - create a new game\n");
        output.append("list - list all games\n");
        output.append("play - exit the program\n");
        output.append("observe <ID> - observe a game\n");
        output.append("quit - quit the program\n");

        return output.toString();
    }

    public String eval(String input) {
        String[] tokens = input.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "logout":
                return "lougout";
        }
        return "Invalid command";
    }



}
