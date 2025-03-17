package server;

import java.util.Scanner;

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
        System.out.println(preLoginClient);

        Scanner scanner = new Scanner(System.in);
        var result = "";


    }

}
