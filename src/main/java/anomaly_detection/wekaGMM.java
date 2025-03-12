package anomaly_detection;

import weka.clusterers.EM;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.List;

public class WekaGMM {
    public static void runGMM(List<JsonParser.EntityFeature> features) throws Exception {
        // Step 1: Define attributes for clustering
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("longitude"));
        attributes.add(new Attribute("latitude"));
        attributes.add(new Attribute("charge"));
        attributes.add(new Attribute("routeLength"));
        attributes.add(new Attribute("load"));

        // Step 2: Create dataset for clustering
        Instances dataset = new Instances("EntityFeatures", attributes, features.size());
        for (JsonParser.EntityFeature f : features) {
            double[] values = {f.lon, f.lat, f.charge, f.routeLength, f.load};
            dataset.add(new DenseInstance(1.0, values));
        }

        // Step 3: Initialize and build GMM (EM algorithm)
        EM gmm = new EM(); 
        gmm.buildClusterer(dataset);

        // Step 4: Calculate Silhouette Score and Davies-Bouldin Index
        double silhouetteScore = calculateSilhouette(dataset, gmm);
        double daviesBouldinIndex = calculateDaviesBouldin(dataset, gmm);

        // Step 5: Output clustering evaluation metrics
       // System.out.println("GMM Clustering Results:");
        
        // Step 6: Detect anomalies based on cluster likelihoods
        for (int i = 0; i < dataset.size(); i++) {
            int cluster = gmm.clusterInstance(dataset.get(i));
            double[] probabilities = gmm.distributionForInstance(dataset.get(i));

            if (probabilities[cluster] <= 0.5) {
                System.out.printf("Feature: %s -> Cluster: %d -> Likelihood: %.4f%n", features.get(i), cluster, probabilities[cluster]);
                System.out.println("  -> Anomaly Detected (Low Likelihood)!");
                System.out.println("Silhouette Score: " + silhouetteScore);
                System.out.println("Davies-Bouldin Index: " + daviesBouldinIndex);

            }
        }
    }

    // Method to calculate the Silhouette Score
    private static double calculateSilhouette(Instances dataset, EM gmm) throws Exception {
        double totalSilhouette = 0.0;
        int numInstances = dataset.size();
        EuclideanDistance distanceFunction = new EuclideanDistance(dataset);

        for (int i = 0; i < numInstances; i++) {
            int cluster = gmm.clusterInstance(dataset.get(i));
            double a_i = 0.0; // average distance to instances in the same cluster
            double b_i = Double.MAX_VALUE; // smallest average distance to instances in other clusters

            // Calculate distance to all other instances
            for (int j = 0; j < numInstances; j++) {
                if (i == j) continue; // Skip distance to itself

                double distance = distanceFunction.distance(dataset.get(i), dataset.get(j)); // Use Euclidean distance

                // Calculate average distance within the same cluster
                if (gmm.clusterInstance(dataset.get(j)) == cluster) {
                    a_i += distance;
                } else { // Calculate average distance to the nearest other cluster
                    b_i = Math.min(b_i, distance);
                }
            }

            a_i /= numInstances; // Normalize the within-cluster distance
            totalSilhouette += (b_i - a_i) / Math.max(a_i, b_i); // Compute silhouette score for instance
        }

        return totalSilhouette / numInstances;
    }

    // Method to calculate Davies-Bouldin Index
    private static double calculateDaviesBouldin(Instances dataset, EM gmm) throws Exception {
        int numClusters = gmm.numberOfClusters();
        double dbIndex = 0.0;

        // Step 1: Calculate centroids manually
        Instances centroids = new Instances(dataset, numClusters);
        for (int c = 0; c < numClusters; c++) {
            double[] centroid = new double[dataset.numAttributes()];
            int count = 0;

            // Calculate mean for each cluster to find its centroid
            for (int i = 0; i < dataset.size(); i++) {
                if (gmm.clusterInstance(dataset.get(i)) == c) {
                    for (int j = 0; j < dataset.numAttributes(); j++) {
                        centroid[j] += dataset.get(i).value(j);
                    }
                    count++;
                }
            }

            for (int j = 0; j < dataset.numAttributes(); j++) {
                centroid[j] /= count; // Normalize to get the centroid
            }
            centroids.add(new DenseInstance(1.0, centroid));
        }

        // Step 2: Calculate Davies-Bouldin Index
        EuclideanDistance distanceFunction = new EuclideanDistance(dataset);
        for (int i = 0; i < numClusters; i++) {
            double maxR = Double.MIN_VALUE;
            for (int j = 0; j < numClusters; j++) {
                if (i != j) {
                    double Si = calculateClusterSpread(dataset, i, gmm, distanceFunction);
                    double Sj = calculateClusterSpread(dataset, j, gmm, distanceFunction);
                    double dij = distanceFunction.distance(centroids.get(i), centroids.get(j));

                    double R = (Si + Sj) / dij;
                    maxR = Math.max(maxR, R);
                }
            }
            dbIndex += maxR;
        }

        return dbIndex / numClusters;
    }

    // Method to calculate the spread of a cluster (average distance between instances and the cluster centroid)
    private static double calculateClusterSpread(Instances dataset, int clusterIndex, EM gmm, EuclideanDistance distanceFunction) throws Exception {
        double spread = 0.0;
        int numInstances = dataset.size();
        int count = 0;

        // Calculate the spread (variance) of a cluster
        for (int i = 0; i < numInstances; i++) {
            if (gmm.clusterInstance(dataset.get(i)) == clusterIndex) {
                spread += distanceFunction.distance(dataset.get(i), dataset.get(clusterIndex));
                count++;
            }
        }

        return spread / count; // Return average spread
    }
}
