package server;

public class GameClient {
    private final ServerFacade serverFacade;
    private final String playerColor;

    public GameClient(String serverUrl, String playerColor) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.playerColor = playerColor;
    }

    public String eval(String input) {
        String[] tokens = input.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "draw":
                return draw(this.playerColor);
        }
        return "Invalid command";
    }

    public String help() {
        var output = new StringBuilder();
        output.append("Help Menu:\n");
        output.append("help - print this message again :)\n");
        output.append("draw - redraw the board\n");
        output.append("quit - quit the program\n");

        return output.toString();
    }

    public String draw(String playerColor) {
        return "Viewing board:\n" + playerColor + "\n";
    }

}
