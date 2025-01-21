package anomaly_detection;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    public static class EntityFeature {
        public String team;       // Team name
        public String role;       // Entity role (e.g., car, drone)
        public double lon;        // Longitude
        public double lat;        // Latitude
        public double charge;     // Charge level
        public double load;       // Current load
        public int routeLength;   // Planned route length
        public String lastAction; // Last action performed

        public EntityFeature(String team, String role, double lon, double lat, double charge, double load, int routeLength, String lastAction) {
            this.team = team;
            this.role = role;
            this.lon = lon;
            this.lat = lat;
            this.charge = charge;
            this.load = load;
            this.routeLength = routeLength;
            this.lastAction = lastAction;
        }

        @Override
        public String toString() {
            return "Team: " + team + ", Role: " + role + ", Lon: " + lon + ", Lat: " + lat +
                    ", Charge: " + charge + ", Load: " + load + ", RouteLength: " + routeLength + 
                    ", LastAction: " + lastAction;
        }
    }

    public static List<EntityFeature> parseJson(String filePath) {
        List<EntityFeature> features = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(filePath));

            for (JsonNode step : root) {
                JsonNode entities = step.get("entities");
                for (JsonNode entity : entities) {
                    String team = entity.get("team").asText();
                    String role = entity.get("role").asText();
                    double lon = entity.get("lon").asDouble();
                    double lat = entity.get("lat").asDouble();
                    double charge = entity.get("charge").asDouble();
                    double load = entity.get("load").asDouble();
                    int routeLength = entity.get("routeLength").asInt();

                    // Parse the lastAction object
                    JsonNode lastActionNode = entity.get("lastAction");
                    String lastAction = parseLastAction(lastActionNode);

                    features.add(new EntityFeature(team, role, lon, lat, charge, load, routeLength, lastAction));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return features;
    }

    /**
     * Parses the lastAction object into a readable string.
     *
     * @param lastActionNode The JSON node containing the lastAction data.
     * @return A string representation of the last action.
     */
    private static String parseLastAction(JsonNode lastActionNode) {
        if (lastActionNode == null || lastActionNode.isNull()) {
            return "None";
        }
        StringBuilder actionBuilder = new StringBuilder();
        String result = lastActionNode.get("result").asText("unknown");
        String type = lastActionNode.get("type").asText("unknown");
        actionBuilder.append("").append(result).append(", ").append(type);

        JsonNode paramsNode = lastActionNode.get("params");
        if (paramsNode != null && paramsNode.isArray()) {
            actionBuilder.append("").append(paramsNode.toString());
        }
        return actionBuilder.toString();
    }
}



