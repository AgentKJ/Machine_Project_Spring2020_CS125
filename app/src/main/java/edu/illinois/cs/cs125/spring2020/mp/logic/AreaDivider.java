package edu.illinois.cs.cs125.spring2020.mp.logic;

import com.google.android.gms.maps.model.LatLng;
import static edu.illinois.cs.cs125.spring2020.mp.logic.LatLngUtils.distance;

/**
 * Constructor of AreaDivider.
 */
public class AreaDivider {
    /**
     * latitude of the north boundary.
     */
    private double north;
    /**
     * latitude of the south boundary.
     */
    private double south;
    /**
     * longitude of the east boundary.
     */
    private double east;
    /**
     * longitude of the west boundary.
     */
    private double west;
    /**
     * the requested side length of each cell, in meters.
     */
    private int size;


    /**
     * Creates an AreaDivider for an area.
     * Note the order of parameters carefully. A mismatch in interpretation of the arguments between
     * the constructor and its callers will result in unreasonable dimensions. The isValid function
     * can detect some such problems.
     * @param setNorth latitude of the north boundary
     * @param setEast longitude of the east boundary
     * @param setSouth latitude of the south boundary
     * @param setWest longitude of the east boundary
     * @param setCellSize the requested side length of each cell, in meters
     */
    public AreaDivider(final double setNorth, final double setEast, final double setSouth,
                       final double setWest, final int setCellSize) {
        north = setNorth;
        south = setSouth;
        east = setEast;
        west = setWest;
        size = setCellSize;
    }

    /**
     * Gets the boundaries of the specified cell as a Google Maps LatLngBounds object.
     * Note that the LatLngBounds constructor takes the southwest and northeast points of the
     * rectangular region as LatLng objects.
     * @param x the cell's X coordinate
     * @param y the cell's Y coordinate
     * @return the boundaries of the cell
     */
    public com.google.android.gms.maps.model.LatLngBounds getCellBounds(final int x, final int y) {
        if (x + y == 0) {
            return null;
        }
        return null;
    }

    /**
     * Gets the number of cells between the west and east boundaries.
     * @return the number of cells in the X direction
     */
    public int getXCells() {
        int xCells;
        double lngDist = distance(south, east, south, west);
        if (lngDist == 0) {
            xCells = 0;
        } else if (lngDist < size) {
            xCells = 1;
        } else {
            xCells = (int) Math.ceil(lngDist / size);
        }
        return xCells;
    }

    /**
     * Gets the number of cells between the south and north boundaries.
     * @return the number of cells in the Y direction
     */
    public int getYCells() {
        int yCells;
        double latDist = distance(north, west, south, west);
        if (latDist == 0) {
            yCells = 0;
        } else if (latDist < size) {
            yCells = 1;
        } else {
            yCells = (int) Math.ceil(latDist / size);
        }
        return yCells;
    }

    /**
     * Gets the X coordinate of the cell containing the specified location.
     * The point is not necessarily within the area. If it is not, the return value must not appear
     * to be a valid cell index. For example, returning 0 for a point even slightly west of the west
     * boundary is not allowed.
     * @param location the location
     * @return the X coordinate of the cell containing the lat-long point
     */
    public int getXIndex(final LatLng location) {
        int xIndex = 0;
        double length = (east - west) / this.getXCells();
        for (double i = west; i < east; i += length) {
            if (location.longitude > i && location.longitude < i + length) {
                return xIndex;
            }
            xIndex++;
        }
        return xIndex;
    }

    /**
     * Gets the Y coordinate of the cell containing the specified location.
     * The point is not necessarily within the area. If it is not, the return value must not appear
     * to be a valid cell index. For example, returning 0 for a point even slightly south of the south
     * boundary is not allowed.
     * @param location the location
     * @return the Y coordinate of the cell containing the lat-long point
     */
    public int getYIndex(final LatLng location) {
        int yIndex = 0;
        double length = (north - south) / this.getYCells();
        for (double i = south; i < north; i += length) {
            if (location.latitude > i && location.latitude < i + length) {
                return yIndex;
            }
            yIndex++;
        }
        return yIndex;
    }

    /**
     * Returns whether the configuration provided to the constructor is valid.
     * The configuration is valid if the cell size is positive the bounds delimit a region of positive area.
     * That is, the east boundary must be strictly further east than the west boundary and the north boundary
     * must be strictly further north than the south boundary.
     * Due to floating-point strangeness, you may find our LatLngUtils.same function helpful if
     * equality comparison of double variables does not work as expected.
     * @return whether this AreaDivider can divide a valid area
     */
    public boolean isValid() {
        return north > south && east > west && size > 0;
    }

    /**
     * Draws the grid to a map using solid black polylines.
     * There should be one line on each of the four boundaries of the overall area and as many internal
     * lines as necessary to divide the rows and columns of the grid. Each line should span the whole
     * width or height of the area rather than the side of just one cell. For example, an area divided
     * into a 2x3 grid would be drawn with 7 lines total: 4 for the outer boundaries, 1 vertical line
     * to divide the west half from the east half (2 columns), and 2 horizontal lines to divide the area
     * into 3 rows.
     *
     * See the provided addLine function from GameActivity for how to add a line to the map.
     * Since these lines should be black, they should not be paired with any extra "border" lines.
     *
     * If equality comparisons of double variables do not work as expected, consider taking advantage
     * of our LatLngUtils.same function.
     * @param map the Google map to draw on
     */
    public void renderGrid(final com.google.android.gms.maps.GoogleMap map) {

    }
}
