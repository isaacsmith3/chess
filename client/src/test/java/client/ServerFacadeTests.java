package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.PreLoginClient;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    private static PreLoginClient client;
    private static String serverUrl;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(5051);
        serverUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
        client = new PreLoginClient(serverUrl);

        try {
            new ServerFacade(serverUrl).clear();
        } catch (ResponseException e) {
            System.out.println("Error clearing database: " + e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() {
        try {
            new ServerFacade(serverUrl).clear();
        } catch (ResponseException e) {
            System.out.println("Error clearing database: " + e.getMessage());
        }
    }

    @Test
    public void testHelpCommandPositive() {
        String result = client.help();
        assertTrue(result.contains("Help Menu"));
        assertTrue(result.contains("register"));
        assertTrue(result.contains("login"));
        assertTrue(result.contains("quit"));
    }

}