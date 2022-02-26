/**
 * CIS 121, QuadTree Compression
 * <p>
 * Interface definition of a {@link QuadTreeNode}.
 * <p/>
 * A {@link QuadTreeNode} represents a node in the QuadTree.
 * It represents a square region of color with sides being length of a
 * power of two. Each {@link QuadTreeNode} contains either four child
 * nodes or a single color represented by an {@code int} when it is a leaf.
 * <p/>
 * Note that a single node is a tree rooted at itself.
 *
 * @author cquanze (old) / hanbangw, 20sp
 */
public interface QuadTreeNode {
    /**
     * Gets the color at coordinate {@code (x, y)}. Find the leaf node that
     * corresponds to this coordinate and get the color of that node.
     * <p/>
     * The coordinate {@code (x, y)} is relative to the node this method is called on.
     * For example, {@code getColor(0, 0)} should give the color located at upper-left most
     * pixel of whatever node it is called on.
     *
     * @param x the {@code x}-coordinate
     * @param y the {@code y}-coordinate
     * @return the color that is on (x, y)
     * @throws IllegalArgumentException if {@code x} or {@code y} is out of bounds
     */
    int getColor(int x, int y);

    /**
     * Sets the color at coordinates {@code (x, y)}. That is, find the 1x1 leaf node that
     * corresponds to this coordinate (you may need to break up leaf nodes) and set the 
     * color of that node to the specific color. 
     * <p/>
     * The coordinate {@code (x, y)} is relative to the node this method is called on.
     * For example, {@code setColor(0, 0)} should set the color located at upper-left most
     * pixel of whatever node it is called on.
     * You should NOT worry about merging of colors for any ancestor node if {@code setColor()}
     * is called on a non-root node of a valid QuadTree.
     * <p/>
     * Colors of all integers are allowed.
     *
     * @param x     the {@code x}-coordinate
     * @param y     the {@code y}-coordinate
     * @param color the color (x, y) should be set to
     * @throws IllegalArgumentException if {@code x} or {@code y} is out of bounds
     */
    void setColor(int x, int y, int color);

    /**
     * Returns the {@link QuadTreeNode} in the specified quadrant.
     * If this QuadTreeNode is a leaf, then this method returns {@code null}
     * for all quadrants. Otherwise this method returns a non-{@code null} value.
     *
     * @param quadrant the quadrant to retrieve
     * @return quadrant instance or {@code null}
     */
    QuadTreeNode getQuadrant(QuadName quadrant);

    /**
     * Returns the dimensions of the area this QuadTreeNode represents.
     * <p/>
     * For example, a QuadTreeNode representing a 4x4 area will return {@code 4},
     * and subsequently its direct children (if they exist) will return {@code 2}.
     * This value will always be a non-negative integer power of 2.
     *
     * @return the size of the square's side length represented by this QuadTreeNode.
     */
    int getDimension();

    /**
     * Returns the number of descendants this current node contains (including itself).
     * <p/>
     * For example, a leaf has size {@code 1}, and a QuadTreeNode with 4 children
     * and no grandchildren has size {@code 5}.
     *
     * @return the number of descendants contained by this QuadTreeNode
     */
    int getSize();

    /**
     * Returns {@code true} if this {@link QuadTreeNode} is a leaf.
     *
     * @return {@code true} if the QuadTreeNode is a leaf
     */
    boolean isLeaf();

    /**
     * Decompresses the QuadTree into a 2d-array. The returned array contains integers that
     * represent the color at each coordinate. The returned 2D array satisfies {@code
     * result[y][x] == getColor(x, y)} for each coordinate {@code (x, y)}.
     *
     * @return a newly initialized array storing the decompressed image data
     */
    int[][] decompress();

    /**
     * Gets the compression ratio of the current QuadTree.
     * The compression ratio is defined as the number of {@link QuadTreeNode}s
     * contained in the tree, divided by the number of pixels in the image.
     *
     * @return the compression ratio
     */
    double getCompressionRatio();

    /**
     * Enumeration for representing the location of a quadrant.
     */
    enum QuadName {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }
}
