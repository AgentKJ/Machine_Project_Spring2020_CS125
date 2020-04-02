package edu.illinois.cs.cs125.spring2020.mp.logic;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neovisionaries.ws.client.WebSocket;

import edu.illinois.cs.cs125.spring2020.mp.R;

/**
 * Represents an area mode game. Keeps track of cells and the player's most recent capture.
 * <p>
 * All these functions are stubs that you need to implement.
 * Feel free to add any private helper functions that would be useful.
 * See {@link TargetGame} for an example of how multiplayer games are handled.
 */
public final class AreaGame extends Game {

    // You will probably want some instance variables to keep track of the game state
    // (similar to the area mode gameplay logic you previously wrote in GameActivity)
    /** The northern bound. */
    private Double areaNorth;

    /** The eastern bound. */
    private Double areaEast;

    /** The southern bound. */
    private Double areaSouth;

    /** The western bound. */
    private Double areaWest;

    /** The side length of each cell. */
    private int cellSize;

    /** An instance of AreaDivider. */
    private AreaDivider manager;

    /** The JsonObject of this player. */
    private JsonObject thisPlayer;

    /** The team's color. */
    private int[] colors = getContext().getResources().getIntArray(R.array.team_colors);

    /** For tracking which cells have been visited. */
    private boolean[][] allPlayerVisited;

    /** For tracking which cells have been visited by this player. */
    private boolean[][] thisPlayerVisited;

    /** The x index of the last cell visited by this player. */
    private int lastVisitedXIndex;

    /** The y index of the last cell visited by this player. */
    private int lastVisitedYIndex;

    /** Score of each team. */
    private int[] score = new int[TeamID.MAX_TEAM];

    /**
     * Creates a game in area mode.
     * <p>
     * Loads the current game state from JSON into instance variables and populates the map
     * to show existing cell captures.
     * @param email the user's email
     * @param map the Google Maps control to render to
     * @param webSocket the websocket to send updates to
     * @param fullState the "full" update from the server
     * @param context the Android UI context
     */
    public AreaGame(final String email, final GoogleMap map, final WebSocket webSocket,
                    final JsonObject fullState, final Context context) {
        super(email, map, webSocket, fullState, context);
        areaNorth = fullState.get("areaNorth").getAsDouble();
        areaEast = fullState.get("areaEast").getAsDouble();
        areaSouth = fullState.get("areaSouth").getAsDouble();
        areaWest = fullState.get("areaWest").getAsDouble();
        cellSize = fullState.get("cellSize").getAsInt();
        manager = new AreaDivider(areaNorth, areaEast, areaSouth, areaWest, cellSize);

        manager.renderGrid(map);
        allPlayerVisited = new boolean[manager.getXCells()][manager.getYCells()];
        thisPlayerVisited = new boolean[manager.getXCells()][manager.getYCells()];

        // Initializing the tracking arrays.
        for (int i = 0; i < allPlayerVisited.length; i++) {
            for (int j = 0; j < allPlayerVisited[i].length; j++) {
                allPlayerVisited[i][j] = false;
                thisPlayerVisited[i][j] = false;
            }
        }

        JsonArray players = fullState.get("players").getAsJsonArray();
        for (JsonElement j : players) {
            JsonObject jAsJO = (JsonObject) j;
            // Track the cells visited by this player.
            if (jAsJO.get("email").getAsString().equals(getEmail())) {
                thisPlayer = jAsJO;
                JsonArray path = thisPlayer.get("path").getAsJsonArray();
                int team = thisPlayer.get("team").getAsInt();
                for (JsonElement a : path) {
                    JsonObject aAsJO = (JsonObject) a;
                    int x = aAsJO.get("x").getAsInt();
                    int y = aAsJO.get("y").getAsInt();
                    lastVisitedXIndex = x;
                    lastVisitedYIndex = y;
                    thisPlayerVisited[x][y] = true;
                    allPlayerVisited[x][y] = true;
                    addNewPolygon(x, y, team, map);
                    score[team - 1]++;
                }
            } else {
                // Track the cells visited by other players.
                JsonArray path = jAsJO.get("path").getAsJsonArray();
                int team = jAsJO.get("team").getAsInt();
                for (JsonElement a : path) {
                    JsonObject aAsJO = (JsonObject) a;
                    int x = aAsJO.get("x").getAsInt();
                    int y = aAsJO.get("y").getAsInt();
                    allPlayerVisited[x][y] = true;
                    addNewPolygon(x, y, team, map);
                    score[team - 1]++;
                }
            }
        }
    }


    /**
     * Called when the user's location changes.
     * <p>
     * Area mode games detect whether the player is in an uncaptured cell. Capture is possible if
     * the player has no captures yet or if the cell shares a side with the previous cell captured by
     * the player. If capture occurs, a polygon with the team color is added to the cell on the map
     * and a cellCapture update is sent to the server.
     * @param location the player's most recently known location
     */
    @Override
    public void locationUpdated(final LatLng location) {
        super.locationUpdated(location);

        Double latitude = location.latitude;
        Double longitude = location.longitude;
        int xIndex = manager.getXIndex(location);
        int yIndex = manager.getYIndex(location);

        if (latitude <= areaNorth && latitude >= areaSouth && longitude >= areaWest && longitude <= areaEast) {
            // Check if the cell has been visited.
            if (!allPlayerVisited[xIndex][yIndex]) {
                // Check if it is the first cell for this player to be visit.
                if (checkFirst()) {
                    addNewPolygon(xIndex, yIndex, getMyTeam(), getMap());
                    score[getMyTeam() - 1]++;
                    thisPlayerVisited[xIndex][yIndex] = true;
                    allPlayerVisited[xIndex][yIndex] = true;
                    lastVisitedXIndex = xIndex;
                    lastVisitedYIndex = yIndex;
                    // Send update to the server
                    JsonObject message = new JsonObject();
                    message.addProperty("type", "cellCapture");
                    message.addProperty("x", xIndex);
                    message.addProperty("y", yIndex);
                    sendMessage(message);
                } else if ((checkNeighbor(xIndex, yIndex))) {
                    addNewPolygon(xIndex, yIndex, getMyTeam(), getMap());
                    score[getMyTeam() - 1]++;
                    thisPlayerVisited[xIndex][yIndex] = true;
                    allPlayerVisited[xIndex][yIndex] = true;
                    lastVisitedXIndex = xIndex;
                    lastVisitedYIndex = yIndex;
                    JsonObject message = new JsonObject();
                    message.addProperty("type", "cellCapture");
                    message.addProperty("x", xIndex);
                    message.addProperty("y", yIndex);
                    sendMessage(message);
                }
            }
        }

    }

