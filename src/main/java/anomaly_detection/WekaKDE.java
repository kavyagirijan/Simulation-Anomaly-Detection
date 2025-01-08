package anomaly_detection;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.List;

public class WekaKDE {
    /**
     * Performs Kernel Density Estimation (KDE) and identifies anomalies based on density.
     *
     * @param features List of features extracted from the JSON data.
     * @throws Exception If there is an error during density estimation.
     */
    public static void runKDE(List<JsonParser.EntityFeature> features) throws Exception {
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
            dataset.add(new weka.core.DenseInstance(1.0, values));
        }

        // Step 3: Compute densities manually (simplified KDE with Gaussian kernel)
        double[] densities = new double[dataset.size()];
        double bandwidth = 1.0; // Bandwidth for the kernel (adjust as needed)

        for (int i = 0; i < dataset.size(); i++) {
            double density = 0.0;

            // Sum kernel contributions from all points
            for (int j = 0; j < dataset.size(); j++) {
                density += gaussianKernel(dataset.instance(i), dataset.instance(j), bandwidth);
            }
            density /= dataset.size(); // Normalize density by the number of instances
            densities[i] = density;
        }

        // Step 4: Identify anomalies based on density
        double densityThreshold = 0.06; // Threshold for low density (adjust as needed)
        System.out.println("KDE Results (Density Estimation):");

        for (int i = 0; i < densities.length; i++) {
            System.out.printf("Feature: %s -> Density: %.4f%n", features.get(i), densities[i]);

            if (densities[i] < densityThreshold) {
                System.out.println("  -> Anomaly Detected (Low Density)!");
            }
        }
    }

    /**
     * Gaussian kernel function for density estimation.
     *
     * @param x1        Instance 1.
     * @param x2        Instance 2.
     * @param bandwidth Bandwidth for the kernel.
     * @return Kernel contribution between the two instances.
     */
    private static double gaussianKernel(Instance x1, Instance x2, double bandwidth) {
        double sum = 0.0;
        for (int i = 0; i < x1.numAttributes(); i++) {
            double diff = x1.value(i) - x2.value(i);
            sum += diff * diff;
        }
        double squaredDist = sum;
        return Math.exp(-squaredDist / (2 * bandwidth * bandwidth)) / Math.sqrt(2 * Math.PI * bandwidth * bandwidth);
    }
}
