package anomaly_detection;

import weka.clusterers.EM;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.List;

public class wekaGMM {
    /**
     * Performs Gaussian Mixture Model (GMM) clustering and identifies anomalies.
     *
     * @param features List of features extracted from the JSON data.
     * @throws Exception If there is an error during clustering.
     */
    public static void runGMM(List<JsonParser.EntityFeature> features) throws Exception {
        // Step 1: Define attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("longitude"));
        attributes.add(new Attribute("latitude"));
        attributes.add(new Attribute("charge"));
        attributes.add(new Attribute("routeLength"));

        // Step 2: Create dataset
        Instances dataset = new Instances("EntityFeatures", attributes, features.size());
        for (JsonParser.EntityFeature f : features) {
            double[] values = {f.lon, f.lat, f.charge, f.routeLength};
            dataset.add(new DenseInstance(1.0, values));
        }

        // Step 3: Run Gaussian Mixture Model (GMM) using Weka's EM algorithm
        EM gmm = new EM();
        gmm.buildClusterer(dataset);

        // Step 4: Track cluster sizes
        int numClusters = gmm.numberOfClusters();
        int[] clusterSizes = new int[numClusters];
        for (int i = 0; i < dataset.size(); i++) {
            int cluster = gmm.clusterInstance(dataset.get(i));
            clusterSizes[cluster]++;
        }

        // Step 5: Display results and flag anomalies
        System.out.println("GMM Clustering Results:");
        System.out.println("Instance -> Cluster -> Likelihood");

        for (int i = 0; i < dataset.size(); i++) {
            int cluster = gmm.clusterInstance(dataset.get(i));  // Get assigned cluster
            double[] probabilities = gmm.distributionForInstance(dataset.get(i)); // Cluster probabilities

            System.out.print("Feature: " + features.get(i) + " -> Cluster: " + cluster);
            System.out.printf(" -> Likelihood: %.4f%n", probabilities[cluster]);

            // Step 6: Identify anomalies
            if (probabilities[cluster] < 0.8) { // Cluster Probability Threshold
                System.out.println("  -> Anomaly Detected (Low Likelihood)!");
            }

            if (clusterSizes[cluster] < (0.08 * dataset.size())) { // Cluster Size Threshold
                System.out.println("  -> Anomaly Detected (Small Cluster)!");
            }
        }
    }
}
