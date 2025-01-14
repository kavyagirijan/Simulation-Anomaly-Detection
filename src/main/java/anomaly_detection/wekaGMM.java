package anomaly_detection;

import weka.clusterers.EM;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.List;

public class WekaGMM {
    public static void runGMM(List<JsonParser.EntityFeature> features) throws Exception {
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

        EM gmm = new EM();
        gmm.buildClusterer(dataset);

        System.out.println("GMM Clustering Results:");
        for (int i = 0; i < dataset.size(); i++) {
            int cluster = gmm.clusterInstance(dataset.get(i));
            double[] probabilities = gmm.distributionForInstance(dataset.get(i));
            System.out.printf("Feature: %s -> Cluster: %d -> Likelihood: %.4f%n", features.get(i), cluster, probabilities[cluster]);

            if (probabilities[cluster] < 0.2) {
                System.out.println("  -> Anomaly Detected: Entity behavior or resource usage unlikely (Low Likelihood)!");
            }
        }
    }
}
