import org.junit.Test;

import static org.junit.Assert.*;

public class QuadTreeNodeImplTest {

    @Test(expected = IllegalArgumentException.class)
    public void buildFromIntArrayNull() {
        int[][] illegal = null;
        QuadTreeNodeImpl.buildFromIntArray(illegal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildFromIntArrayNotPowerOfTwo() {
        int[][] illegal = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {8, 9, 0, 1},
        };
        QuadTreeNodeImpl.buildFromIntArray(illegal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildFromIntArrayEmpty() {
        int[][] illegal = {

        };
        QuadTreeNodeImpl.buildFromIntArray(illegal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildFromIntArrayNotEqualSize() {
        int[][] illegal = {
                {0, 1, 2},
                {4, 5, 6},
                {8, 9, 0},
                {4, 3, 2}
        };
        QuadTreeNodeImpl.buildFromIntArray(illegal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildFromIntArrayRagged() {
        int[][] illegal = {
                {0, 1, 2, 8},
                {4, 5, 6},
                {8, 9, 0},
                {4, 3, }
        };
        QuadTreeNodeImpl.buildFromIntArray(illegal);
    }

    @Test(timeout =  1000)
    public void buildFromIntArrayOneNodeNoLeaves() {
        int[][] image = {
                {1, 1},
                {1, 1},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertTrue(returned.isLeaf());
        assertEquals(1, returned.color);
    }

    @Test(timeout =  1000)
    public void buildFromIntArrayOneNodeWithChildren() {
        int[][] image = {
                {1, 0},
                {1, 1},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertFalse(returned.isLeaf());
        assertEquals(1, returned.topLeft.color);
        assertTrue(returned.topLeft.isLeaf());
    }

    @Test(timeout =  1000)
    public void buildFromIntArrayBigArray() {
        int[][] image = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {2, 2, 0, 1},
                {2, 2, 4, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertFalse(returned.isLeaf());
        assertFalse(returned.topLeft.isLeaf());
        assertTrue(returned.bottomLeft.isLeaf());
        assertEquals(2, returned.bottomLeft.color);
    }

    @Test(timeout =  1000)
    public void buildFromIntArrayBigArrayAllLeaves() {
        int[][] image = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {2, 1, 0, 1},
                {2, 2, 4, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertFalse(returned.isLeaf());
        assertFalse(returned.topLeft.isLeaf());
        assertFalse(returned.bottomLeft.isLeaf());
        assertEquals(1, returned.bottomLeft.topRight.color);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getColorOutOfBounds() {
        int[][] image = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {2, 1, 0, 1},
                {2, 2, 4, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        returned.getColor(0, 5);
    }

    @Test(timeout = 1000)
    public void getColorSimpleTest() {
        int[][] image = {
                {0, 1, 2, 3},
                {4, 5, 6, 7},
                {2, 1, 0, 1},
                {2, 2, 4, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);

        int color = returned.getColor(0,0);
        assertEquals(0, color);

        color = returned.getColor(3, 1);
        assertEquals(7, color);

        color = returned.getColor(2, 3);
        assertEquals(4, color);
    }

    @Test(timeout = 1000)
    public void getColorLeaf() {
        int[][] image = {
                {0, 1, 6, 6},
                {4, 5, 6, 6},
                {2, 2, 0, 1},
                {2, 2, 4, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);

        int color = returned.getColor(1,3);
        assertEquals(2, color);

        color = returned.getColor(3, 0);
        assertEquals(6, color);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setColorOutOfBounds() {
        int[][] image = {
                {0, 1, 6, 6},
                {4, 5, 6, 6},
                {2, 2, 0, 1},
                {1, 2, 4, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        returned.setColor(3, -1, 9);
    }

    @Test(timeout = 1000)
    public void setColorNoStructureChange() {
        int[][] image = {
                {0, 1, 6, 1},
                {4, 5, 3, 6},
                {2, 2, 0, 1},
                {1, 2, 4, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        returned.setColor(3, 0, 9);

        assertFalse(returned.isLeaf());
        assertFalse(returned.topRight.isLeaf());
        assertEquals(9, returned.topRight.topRight.color);
        assertTrue(returned.topRight.topRight.isLeaf());
    }

    @Test(timeout = 1000)
    public void setColorNodesBrokenDown() {
        int[][] image = {
                {0, 1, 6, 6},
                {4, 5, 6, 6},
                {2, 2, 0, 0},
                {1, 2, 0, 0},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertTrue(returned.topRight.isLeaf());

        returned.setColor(3, 0, 9);

        assertFalse(returned.isLeaf());
        assertFalse(returned.topRight.isLeaf());
        assertEquals(9, returned.topRight.topRight.color);
        assertTrue(returned.topRight.topLeft.isLeaf());

        returned.setColor(2, 3, 5);
        assertFalse(returned.bottomRight.isLeaf());
        assertEquals(5, returned.bottomRight.bottomLeft.color);
    }

    @Test(timeout = 1000)
    public void setColorNodesCombined() {
        int[][] image = {
                {5, 1, 6, 1},
                {5, 5, 3, 6},
                {2, 2, 1, 1},
                {1, 2, 1, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);

        assertFalse(returned.topLeft.isLeaf());
        returned.setColor(1, 0, 5);

        assertTrue(returned.topLeft.isLeaf());
        assertEquals(5, returned.topLeft.color);

        assertFalse(returned.bottomLeft.isLeaf());
        returned.setColor(0, 3, 2);

        assertTrue(returned.bottomLeft.isLeaf());
        assertEquals(2, returned.bottomLeft.color);
    }

    @Test(timeout = 1000)
    public void getQuadrantNull() {
        int[][] image = {
                {5, 1, 6, 1},
                {5, 5, 3, 6},
                {2, 2, 1, 1},
                {2, 2, 1, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        QuadTreeNode quadrant =
                returned.bottomLeft.getQuadrant(QuadTreeNode.QuadName.TOP_LEFT);

        assertNull(quadrant);
    }

    @Test(timeout = 1000)
    public void getQuadrantNotNull() {
        int[][] image = {
                {5, 1, 6, 1},
                {5, 5, 3, 6},
                {2, 2, 1, 1},
                {2, 2, 1, 1},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        QuadTreeNode topLeft = returned.getQuadrant(QuadTreeNode.QuadName.TOP_LEFT);
        QuadTreeNode topRight = returned.getQuadrant(QuadTreeNode.QuadName.TOP_RIGHT);
        QuadTreeNode bottomLeft = returned.getQuadrant(QuadTreeNode.QuadName.BOTTOM_LEFT);
        QuadTreeNode bottomRight = returned.getQuadrant(QuadTreeNode.QuadName.BOTTOM_RIGHT);

        assertFalse(topLeft.isLeaf());
        assertFalse(topRight.isLeaf());
        assertTrue(bottomLeft.isLeaf());
        assertTrue(bottomRight.isLeaf());
    }

    @Test(timeout = 1000)
    public void decompressOneNode() {
        int[][] image = {
                {5, 5},
                {5, 5},

        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertTrue(returned.isLeaf());
        int[][] decompressed = returned.decompress();
        assertArrayEquals(image, decompressed);
    }

    @Test(timeout = 1000)
    public void decompressBigArray() {
        int[][] image = {
                {5, 1, 6, 1},
                {5, 5, 3, 6},
                {2, 2, 1, 1},
                {2, 2, 1, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertFalse(returned.isLeaf());
        int[][] decompressed = returned.decompress();
        assertArrayEquals(image, decompressed);
    }

    @Test(timeout = 1000)
    public void sizeLeaf() {
        int[][] image = {
                {5, 5},
                {5, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertEquals(1, returned.getSize());
    }

    @Test(timeout = 1000)
    public void sizeWithChildren() {
        int[][] image = {
                {5, 1, 6, 1},
                {5, 5, 3, 6},
                {2, 2, 1, 1},
                {2, 2, 1, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertEquals(17, returned.getSize());
    }

    @Test(timeout = 1000)
    public void compressionRatio() {
        int[][] image = {
                {5, 1, 6, 1},
                {5, 5, 3, 6},
                {2, 2, 1, 1},
                {2, 2, 1, 5},
        };

        QuadTreeNodeImpl returned = QuadTreeNodeImpl.buildFromIntArray(image);
        assertEquals(17.0 / 16.0, returned.getCompressionRatio(), 0.0000001);
    }

    @Test(timeout = 1000)
    public void testPowerOfTwo() {
        assertTrue(QuadTreeNodeImpl.isPowerOfTwo(8));
        assertFalse(QuadTreeNodeImpl.isPowerOfTwo(3));
        assertTrue(QuadTreeNodeImpl.isPowerOfTwo(64));
        assertFalse(QuadTreeNodeImpl.isPowerOfTwo(9));
    }
}