    /**
     * Add a new polygon on the cell.
     * @param xIndex The x index of the cell.
     * @param yIndex The y index of the cell.
     * @param team The team capturing the cell.
     * @param map The playing map.
     */
    public void addNewPolygon(final int xIndex, final int yIndex, final int team, final GoogleMap map) {

        double xLength = (areaEast - areaWest) / manager.getXCells();
        double yLength = (areaNorth - areaSouth) / manager.getYCells();
        double cellWest = areaWest + xLength * xIndex;
        double cellEast = areaWest + xLength * (xIndex + 1);
        double cellSouth = areaSouth + yLength * yIndex;
        double cellNorth = areaSouth + yLength * (yIndex + 1);

        PolygonOptions fill = new PolygonOptions().add(new LatLng(cellSouth, cellWest),
                new LatLng(cellNorth, cellWest),
                new LatLng(cellNorth, cellEast),
                new LatLng(cellSouth, cellEast),
                new LatLng(cellSouth, cellWest)).fillColor(colors[team]);
        map.addPolygon(fill);
    }

    /**
     * Check if a cell is the first to visit.
     * @return Whether a cell is the first to visit.
     */
    private boolean checkFirst() {
        for (boolean[] b : thisPlayerVisited) {
            for (boolean c : b) {
                if (c) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Check if the cell is next to the last visit cell.
     * @param x The x index of the cell.
     * @param y The y index of the cell.
     * @return Whether the cell is next to the last visit cell.
     */
    private boolean checkNeighbor(final int x, final int y) {
        if (lastVisitedXIndex == x) {
            if (lastVisitedYIndex == y + 1 || lastVisitedYIndex == y - 1) {
                return true;
            }
        }
        if (lastVisitedYIndex == y) {
            if (lastVisitedXIndex == x + 1 || lastVisitedXIndex == x - 1) {
                return true;
            }
        }

        return false;
    }


    /**
     * Processes an update from the server.
     * <p>
     * Since playerCellCapture events are specific to area mode games, this function handles those
     * by placing a polygon of the capturing player's team color on the newly captured cell and
     * recording the cell's new owning team.
     * All other message types are delegated to the superclass.
     * @param message JSON from the server (the "type" property indicates the update type)
     * @return whether the message type was recognized
     */
    @Override
    public boolean handleMessage(final JsonObject message) {
        if (message.get("type").getAsString().equals("playerCellCapture")) {
            int team = message.get("team").getAsInt();
            int x = message.get("x").getAsInt();
            int y = message.get("y").getAsInt();

            if (message.get("email").getAsString().equals(getEmail())) {
                if (!allPlayerVisited[x][y]) {
                    if (checkFirst()) {
                        addNewPolygon(x, y, getMyTeam(), getMap());
                        score[getMyTeam() - 1]++;
                        thisPlayerVisited[x][y] = true;
                        allPlayerVisited[x][y] = true;
                        lastVisitedXIndex = x;
                        lastVisitedYIndex = y;
                        // Send update to the server
                        JsonObject newMessage = new JsonObject();
                        newMessage.addProperty("type", "cellCapture");
                        newMessage.addProperty("x", x);
                        newMessage.addProperty("y", y);
                        sendMessage(newMessage);
                    } else if ((checkNeighbor(x, y))) {
                        addNewPolygon(x, y, getMyTeam(), getMap());
                        score[getMyTeam() - 1]++;
                        thisPlayerVisited[x][y] = true;
                        allPlayerVisited[x][y] = true;
                        lastVisitedXIndex = x;
                        lastVisitedYIndex = y;
                        JsonObject newMessage = new JsonObject();
                        newMessage.addProperty("type", "cellCapture");
                        newMessage.addProperty("x", x);
                        newMessage.addProperty("y", y);
                        sendMessage(newMessage);
                    }
                }
            } else {
                if (!allPlayerVisited[x][y]) {
                    addNewPolygon(x, y, team, getMap());
                    score[team - 1]++;
                    allPlayerVisited[x][y] = true;
                }
            }
            return true;
        } else {
            return super.handleMessage(message);
        }
    }

    /**
     * Gets a team's score in this area mode game.
     * @param teamId the team ID
     * @return the number of cells owned by the team
     */
    @Override
    public int getTeamScore(final int teamId) {
        return score[teamId - 1];
    }

    /**
     * For getWinningTeam function in Game class.
     * @return The winning team.
     */
    public int gWT() {
        int result = score[0];
        for (int i = 0; i < score.length; i++) {
            if (score[i] > result) {
                result = score[i];
            }
        }
        for (int i = 0; i < score.length; i++) {
            if (score[i] == result) {
                return i + 1;
            }
        }
        return 0;
    }
}
