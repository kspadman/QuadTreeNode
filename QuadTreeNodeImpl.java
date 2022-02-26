// CIS 121, QuadTree

public class QuadTreeNodeImpl implements QuadTreeNode {
    /**
     * ! Do not delete this method !
     * Please implement your logic inside this method without modifying the signature
     * of this method, or else your code won't compile.
     * <p/>
     * As always, if you want to create another method, make sure it is not public.
     *
     * @param image image to put into the tree
     * @return the newly build QuadTreeNode instance which stores the compressed image
     * @throws IllegalArgumentException if image is null
     * @throws IllegalArgumentException if image is empty
     * @throws IllegalArgumentException if image.length is not a power of 2
     * @throws IllegalArgumentException if image, the 2d-array, is not a perfect square
     */

    int color;
    int size;
    QuadTreeNodeImpl topLeft;
    QuadTreeNodeImpl topRight;
    QuadTreeNodeImpl bottomLeft;
    QuadTreeNodeImpl bottomRight;

    public static QuadTreeNodeImpl buildFromIntArray(int[][] image) {
        if (image == null) {
            throw new IllegalArgumentException();
        }
        int size = image.length;
        if (size == 0) {
            throw new IllegalArgumentException();
        }
        if (!isPowerOfTwo(size)) {
            throw new IllegalArgumentException();
        }
        if (size != image[0].length) {
            throw new IllegalArgumentException();
        }
        if (isRagged(image)) {
            throw new IllegalArgumentException();
        }

        return helper(image, size, 0, 0);
    }

    static QuadTreeNodeImpl helper(int[][] image, int size, int x, int y) {
        if (size == 1) {
            return new QuadTreeNodeImpl(image[x][y], size);
        }

        QuadTreeNodeImpl topLeft = helper(image, size / 2, x, y);
        QuadTreeNodeImpl topRight = helper(image, size / 2, x, y + size / 2);
        QuadTreeNodeImpl bottomLeft = helper(image, size / 2, x + size / 2, y);
        QuadTreeNodeImpl bottomRight = helper(image, size / 2, x + size / 2, y + size / 2);

        if (allSameColor(image, x, y, size) && topLeft.isLeaf() && topRight.isLeaf() &&
                bottomLeft.isLeaf() && bottomRight.isLeaf()) {
            return new QuadTreeNodeImpl(image[x][y], size);
        } else {
            return new QuadTreeNodeImpl(topLeft, topRight, bottomLeft,
                    bottomRight, image[x][y], size);
        }
    }

    private QuadTreeNodeImpl(QuadTreeNodeImpl topL, QuadTreeNodeImpl topR,
                            QuadTreeNodeImpl bottomL, QuadTreeNodeImpl bottomR,
                             int col, int numCells) {
        topLeft = topL;
        topRight = topR;
        bottomLeft = bottomL;
        bottomRight = bottomR;
        size = numCells;
        color = col;
    }

    private QuadTreeNodeImpl(int col, int numCells) {
        color = col;
        size = numCells;
        topLeft = null;
        topRight = null;
        bottomLeft = null;
        bottomRight = null;
    }

    @Override
    public int getColor(int x, int y) {
        if (outOfBounds(x, y, size)) {
            throw new IllegalArgumentException();
        }

        return colorHelp(this, x, y);
    }

    int colorHelp(QuadTreeNodeImpl quadrant, int x, int y) {
        while (!quadrant.isLeaf()) {
            int quadrantSize = quadrant.getDimension();
            if (x < quadrantSize / 2) {
                if (y < quadrantSize / 2) {
                    return colorHelp(quadrant.topLeft, x, y);
                } else {
                    return colorHelp(quadrant.bottomLeft, x, y - quadrantSize / 2);
                }
            } else {
                if (y < quadrantSize / 2) {
                    return colorHelp(quadrant.topRight, x - quadrantSize / 2, y);
                } else {
                    return colorHelp(quadrant.bottomRight, x - quadrantSize / 2,
                            y - quadrantSize / 2);
                }
            }
        }
        return quadrant.color;
    }
    @Override
    public void setColor(int x, int y, int c) {
        if (outOfBounds(x, y, size)) {
            throw new IllegalArgumentException();
        }

        helpSetColor(this, x, y, c);
    }

