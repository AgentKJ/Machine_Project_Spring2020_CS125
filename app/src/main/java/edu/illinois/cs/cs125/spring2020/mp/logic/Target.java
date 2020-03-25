package edu.illinois.cs.cs125.spring2020.mp.logic;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Target class.
 */
public class Target {

    /**
     * Target's position.
     */
    private LatLng position;

    /**
     * Target's team.
     */
    private int team;

    /**
     * An instance of BitmapDescriptor.
     */
    private BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);

    /**
     * An instance of MarkerOptions.
     */
    private MarkerOptions options;

    /**
     * An instance of Marker.
     */
    private Marker marker;


    /**
     * Creates a target in a target-mode game by placing an appropriately colored marker on the map.
     * The marker's hue should reflect the team (if any) currently owning the target.
     * See the class description for the hue values to use.
     * @param setMap the map to render to
     * @param setPosition the position of the target
     * @param setTeam the TeamID code of the team currently owning the target
     */
    public Target(final com.google.android.gms.maps.GoogleMap setMap,
                  final com.google.android.gms.maps.model.LatLng setPosition, final int setTeam) {

        position = setPosition;
        team = setTeam;

        options = new MarkerOptions().position(setPosition);
        marker = setMap.addMarker(options);

        if (setTeam == TeamID.TEAM_RED) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        } else if (setTeam == TeamID.TEAM_YELLOW) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        } else if (setTeam == TeamID.TEAM_GREEN) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        } else if (setTeam == TeamID.TEAM_BLUE) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        }
        marker.setIcon(icon);

    }

    /**
     * Gets the position of the target.
     * @return the coordinates of the target
     */
    public com.google.android.gms.maps.model.LatLng getPosition() {
        return position;
    }

    /**
     * Gets the ID of the team currently owning this target.
     * @return the owning team ID or OBSERVER if unclaimed
     */
    public int getTeam() {
        return team;
    }

    /**
     * Updates the owning team of this target and updates the hue of the marker to match.
     * @param newTeam the ID of the team that captured the target
     */
    public void setTeam(final int newTeam) {

        team = newTeam;

        if (newTeam == TeamID.TEAM_RED) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        } else if (newTeam == TeamID.TEAM_YELLOW) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        } else if (newTeam == TeamID.TEAM_GREEN) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
        } else if (newTeam == TeamID.TEAM_BLUE) {
            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        }
        marker.setIcon(icon);
    }
}
