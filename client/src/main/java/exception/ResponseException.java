package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ResponseException extends Exception {
    final private int statusCode;

    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public static ResponseException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        int statusCode = 500;

        if (map.containsKey("status") && map.get("status") != null) {
            try {
                statusCode = ((Double)map.get("status")).intValue();

            } catch (ClassCastException e) {
                Object statusObj = map.get("status");

                if (statusObj instanceof Number) {
                    statusCode = ((Number)statusObj).intValue();

                } else if (statusObj instanceof String) {
                    try {
                        statusCode = Integer.parseInt((String)statusObj);
                    } catch (NumberFormatException exception) {
                        return new ResponseException(statusCode, "Invalid code number");
                    }
                }
            }
        }

        String message = "Unknown error";
        if (map.containsKey("message") && map.get("message") != null) {
            message = map.get("message").toString();
        }

        return new ResponseException(statusCode, message);
    }
}