package AI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeminiApiClient {

    private static final String API_KEY = "YOUR_GEMINI_API_KEY";

    public String sendMessage(String userMessage) {
        try {
            // Construct the request URL
            String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + API_KEY;

            // Construct the JSON payload
            String payload = "{\"contents\": [{\"parts\":[{\"text\": \"" + userMessage + "\"}]}]}";

            // Create a URL object
            URL url = new URL(apiUrl);

            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method
            connection.setRequestMethod("POST");

            // Set the request headers
            connection.setRequestProperty("Content-Type", "application/json");

            // Enable output
            connection.setDoOutput(true);

            // Write the payload to the connection's output stream
            connection.getOutputStream().write(payload.getBytes());

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response
            BufferedReader reader;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse and extract the response
            GeminiResponseParser parser = new GeminiResponseParser();
            String botResponse = parser.parseResponse(response.toString());

            // Close the connection
            connection.disconnect();

            return botResponse;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Failed to connect to Gemini API";
        }
    }
}
