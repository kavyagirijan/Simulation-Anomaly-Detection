package anomaly_detection;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.EuclideanDistance;

import java.util.ArrayList;
import java.util.List;

public class WekaKMeans {
    public static void runKMeans(List<JsonParser.EntityFeature> features, int k) throws Exception {
        // Set up attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("longitude"));
        attributes.add(new Attribute("latitude"));
        attributes.add(new Attribute("charge"));
        attributes.add(new Attribute("load"));
        attributes.add(new Attribute("routeLength"));

        // Prepare the dataset
        Instances dataset = new Instances("EntityFeatures", attributes, features.size());
        for (JsonParser.EntityFeature f : features) {
            double[] values = {f.lon, f.lat, f.charge, f.load, f.routeLength};
            dataset.add(new DenseInstance(1.0, values));
        }

        // Run K-Means Clustering
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setNumClusters(k);
        kMeans.buildClusterer(dataset);

        //System.out.println("K-Means Clustering Results:");

        // Calculate Silhouette Score
        double silhouetteScore = calculateSilhouette(dataset, kMeans);
       

        // Calculate Davies-Bouldin Index
        double daviesBouldinIndex = calculateDBI(dataset, kMeans);
       
        // Anomaly detection based on distance to centroid
        double threshold = 0.042;
        for (int i = 0; i < dataset.size(); i++) {
            int cluster = kMeans.clusterInstance(dataset.get(i));
            double distance = kMeans.getDistanceFunction().distance(dataset.instance(i), kMeans.getClusterCentroids().instance(cluster));

            if (distance < threshold) {
                System.out.printf("Feature: %s -> Cluster: %d -> Distance: %.4f%n", features.get(i), cluster, distance);
                System.out.println("  -> Anomaly Detected (Far from Centroid)!");
                System.out.println("Silhouette Score: " + silhouetteScore);
                System.out.println("Davies-Bouldin Index: " + daviesBouldinIndex);

            }
        }
    }

    // Calculate Silhouette Score
    public static double calculateSilhouette(Instances dataset, SimpleKMeans kMeans) throws Exception {
        double totalSilhouette = 0.0;
        int numInstances = dataset.size();
        EuclideanDistance distanceFunction = new EuclideanDistance();
        distanceFunction.setInstances(dataset);  // Set the dataset for the distance function

        for (int i = 0; i < numInstances; i++) {
            int clusterId = kMeans.clusterInstance(dataset.instance(i));
            double a_i = 0.0;
            double b_i = Double.MAX_VALUE;

            // Calculate a(i) - average distance to all other points in the same cluster
            for (int j = 0; j < numInstances; j++) {
                if (kMeans.clusterInstance(dataset.instance(j)) == clusterId) {
                    a_i += distanceFunction.distance(dataset.instance(i), dataset.instance(j));
                }
            }
            a_i /= numInstances; // Normalize

            // Calculate b(i) - average distance to all points in the nearest cluster
            for (int j = 0; j < kMeans.numberOfClusters(); j++) {
                if (j != clusterId) {
                    double sumDistance = 0.0;
                    int count = 0;
                    for (int m = 0; m < numInstances; m++) {
                        if (kMeans.clusterInstance(dataset.instance(m)) == j) {
                            sumDistance += distanceFunction.distance(dataset.instance(i), dataset.instance(m));
                            count++;
                        }
                    }
                    double b_ij = sumDistance / count;  // Average distance to points in cluster j
                    b_i = Math.min(b_i, b_ij);
                }
            }

            totalSilhouette += (b_i - a_i) / Math.max(a_i, b_i);
        }

        return totalSilhouette / numInstances;
    }

    // Calculate Davies-Bouldin Index
    public static double calculateDBI(Instances dataset, SimpleKMeans kMeans) throws Exception {
        int numClusters = kMeans.numberOfClusters();
        double dbIndex = 0.0;
        EuclideanDistance distanceFunction = new EuclideanDistance();
        distanceFunction.setInstances(dataset);  // Set the dataset for the distance function

        for (int i = 0; i < numClusters; i++) {
            double clusterScatter = calculateClusterScatter(dataset, kMeans, i);
            double maxSimilarity = Double.MIN_VALUE;

            for (int j = 0; j < numClusters; j++) {
                if (i != j) {
                    double clusterDistance = distanceFunction.distance(kMeans.getClusterCentroids().instance(i), kMeans.getClusterCentroids().instance(j));
                    double similarity = (clusterScatter + calculateClusterScatter(dataset, kMeans, j)) / clusterDistance;
                    maxSimilarity = Math.max(maxSimilarity, similarity);
                }
            }
            dbIndex += maxSimilarity;
        }

        return dbIndex / numClusters;
    }

    // Calculate the scatter (average distance from centroid) for a given cluster
    private static double calculateClusterScatter(Instances dataset, SimpleKMeans kMeans, int clusterIndex) throws Exception {
        double scatter = 0.0;
        int count = 0;
        for (int i = 0; i < dataset.size(); i++) {
            if (kMeans.clusterInstance(dataset.get(i)) == clusterIndex) {
                scatter += kMeans.getDistanceFunction().distance(dataset.get(i), kMeans.getClusterCentroids().instance(clusterIndex));
                count++;
            }
        }
        return (count > 0) ? scatter / count : 0.0;
    }
}
