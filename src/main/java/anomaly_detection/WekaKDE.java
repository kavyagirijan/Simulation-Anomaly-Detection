package anomaly_detection;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.List;

public class WekaKDE {
    public static void runKDE(List<JsonParser.EntityFeature> features) throws Exception {
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
            dataset.add(new weka.core.DenseInstance(1.0, values));
        }

        // Compute densities
        double[] densities = new double[dataset.size()];
        double bandwidth = 1.0; // Bandwidth for the kernel

        for (int i = 0; i < dataset.size(); i++) {
            double density = 0.0;
            for (int j = 0; j < dataset.size(); j++) {
                density += gaussianKernel(dataset.instance(i), dataset.instance(j), bandwidth);
            }
            density /= dataset.size();
            densities[i] = density;
        }

        // Identify anomalies
        double densityThreshold = 0.06; // Low density threshold
        System.out.println("\nKDE Anomalies:");
        for (int i = 0; i < densities.length; i++) {
            if (densities[i] < densityThreshold) {
                System.out.printf("Feature: %s -> Density: %.4f%n", features.get(i), densities[i]);
            }
        }
    }

    private static double gaussianKernel(Instance x1, Instance x2, double bandwidth) {
        double sum = 0.0;
        for (int i = 0; i < x1.numAttributes(); i++) {
            double diff = x1.value(i) - x2.value(i);
            sum += diff * diff;
        }
        return Math.exp(-sum / (2 * bandwidth * bandwidth)) / Math.sqrt(2 * Math.PI * bandwidth * bandwidth);
    }
}
