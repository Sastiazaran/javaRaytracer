package edu.up.isgc.raytracer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Vector3DTest {

    private static final double EPS = 1e-9;

    @Test
    void addSubtractAndDot() {
        Vector3D a = new Vector3D(1, 2, 3);
        Vector3D b = new Vector3D(4, -1, 0.5);
        Vector3D sum = Vector3D.add(a, b);
        Vector3D diff = Vector3D.substract(a, b);

        assertEquals(5.0, sum.getX(), EPS);
        assertEquals(1.0, sum.getY(), EPS);
        assertEquals(3.5, sum.getZ(), EPS);
        assertEquals(-3.0, diff.getX(), EPS);
        assertEquals(3.0, diff.getY(), EPS);
        assertEquals(2.5, diff.getZ(), EPS);
        assertEquals(3.5, Vector3D.dotProduct(a, b), EPS);
    }

    @Test
    void normalizeZeroIsZero() {
        Vector3D zero = Vector3D.normalize(Vector3D.ZERO());
        assertEquals(0.0, Vector3D.magnitude(zero), EPS);
    }

    @Test
    void normalizeUnitLength() {
        Vector3D n = Vector3D.normalize(new Vector3D(0, 3, 4));
        assertEquals(1.0, Vector3D.magnitude(n), EPS);
        assertEquals(0.0, n.getX(), EPS);
        assertEquals(0.6, n.getY(), EPS);
        assertEquals(0.8, n.getZ(), EPS);
    }

    @Test
    void reflectAroundUpAxis() {
        Vector3D incident = Vector3D.normalize(new Vector3D(1, -1, 0));
        Vector3D normal = new Vector3D(0, 1, 0);
        Vector3D reflected = Vector3D.reflect(incident, normal);
        Vector3D expected = Vector3D.normalize(new Vector3D(1, 1, 0));
        assertEquals(expected.getX(), reflected.getX(), 1e-6);
        assertEquals(expected.getY(), reflected.getY(), 1e-6);
        assertEquals(expected.getZ(), reflected.getZ(), 1e-6);
        assertEquals(1.0, Vector3D.magnitude(reflected), 1e-6);
    }

    @Test
    void crossProductRightHanded() {
        Vector3D c = Vector3D.crossProduct(new Vector3D(1, 0, 0), new Vector3D(0, 1, 0));
        assertEquals(0.0, c.getX(), EPS);
        assertEquals(0.0, c.getY(), EPS);
        assertEquals(1.0, c.getZ(), EPS);
    }
}
