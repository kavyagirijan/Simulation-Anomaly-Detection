package anomaly_detection;
import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Directory containing the JSON files
            String directoryPath = "src/main/resources";

            // List all JSON files in the directory
            File folder = new File(directoryPath);
            File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));

            if (jsonFiles == null || jsonFiles.length == 0) {
                System.out.println("No JSON files found in the directory!");
                return;
            }

            // Iterate through each JSON file
            for (File jsonFile : jsonFiles) {
                System.out.println("\nProcessing File: " + jsonFile.getName());

                // Step 1: Parse JSON and extract features
                List<JsonParser.EntityFeature> features = JsonParser.parseJson(jsonFile.getAbsolutePath());
                System.out.println("Extracted Features: " + features);

                // Step 2: Run K-Means Clustering
                System.out.println("\nRunning K-Means Clustering:");
                WekaKMeans.runKMeans(features, 3);

                // Step 3: Run GMM Clustering
                System.out.println("\nRunning GMM Clustering:");
                wekaGMM.runGMM(features);

                // Step 4: Run KDE
                System.out.println("\nRunning KDE:");
                WekaKDE.runKDE(features);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
