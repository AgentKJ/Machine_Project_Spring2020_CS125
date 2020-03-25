package edu.illinois.cs.cs125.spring2020.mp.logic;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * GameSetUp class.
 */
public class GameSetup {

    /**
     * Creates a JSON object representing the configuration of a multiplayer area mode game.
     * Refer to our API documentation for the structure of the output JSON.
     * The configuration is valid if there is at least one invitee and a positive (larger than zero) cell size.
     * @param invitees all players involved in the game (never null)
     * @param area the area boundaries
     * @param cellSize the desired cell size in meters
     * @return a JSON object usable by the /games/create endpoint or null if the configuration is invalid
     */
    public static com.google.gson.JsonObject areaMode(final java.util.List<Invitee> invitees,
                                                      final com.google.android.gms.maps.model.LatLngBounds area,
                                                      final int cellSize) {
        if (invitees.size() == 0) {
            return null;
        }
        if (cellSize <= 0) {
            return null;
        }

        JsonObject output = new JsonObject();
        output.addProperty("mode", "area");
        output.addProperty("cellSize", cellSize);
        output.addProperty("areaNorth", area.northeast.latitude);
        output.addProperty("areaEast", area.northeast.longitude);
        output.addProperty("areaSouth", area.southwest.latitude);
        output.addProperty("areaWest", area.southwest.longitude);

        JsonArray members = new JsonArray();
        for (Invitee member : invitees) {
            JsonObject input = new JsonObject();
            input.addProperty("email", member.getEmail());
            input.addProperty("team", member.getTeamId());
            members.add(input);
        }
        output.add("invitees", members);

        return output;
    }

    /**
     * Creates a JSON object representing the configuration of a multiplayer target mode game.
     * Refer to our API documentation for the structure of the output JSON.
     * The configuration is valid if there is at least one invitee, at least one target,
     * and a positive (larger than zero) proximity threshold. If the configuration is invalid,
     * this function returns null.
     * @param invitees all players involved in the game (never null)
     * @param targets the positions of all targets (never null)
     * @param proximityThreshold the proximity threshold in meters
     * @return a JSON object usable by the /games/create endpoint or null if the configuration is invalid
     */
    public static com.google.gson.JsonObject targetMode(
            final java.util.List<Invitee> invitees,
            final java.util.List<com.google.android.gms.maps.model.LatLng> targets,
            final int proximityThreshold) {
        if (invitees.size() == 0) {
            return null;
        }
        if (targets.size() == 0) {
            return null;
        }
        if (proximityThreshold <= 0) {
            return null;
        }

        JsonObject output = new JsonObject();
        output.addProperty("mode", "target");
        output.addProperty("proximityThreshold", proximityThreshold);


        JsonArray members = new JsonArray();
        for (Invitee member : invitees) {
            JsonObject input = new JsonObject();
            input.addProperty("email", member.getEmail());
            input.addProperty("team", member.getTeamId());
            members.add(input);
        }
        output.add("invitees", members);

        JsonArray targs = new JsonArray();
        for (LatLng target : targets) {
            JsonObject input = new JsonObject();
            input.addProperty("latitude", target.latitude);
            input.addProperty("longitude", target.longitude);
            targs.add(input);
        }
        output.add("targets", targs);

        return output;
    }
}
