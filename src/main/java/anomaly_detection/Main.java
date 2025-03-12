package anomaly_detection;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Step 1: Specify the directory containing JSON files
            String resourceDirectory = "src/main/resources";
            File folder = new File(resourceDirectory);
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

            if (files == null || files.length == 0) {
                System.out.println("No JSON files found in the directory.");
                return;
            }

            // Combined feature list from all files
            List<JsonParser.EntityFeature> allFeatures = new ArrayList<>();

            // Step 1: Read and merge data from all files
            for (File file : files) {
                //System.out.println("\nProcessing File: " + file.getName());
                List<JsonParser.EntityFeature> features = JsonParser.parseJson(file.getAbsolutePath());
                allFeatures.addAll(features);
         

                 // Step 5: Apply anomaly detection techniques
                //System.out.println("\nRunning K-Means Clustering:");
               // WekaKMeans.runKMeans(allFeatures, 3);

               System.out.println("\nRunning GMM Clustering:");
                WekaGMM.runGMM(allFeatures);

               // System.out.println("\nRunning KDE:");
               //WekaKDE.runKDE(allFeatures);
                
            };
            
          }  
            
           catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}


