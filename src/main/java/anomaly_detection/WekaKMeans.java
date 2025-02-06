package anomaly_detection;
import weka.clusterers.SimpleKMeans;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.List;

public class WekaKMeans {
    public static void runKMeans(List<JsonParser.EntityFeature> features, int k) throws Exception {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("longitude"));
        attributes.add(new Attribute("latitude"));
        attributes.add(new Attribute("charge"));
        attributes.add(new Attribute("load"));
        attributes.add(new Attribute("routeLength"));

        Instances dataset = new Instances("EntityFeatures", attributes, features.size());
        for (JsonParser.EntityFeature f : features) {
            double[] values = {f.lon, f.lat, f.charge, f.load, f.routeLength};
            dataset.add(new DenseInstance(1.0, values));
        }

        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setNumClusters(k);
        kMeans.buildClusterer(dataset);

        System.out.println("K-Means Clustering Results:");
        double threshold = 0.042;

        for (int i = 0; i < dataset.size(); i++) {
            int cluster = kMeans.clusterInstance(dataset.get(i));
            double distance = kMeans.getDistanceFunction().distance(dataset.instance(i), kMeans.getClusterCentroids().instance(cluster));
         
         //System.out.printf("Feature: %s -> Cluster: %d -> Distance: %.4f%n", features.get(i), cluster, distance);
        
            if (distance < threshold) {
            	
           	 System.out.printf("Feature: %s -> Cluster: %d -> Distance: %.4f%n", features.get(i), cluster, distance);
                System.out.println("  -> Anomaly Detected (Far from Centroid)!");
            }
          
        }
    }
}