package endpoint;

public class AuthResult {
    public String username;
    public String authToken;

    public AuthResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }
}
