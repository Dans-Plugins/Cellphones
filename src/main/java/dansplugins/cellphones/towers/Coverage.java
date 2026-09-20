package dansplugins.cellphones.towers;

/**
 * Pure geometry for deciding whether a position is inside a tower's coverage.
 *
 * <p>Kept free of Bukkit types so it can be unit-tested without a server.
 */
public final class Coverage {

    private Coverage() {
    }

    /**
     * Returns whether a point is within {@code radius} blocks of a tower.
     *
     * @param ignoreY when true, the vertical distance is ignored (a tower covers the ground below and the sky above it)
     */
    public static boolean covers(double towerX, double towerY, double towerZ,
                                 double x, double y, double z,
                                 double radius, boolean ignoreY) {
        if (radius < 0) {
            return false;
        }
        double dx = towerX - x;
        double dz = towerZ - z;
        double dy = ignoreY ? 0 : towerY - y;
        return dx * dx + dy * dy + dz * dz <= radius * radius;
    }
}
