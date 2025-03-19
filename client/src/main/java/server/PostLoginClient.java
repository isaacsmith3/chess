package server;

import exception.ResponseException;
import types.ListGamesResult;

import java.util.Collection;

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
        output.append("logout - logout of your account\n");
        output.append("list - list all games\n");
        output.append("create <NAME> - create a new game\n");
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
                return logout(authToken);
            case "list":
                return list();
        }
        return "Invalid command";
    }

    public String logout(String authToken) {
        try {
            serverFacade.logout(authToken);
            return "Logged Out Successfully";
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String list() {
        try {
            Collection<ListGamesResult> games = serverFacade.listGames(authToken);

            StringBuilder output = new StringBuilder();
            output.append("                           GAME LIST                          \n");
            output.append("--------------------------------------------------------------\n");
            output.append("| NUM  | GAME NAME             | WHITE PLAYER | BLACK PLAYER |\n");
            output.append("--------------------------------------------------------------\n");

            int number = 0;
            for (ListGamesResult g : games) {
                String whitePlayer = (g.whiteUsername() != null) ? g.whiteUsername() : "-";
                String blackPlayer = (g.blackUsername() != null) ? g.blackUsername() : "-";

                output.append(String.format("| %-4d | %-21s | %-12s | %-11s  |\n",
                        number,
                        g.gameName(),
                        whitePlayer,
                        blackPlayer));
                number++;
            }
            output.append("--------------------------------------------------------------\n");

            return output.toString();
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }





}
