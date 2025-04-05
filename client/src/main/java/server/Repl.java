package server;

import model.AuthData;

import java.io.IOException;
import java.util.Scanner;

enum State { PRE_LOGIN, POST_LOGIN, GAME }

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private GameClient gameClient;
    private String authToken = null;
    private String serverUrl;
    private String username;
    private String playerColor = null;

    public Repl(String serverUrl) {
        this.preLoginClient = new PreLoginClient(serverUrl);
        this.postLoginClient = new PostLoginClient(serverUrl);
        this.gameClient = null;
        this.serverUrl = serverUrl;
    }

    public void run() {
        initializeSystem();
        Scanner scanner = new Scanner(System.in);
        String input = "";
        State currentState = State.PRE_LOGIN;

        while (true) {
            printPrompt();
            input = scanner.nextLine();

            if (isQuitCommand(input)) {
                performLogoutIfNeeded();
                break;
            }

            try {
                currentState = processInputBasedOnState(input, currentState);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void initializeSystem() {
        System.out.println("Welcome to Chess. Sign in to start");
        System.out.println(preLoginClient.help());
    }

    private boolean isQuitCommand(String input) {
        return input.equalsIgnoreCase("quit");
    }

    private void performLogoutIfNeeded() {
        if (authToken != null) {
            postLoginClient.eval("logout");
        }
    }

    private State processInputBasedOnState(String input, State currentState) throws Exception {
        switch (currentState) {
            case PRE_LOGIN:
                return handlePreLoginState(input);
            case POST_LOGIN:
                return handlePostLoginState(input);
            case GAME:
                return handleGameState(input);
            default:
                return currentState;
        }
    }

    private State handlePreLoginState(String input) {
        String result = preLoginClient.eval(input);
        String[] parsedResult = parseAuthMessage(result);

        if (authToken != null) {
            postLoginClient.setAuthToken(authToken);
            System.out.println(parsedResult[0]);
            System.out.println("\n");
            System.out.println(postLoginClient.help());
            return State.POST_LOGIN;
        }

        System.out.println(result);
        return State.PRE_LOGIN;
    }

    private State handlePostLoginState(String input) throws Exception {
        String result = postLoginClient.eval(input);

        if (input.equalsIgnoreCase("logout")) {
            this.authToken = null;
            System.out.println(result);
            return State.PRE_LOGIN;
        }

        if (input.startsWith("join")) {
            return processJoinCommand(input, result);
        }

        if (input.startsWith("observe")) {
            return processObserveCommand(input, result);
        }

        System.out.println(result);
        return State.POST_LOGIN;
    }

    private boolean isErrorResponse(String result) {
        return result.startsWith("Error:") ||
                result.startsWith("Usage:") ||
                result.startsWith("Invalid") ||
                result.startsWith("Game");
    }

    private State processJoinCommand(String input, String result) throws Exception {
        if (isErrorResponse(result)) {
            System.out.println(result);
            return State.POST_LOGIN;
        }

        String[] tokens = input.split(" ");
        String gameId = tokens[1];
        String playerColor = tokens[2];
        int actualGameId = postLoginClient.getActualGameId(gameId);

        this.gameClient = new GameClient(
                serverUrl,
                playerColor,
                actualGameId,
                new AuthData(this.authToken, this.username)
        );
        this.playerColor = playerColor;

        System.out.println(result);
        System.out.println("\n");
        System.out.println(gameClient.help());

        return State.GAME;
    }

    private State processObserveCommand(String input, String result) throws Exception {
        if (isErrorResponse(result)) {
            System.out.println(result);
            return State.POST_LOGIN;
        }

        String[] tokens = input.split(" ");
        String gameId = tokens[1];
        int actualGameId = postLoginClient.getActualGameId(gameId);

        this.gameClient = new GameClient(
                serverUrl,
                null,
                actualGameId,
                new AuthData(this.authToken, this.username)
        );
        this.playerColor = null;

        System.out.println(result);
        return State.POST_LOGIN;
    }

    private State handleGameState(String input) throws IOException {
        String result;

        if (playerColor != null) {
            result = gameClient.eval(input);
            System.out.println(result);
        } else {
            result = gameClient.evalObserver(input);
        }

        if (result.startsWith("Left")) {
            return State.POST_LOGIN;
        }

        return State.GAME;
    }


    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

    private String[] parseAuthMessage(String message) {
        if (message.startsWith("AUTH_TOKEN:")) {
            String[] parts = message.split(":", 4);

            if (parts.length >= 2) {
                this.authToken = parts[1].trim();
                this.username = parts[2].trim();

                if (parts.length >= 3) {
                    return new String[]{parts[2].trim()};
                } else {
                    return new String[]{"Login successful"};
                }
            } else {
                return new String[]{"Received auth token but with incorrect format"};
            }
        } else {
            return new String[]{message};
        }
    }

}
