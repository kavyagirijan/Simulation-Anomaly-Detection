package anomaly_detection;
import weka.clusterers.EM;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.List;

public class wekaGMM {
    public static void runGMM(List<JsonParser.EntityFeature> features) throws Exception {
        // Define attributes
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("longitude"));
        attributes.add(new Attribute("latitude"));
        attributes.add(new Attribute("charge"));
        attributes.add(new Attribute("routeLength"));

        // Create dataset
        Instances dataset = new Instances("EntityFeatures", attributes, features.size());
        for (JsonParser.EntityFeature f : features) {
            double[] values = {f.lon, f.lat, f.charge, f.routeLength};
            dataset.add(new DenseInstance(1.0, values));
        }

        // Run GMM
        EM gmm = new EM();
        gmm.buildClusterer(dataset);

        // Identify anomalies
        System.out.println("\nGMM Anomalies:");
        for (int i = 0; i < dataset.size(); i++) {
            int cluster = gmm.clusterInstance(dataset.get(i));
            double[] probabilities = gmm.distributionForInstance(dataset.get(i));

            if (probabilities[cluster] < 0.8) { // Low likelihood threshold
                System.out.printf("Feature: %s -> Likelihood: %.4f%n", features.get(i), probabilities[cluster]);
            }
        }
    }
}
