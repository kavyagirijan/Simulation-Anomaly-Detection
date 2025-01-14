package anomaly_detection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/0.json";

            List<JsonParser.EntityFeature> features = JsonParser.parseJson(filePath);
            System.out.println("Extracted Features: " + features);

            System.out.println("\nRunning K-Means Clustering:");
            WekaKMeans.runKMeans(features, 3);

            System.out.println("\nRunning GMM Clustering:");
            WekaGMM.runGMM(features);

            System.out.println("\nRunning KDE:");
            WekaKDE.runKDE(features);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
