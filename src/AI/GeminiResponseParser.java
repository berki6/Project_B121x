package AI;


/**
 *
 * @author berek
 */

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GeminiResponseParser {
    
    public String parseResponse(String jsonResponse) {
        try {
            Gson gson = new Gson();
            // Parse JSON response to JsonObject
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            
            // Check if jsonObject is not null
            if (jsonObject != null) {
                // Access the JsonArray "candidates"
                JsonArray candidatesArray = jsonObject.getAsJsonArray("candidates");
                // Check if candidatesArray is not null and not empty
                if (candidatesArray != null && candidatesArray.size() > 0) {
                    // Access the first candidate JsonObject
                    JsonObject candidate = candidatesArray.get(0).getAsJsonObject();
                    // Access the "content" JsonObject
                    JsonObject content = candidate.getAsJsonObject("content");
                    // Access the "parts" JsonArray
                    JsonArray parts = content.getAsJsonArray("parts");
                    // Loop through the parts array if needed
                    StringBuilder parsedResponse = new StringBuilder();
                    for (int i = 0; i < parts.size(); i++) {
                        JsonObject part = parts.get(i).getAsJsonObject();
                        // Access and process individual parts
                        // For example:
                        String text = part.get("text").getAsString();
                        // Append text to parsedResponse
                        parsedResponse.append(text).append("\n");
                    }
                    // Return the parsed response
                    return parsedResponse.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle parsing errors
        }
        // Return null if parsing fails
        return null;
    }
}



