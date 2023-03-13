/**
 * A 2D cartesian plane implemented as with an array. Each (x,y) coordinate can
 * hold a single item of type <T>.
 *
 * @param <T> The type of element held in the data structure
 */
public class ArrayCartesianPlane<T> implements CartesianPlane<T> {
    private int minimumX;
    private int minimumY;
    private int maximumX;
    private int maximumY;
    private int width;
    private int height;
    private T[][] plane;

    /**
     * Constructs a new ArrayCartesianPlane object with given minimum and
     * maximum bounds.
     *
     * Note that these bounds are allowed to be negative.
     *
     * @param minimumX A new minimum bound for the x values of
     *         elements.
     * @param maximumX A new maximum bound for the x values of
     *         elements.
     * @param minimumY A new minimum bound for the y values of
     *         elements.
     * @param maximumY A new maximum bound for the y values of
     *         elements.
     * @throws IllegalArgumentException if the x minimum is greater
     *         than the x maximum (and resp. with y min/max)
     */
    public ArrayCartesianPlane(int minimumX, int maximumX, int minimumY,
            int maximumY) throws IllegalArgumentException {
        // TODO: implement the constructor
        if (minimumX > maximumX || minimumY > maximumY) {
            throw new IllegalArgumentException();
        }
        this.minimumX = minimumX;
        this.maximumX = maximumX;
        this.minimumY = minimumY;
        this.maximumY = maximumY;
        this.width = maximumX - minimumX + 1;
        this.height = maximumY - minimumY + 1;
        this.plane = (T[][])new Object[width][height];
    }

    @Override
    public void add(int x, int y, T element) throws IllegalArgumentException {
        if (x < minimumX || x > maximumX) {
            throw new IllegalArgumentException("x-coordinate is out of bounds");
        } else if (y < minimumY || y > maximumY) {
            throw new IllegalArgumentException("y-coordinate is out of bounds");
        }
        plane[x - minimumX][y - minimumY] = element;
    }

    @Override
    public T get(int x, int y) throws IndexOutOfBoundsException {
        // no need to do a check for throwing the exception, array indexing will do this for us
        return plane[x - minimumX][y - minimumY];
    }

    @Override
    public boolean remove(int x, int y) throws IndexOutOfBoundsException {
        if (plane[x - minimumX][y - minimumY] == null) {
            return false;
        } else {
            plane[x - minimumX][x - minimumX] = null;
            return true;
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                plane[i][j] = null;
            }
        }
    }

    @Override
    public void resize(int newMinimumX, int newMaximumX, int newMinimumY,
                       int newMaximumY) throws IllegalArgumentException {
        if (newMinimumX > newMaximumX || newMinimumY > newMaximumY) {
            throw new IllegalArgumentException();
        }
        int newWidth = newMaximumX - newMinimumX + 1;
        int newHeight = newMaximumY - newMinimumY + 1;
        T[][] newPlane = (T[][])new Object[newWidth][newHeight];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (plane[i][j] != null) {
                    if ((i + minimumX) < newMinimumX || (i + minimumX) > newMaximumX
                            || (j + minimumY) < newMinimumY || (j + minimumY) > newMaximumY) {
                        throw new IllegalArgumentException();
                    } else {
                        newPlane[i + minimumX - newMinimumX][j + minimumY - newMinimumY]
                                = plane[i][j];
                    }
                }
            }
        }
        this.minimumX = newMinimumX;
        this.maximumX = newMaximumX;
        this.minimumY = newMinimumY;
        this.maximumY = newMaximumY;
        this.width = newWidth;
        this.height = newHeight;
        this.plane = newPlane;
    }
}

