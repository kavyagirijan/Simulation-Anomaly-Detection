package anomaly_detection;

import weka.clusterers.SimpleKMeans;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.List;

public class WekaKMeans {
    /**
     * Performs K-Means clustering and identifies anomalies based on distance to cluster centroids.
     *
     * @param features List of features extracted from the JSON data.
     * @param k Number of clusters.
     * @throws Exception If there is an error during clustering.
     */
    public static void runKMeans(List<JsonParser.EntityFeature> features, int k) throws Exception {
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

        // Step 3: Run K-Means
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setNumClusters(k);
        kMeans.buildClusterer(dataset);

        // Step 4: Identify anomalies based on distance to centroids
        System.out.println("K-Means Clustering Results:");
        System.out.println("Instance -> Cluster -> Distance to Centroid");
        double threshold = 0.5; // Distance threshold for anomaly detection

        for (int i = 0; i < dataset.size(); i++) {
            int cluster = kMeans.clusterInstance(dataset.get(i));
            double distance = kMeans.getDistanceFunction().distance(dataset.instance(i), kMeans.getClusterCentroids().instance(cluster));

            System.out.printf("Feature: %s -> Cluster: %d -> Distance: %.4f%n", features.get(i), cluster, distance);

            if (distance > threshold) {
                System.out.println("  -> Anomaly Detected (Far from Centroid)!");
            }
        }
    }
}
