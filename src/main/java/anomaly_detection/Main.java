package anomaly_detection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/0.json";
            
            // Step 1: Parse JSON and extract features
            List<JsonParser.EntityFeature> features = JsonParser.parseJson(filePath);
            System.out.println("Extracted Features: " + features);

            // Step 2: Run K-Means Clustering
            System.out.println("\nRunning K-Means Clustering:");
            WekaKMeans.runKMeans(features, 3);

            // Step 3: Run Gaussian Mixture Model
            System.out.println("\nRunning Gaussian Mixture Model:");
            wekaGMM.runGMM(features);

             // Step 4: Run K-Density Estimation
            System.out.println("\nRunning Kernel Density Estimation:");
            WekaKDE.runKDE(features);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


