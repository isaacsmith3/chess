package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /// TODO: fix styling
    public static ResponseException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        int status = 500;
        if (map.containsKey("status") && map.get("status") != null) {
            try {
                status = ((Double)map.get("status")).intValue();
            } catch (ClassCastException e) {
                Object statusObj = map.get("status");
                if (statusObj instanceof Number) {
                    status = ((Number)statusObj).intValue();
                } else if (statusObj instanceof String) {
                    try {
                        status = Integer.parseInt((String)statusObj);
                    } catch (NumberFormatException nfe) {
                        return new ResponseException(status, "Invalid status code");
                    }
                }
            }
        }

        String message = "Unknown error";
        if (map.containsKey("message") && map.get("message") != null) {
            message = map.get("message").toString();
        }

        return new ResponseException(status, message);
    }
}