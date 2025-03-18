import server.Repl;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:5050";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }
}