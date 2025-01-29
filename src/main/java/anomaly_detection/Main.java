package anomaly_detection;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Step 1: Specify the directory containing JSON files
            String resourceDirectory = "src/main/resources2";

            // Step 2: Get all JSON files in the directory
            File folder = new File(resourceDirectory);
            File[] jsonFiles = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (jsonFiles == null || jsonFiles.length == 0) {
                System.out.println("No JSON files found in the specified directory.");
                return;
            }

            // Step 3: Iterate through each JSON file
            for (File jsonFile : jsonFiles) {
                System.out.println("\nProcessing file: " + jsonFile.getName());

                // Step 4: Parse JSON and extract features
                List<JsonParser.EntityFeature> features = JsonParser.parseJson(jsonFile.getAbsolutePath());
               //System.out.println("Extracted Features: " + features);

                 // Step 5: Apply anomaly detection techniques
                // System.out.println("\nRunning K-Means Clustering:");
                //WekaKMeans.runKMeans(features, 3);

                 System.out.println("\nRunning GMM Clustering:");
                WekaGMM.runGMM(features);

               // System.out.println("\nRunning KDE:");
               //WekaKDE.runKDE(features);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


