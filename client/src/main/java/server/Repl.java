package server;

import java.util.Scanner;

enum State { PRE_LOGIN, POST_LOGIN, GAME }

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private String authToken = null;

    public Repl(String serverUrl) {
        this.preLoginClient = new PreLoginClient(serverUrl);
        this.postLoginClient = new PostLoginClient(serverUrl);
        this.gameClient = new GameClient();
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
                            System.out.println(postLoginClient.help());
                        } else {
                            System.out.println(result);
                        }
                        break;
                    case POST_LOGIN:
                        result = postLoginClient.eval(input);
                        System.out.println(result);
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

                if (parts.length >= 3) {
                    return new String[]{parts[2].trim()};
                } else {
                    return new String[]{"Login successful"};
                }
            } else {
                return new String[]{"Received auth token but with unexpected format"};
            }
        } else {
            return new String[]{message};
        }
    }

}
