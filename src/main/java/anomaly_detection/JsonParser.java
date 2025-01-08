package anomaly_detection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
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

            // Iterate through entities at step 0
            JsonNode entities = root.get("0").get("entities");
            for (JsonNode entity : entities) {
                double lon = entity.get("lon").asDouble();
                double lat = entity.get("lat").asDouble();
                double charge = entity.get("charge").asDouble();
                int routeLength = entity.get("routeLength").asInt();

                features.add(new EntityFeature(lon, lat, charge, routeLength));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return features;
    }
}

