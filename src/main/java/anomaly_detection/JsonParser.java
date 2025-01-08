package anomaly_detection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonParser {
    public static class EntityFeature {
        public double lon;
        public double lat;
        public double charge;
        public int routeLength;

        public EntityFeature(double lon, double lat, double charge, int routeLength) {
            this.lon = lon;
            this.lat = lat;
            this.charge = charge;
            this.routeLength = routeLength;
        }

        @Override
        public String toString() {
            return "Lon: " + lon + ", Lat: " + lat + ", Charge: " + charge + ", RouteLength: " + routeLength;
        }
    }

    public static List<EntityFeature> parseJson(String filePath) {
        List<EntityFeature> features = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(filePath));

            // Iterate over all keys in the root JSON object
            Iterator<String> keys = root.fieldNames();
            while (keys.hasNext()) {
                String key = keys.next();
                JsonNode step = root.get(key);

                // Check if this key contains "entities"
                if (step != null && step.has("entities")) {
                    JsonNode entities = step.get("entities");

                    // Parse each entity
                    for (JsonNode entity : entities) {
                        double lon = entity.has("lon") ? entity.get("lon").asDouble() : 0.0;
                        double lat = entity.has("lat") ? entity.get("lat").asDouble() : 0.0;
                        double charge = entity.has("charge") ? entity.get("charge").asDouble() : 0.0;
                        int routeLength = entity.has("routeLength") ? entity.get("routeLength").asInt() : 0;

                        features.add(new EntityFeature(lon, lat, charge, routeLength));
                    }
                    break; // Stop after finding the first valid "entities" key
                }
            }

            if (features.isEmpty()) {
                System.out.println("No 'entities' found in file: " + filePath);
            }

        } catch (Exception e) {
            System.out.println("Error parsing file: " + filePath);
            e.printStackTrace();
        }
        return features;
    }
}
