import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PresentationAI {

    public static void main(String[] args) {
        try {
            String presentationSuggestion = generatePresentationSuggestion("latest trends in automotive industry");
            System.out.println("Presentation Suggestion: " + presentationSuggestion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generatePresentationSuggestion(String topic) throws IOException {
        String apiKey = "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"; // API key goes here
        String model = "text-davinci-003"; // GPT-3 model for text generation
        String url = "https://api.openai.com/v1/engines/" + model + "/completions";

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Set the request method to POST
            con.setRequestMethod("POST");

            // Set the authorization header
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            // Enable input and output streams
            con.setDoOutput(true);

            // Build the request body
            String requestBody = "{\"prompt\": \"" + topic + "\", \"max_tokens\": 100}";

            // Write the request body to the output stream
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = con.getResponseCode();

            // Check if the response code indicates success
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Get the response
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    // Extract and return the response content
                    return extractContentFromResponse(response.toString());
                }
            } else {
                // Print error message for non-successful response
                System.err.println("Error response code: " + responseCode);
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(con.getErrorStream()))) {
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        System.err.println(errorLine);
                    }
                }
                throw new RuntimeException("Failed : HTTP error code : " + responseCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method extracts the response expected from OpenAI and returns it.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("\"choices\":[{\"text\":\"") + 21; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }
}
