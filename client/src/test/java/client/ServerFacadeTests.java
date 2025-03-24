package client;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import types.AuthResult;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static String serverUrl;
    private ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        serverUrl = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade(serverUrl);
        try {
            serverFacade.clear();
        } catch (ResponseException e) {
            System.out.println("Error clearing database: " + e.getMessage());
        }
    }

    @Test
    public void testRegisterPositive() throws ResponseException {
        UserData userData = new UserData("testUser", "password", "test@example.com");
        AuthResult result = serverFacade.register(userData);
        assertNotNull(result.authToken());
        assertEquals("testUser", result.username());
    }

    @Test
    public void testRegisterNegative() throws ResponseException {
        UserData userData = new UserData("testUser", "password", "test@example.com");
        AuthResult result = serverFacade.register(userData);
        assertThrows(ResponseException.class, () -> serverFacade.register(userData));
    }

    @Test public void testLoginPositive() throws ResponseException {
        UserData userData = new UserData("testUser", "password", "test@example.com");
        AuthResult result = serverFacade.register(userData);
        AuthResult authResult = serverFacade.login(userData);
        assertNotNull(result.authToken());
        assertEquals("testUser", result.username());
    }

    @Test public void testLoginNegative() throws ResponseException {
        UserData userData = new UserData("testUser", "password", "test@example.com");
        assertThrows(ResponseException.class, () -> serverFacade.login(userData));
    }

    @Test public void testLogoutPositive() throws ResponseException {
        UserData userData = new UserData("testUser", "password", "test@example.com");
        AuthResult result = serverFacade.register(userData);
        assertNotNull(result.authToken());
        serverFacade.logout(result.authToken());
    }

    @Test public void testLogoutNegative() throws ResponseException {
        UserData userData = new UserData("testUser", "password", "test@example.com");
        AuthResult result = serverFacade.register(userData);
        assertThrows(ResponseException.class, () -> serverFacade.logout("authToken"));
    }
    
}