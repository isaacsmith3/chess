package server;

import exception.ResponseException;
import model.UserData;
import types.AuthResult;

public class PreLoginClient {

    private final ServerFacade serverFacade;
    private final String serverUrl;

    public PreLoginClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        String[] tokens = input.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "quit":
                return quit();
            case "register":
                if (tokens.length < 3) {
                    return "Usage: register <username> <password> <email>";
                }
                return register(tokens[1], tokens[2], tokens[3]);
            case "login":
                if (tokens.length < 2) {
                    return "Usage: login <username> <password>";
                }
                return login(tokens[1], tokens[2]);


        }
        return null;
    }

    public String help() {
        var output = new StringBuilder();
        output.append("Help Menu:\n");
        output.append("help - print this message again :)\n");
        output.append("register - make an account\n");
        output.append("login - login to your account\n");
        output.append("quit - exit the program\n");

        return output.toString();
    }

    public String quit() {
        return "quit";
    }

    public String login(String username, String password) {
        try {
            UserData userData = new UserData(username, password, null);
            AuthResult authResult = serverFacade.login(userData);
            return "AUTH_TOKEN:" + authResult.authToken() + ":Successfully Logged In ";
        } catch (ResponseException e) {
            return "ERROR:" + e.getMessage();
        }
    }

    public String register(String username, String password, String email) {
        try {
            UserData userData = new UserData(username, password, email);
            AuthResult authResult = serverFacade.register(userData);
            return "AUTH_TOKEN:" + authResult.authToken() + ":Successfully Registered and Logged In ";
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

}