    void helpSetColor(QuadTreeNodeImpl quadrant, int x, int y, int c) {
        int quadrantSize = quadrant.getDimension();
        if (quadrantSize == 1) {
            quadrant.color = c;
            return;
        }
        if (quadrant.isLeaf()) {
            int quadColor = quadrant.color;
            QuadTreeNodeImpl newTL = new QuadTreeNodeImpl(null, null, null, null,
                    quadColor, quadrantSize / 2);
            QuadTreeNodeImpl newTR = new QuadTreeNodeImpl(null, null, null, null,
                    quadColor, quadrantSize / 2);
            QuadTreeNodeImpl newBL = new QuadTreeNodeImpl(null, null, null, null,
                    quadColor, quadrantSize / 2);
            QuadTreeNodeImpl newBR = new QuadTreeNodeImpl(null, null, null, null,
                    quadColor, quadrantSize / 2);
            quadrant.topLeft = newTL;
            quadrant.topRight = newTR;
            quadrant.bottomLeft = newBL;
            quadrant.bottomRight = newBR;
        }

        if (x < quadrantSize / 2) {
            if (y < quadrantSize / 2) {
                helpSetColor(quadrant.topLeft, x, y, c);
            } else {
                helpSetColor(quadrant.bottomLeft, x, y - quadrantSize / 2, c);
            }
        } else {
            if (y < quadrantSize / 2) {
                helpSetColor(quadrant.topRight, x - quadrantSize / 2, y, c);
            } else {
                helpSetColor(quadrant.bottomRight, x - quadrantSize / 2, y - quadrantSize / 2, c);
            }
        }

        if (needsRecompress(quadrant.topLeft, quadrant.topRight,
                quadrant.bottomLeft, quadrant.bottomRight)) {

            int childColor = quadrant.topLeft.color;
            quadrant.color = childColor;
            quadrant.topLeft = null;
            quadrant.topRight = null;
            quadrant.bottomLeft = null;
            quadrant.bottomRight = null;
            return;
        }
    }

    boolean needsRecompress(QuadTreeNodeImpl topLeft, QuadTreeNodeImpl topRight,
                            QuadTreeNodeImpl bottomLeft, QuadTreeNodeImpl bottomRight) {
        boolean recompress = false;
        if (topLeft.isLeaf() && topRight.isLeaf() && bottomLeft.isLeaf() && bottomRight.isLeaf()) {
            if (topLeft.color == topRight.color && topRight.color == bottomLeft.color
                && bottomLeft.color == bottomRight.color) {
                recompress = true;
            }
        }

        return recompress;
    }

    @Override
    public QuadTreeNode getQuadrant(QuadName quadrant) {
        if (isLeaf()) {
            return null;
        }

        if (quadrant.equals(QuadTreeNode.QuadName.TOP_LEFT)) {
            return topLeft;
        } else if (quadrant.equals(QuadTreeNode.QuadName.TOP_RIGHT)) {
            return topRight;
        } else if (quadrant.equals(QuadTreeNode.QuadName.BOTTOM_LEFT)) {
            return bottomLeft;
        } else  {
            return bottomRight;
        }
    }

    @Override
    public int getDimension() {
        return this.size;
    }

    @Override
    public int getSize() {
        if (this.isLeaf()) {
            return 1;
        }
        return 1 + topLeft.getSize() + topRight.getSize() +
                bottomLeft.getSize() + bottomRight.getSize();
    }

    @Override
    public boolean isLeaf() {
        return (topLeft == null && topRight == null &&
                bottomLeft == null && bottomRight == null);
    }

    @Override
    public int[][] decompress() {
        int[][] decompressed = new int[size][size];
        return restoreOriginal(this, decompressed, 0, 0);
    }

    int[][] restoreOriginal(QuadTreeNodeImpl quadrant, int[][] decompressed, int x, int y) {
        int quadrantSize = quadrant.getDimension();
        if (!quadrant.isLeaf()) {
            restoreOriginal(quadrant.topLeft, decompressed, x, y);
            restoreOriginal(quadrant.topRight, decompressed, x, y + quadrantSize / 2);
            restoreOriginal(quadrant.bottomLeft, decompressed, x + quadrantSize / 2, y);
            restoreOriginal(quadrant.bottomRight, decompressed,
                    x + quadrantSize / 2, y + quadrantSize / 2);
        } else {
            for (int i = x; i < x + quadrantSize; i++) {
                for (int j = y; j < y + quadrantSize; j++) {
                    decompressed[i][j] = quadrant.color;
                }
            }
        }
        return decompressed;
    }

    @Override
    public double getCompressionRatio() {
        double numPixels = getDimension() * getDimension();
        return getSize() / numPixels;
    }

    static boolean isPowerOfTwo(int x) {
        double a = Math.log(x) / Math.log(2);
        return (Math.ceil(a) == Math.floor(a));
    }

    static boolean allSameColor(int[][] arr, int x, int y, int numCells) {
        boolean same = true;
        int[] leadingPixels = new int[4];
        leadingPixels[0] = arr[x][y];
        leadingPixels[1] = arr[x + numCells / 2][y];
        leadingPixels[2] = arr[x][y + numCells / 2];
        leadingPixels[3] = arr[x + numCells / 2][y + numCells / 2];
        for (int i = 1; i < leadingPixels.length; i++) {
            if (leadingPixels[i] != leadingPixels[0]) {
                same = false;
                break;
            }
        }
        return same;
    }

    static boolean outOfBounds(int x, int y, int size) {
        boolean outOfBounds = x < 0 || y < 0;
        if (x >= size || y >= size) {
            outOfBounds = true;
        }

        return outOfBounds;
    }

    static boolean isRagged(int[][] image) {
        boolean ragged = false;
        for (int[] row : image) {
            if (row.length != image[0].length) {
                ragged = true;
                break;
            }
        }
        return ragged;
    }
}
