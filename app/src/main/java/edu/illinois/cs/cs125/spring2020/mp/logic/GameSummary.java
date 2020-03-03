package edu.illinois.cs.cs125.spring2020.mp.logic;

import android.content.Context;

/**
 * GameSummary.class.
 */
public class GameSummary {
    /**
     * Id.
     */
    private String id;
    /**
     * Mode.
     */
    private String mode;
    /**
     * Owner.
     */
    private String owner;
    /**
     * PlayerRole.
     */
    private String playerRole;

    /**
     * Creates a game summary from JSON from the server.
     * @param infoFromServer one object from the array in the /games response
     */
    public GameSummary(final com.google.gson.JsonObject infoFromServer) {
        id = infoFromServer.get("ID").getAsString();
        mode = infoFromServer.get("mode").getAsString();
        owner = infoFromServer.get("owner").getAsString();
        playerRole = infoFromServer.get("player_role").getAsString();
    }

    /**
     * Gets the unique, server-assigned ID of this game.
     * @return The game ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the mode of this game, either area or target.
     * @return the game mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * Gets the owner/creator of this game.
     * @return the email of the game's owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Gets the name of the user's team/role.
     * @param userEmail the logged-in user's email
     * @param context an Android context (for access to resources)
     * @return the human-readable team/role name of the user in this game
     */
    public String getPlayerRole(final String userEmail, final Context context) {
        return playerRole;
    }

    /**
     * Determines whether this game is an invitation to the user.
     * @param userEmail the logged-in user's email
     * @return whether the user is invited to this game
     */
    public boolean isInvitation(final String userEmail) {
        return false;
    }

    /**
     * Determines whether the user is currently involved in this game.
     * For a game to be ongoing, it must not be over and the user must have accepted their invitation to it.
     * @param userEmail the logged-in user's email
     * @return whether this game is ongoing for the user
     */
    public boolean isOngoing(final String userEmail) {
        return false;
    }

}
