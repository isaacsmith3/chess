package server;

import model.AuthData;

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
        System.out.println("Welcome to Chess. Sign in to start");
        System.out.println(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var input = "";

        State currentState = State.PRE_LOGIN;

        while (true) {
            printPrompt();
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("quit")) {
                if (authToken != null) {
                    postLoginClient.eval("logout");
                }
                break;
            }

            try {
                switch (currentState) {
                    case PRE_LOGIN:
                        String result = preLoginClient.eval(input);
                        String[] parsedResult = parseAuthMessage(result);

                        if (authToken != null) {
                            currentState = State.POST_LOGIN;
                            postLoginClient.setAuthToken(authToken);
                            System.out.println(parsedResult[0]);
                            System.out.println("\n");
                            System.out.println(postLoginClient.help());
                        } else {
                            System.out.println(result);
                        }
                        break;
                    case POST_LOGIN:
                        result = postLoginClient.eval(input);
                        if (input.equalsIgnoreCase("logout")) {
                            this.authToken = null;
                            currentState = State.PRE_LOGIN;
                            System.out.println(result);
                        } else if (input.startsWith("join")) {
                            if (!result.startsWith("Error:") && !result.startsWith("Usage:") &&
                                    !result.startsWith("Invalid") && !result.startsWith("Game")) {
                                String[] tokens = input.split(" ");
                                String gameId = tokens[1];
                                String playerColor = tokens[2];
                                int actualGameId = postLoginClient.getActualGameId(gameId);
                                this.gameClient = new GameClient(serverUrl, playerColor, actualGameId, new AuthData(this.authToken, this.username));
                                this.playerColor = playerColor;
                                currentState = State.GAME;
                                System.out.println(result);
                                System.out.println("\n");
                                System.out.println(gameClient.help());
                            }
                            else {
                                System.out.println(result);
                            }
                        } else if (input.startsWith("observe")) {
                            if (!result.startsWith("Error:") && !result.startsWith("Usage")) {
                                String[] tokens = input.split(" ");
                                String gameId = tokens[1];
                                int actualGameId = postLoginClient.getActualGameId(gameId);
                                this.gameClient = new GameClient(serverUrl, null, actualGameId, new AuthData(this.authToken, this.username));
                                this.playerColor = null;
                                System.out.println(result);
                            }
                            else {
                                System.out.println(result);
                            }
                        }
                        else {
                            System.out.println(result);
                        }
                        break;
                    case GAME:
                        if (playerColor != null) {
                            result = gameClient.eval(input);
                            if (result.startsWith("Left")) {
                                currentState = State.POST_LOGIN;
                            }
                            System.out.println(result);
                        } else {
                            result = gameClient.evalObserver(input);
                            if (result.startsWith("Left")) {
                                currentState = State.POST_LOGIN;
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

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
