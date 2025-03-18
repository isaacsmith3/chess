package server;

import java.util.Scanner;

enum State { PRE_LOGIN, POST_LOGIN, GAME }

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;

    public Repl(String serverUrl) {
        this.preLoginClient = new PreLoginClient(serverUrl);
        this.postLoginClient = new PostLoginClient();
        this.gameClient = new GameClient();
    }

    public void run() {
        System.out.println("Welcome to Chess. Sign in to start");
        System.out.println(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var input = "";

        State currentState = State.PRE_LOGIN;

        String authToken = null;

        while (!input.equals("quit")) {
            printPrompt();
            input = scanner.nextLine();

            try {
                switch (currentState) {
                    case PRE_LOGIN:
                        String result = preLoginClient.eval(input);
                        if (result.startsWith("AUTH_TOKEN:")) {
                            System.out.println("Do something");
                        }
                        System.out.println(result);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

}
