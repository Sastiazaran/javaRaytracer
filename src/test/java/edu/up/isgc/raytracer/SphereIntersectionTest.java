package edu.up.isgc.raytracer;

import edu.up.isgc.raytracer.objects.Material;
import edu.up.isgc.raytracer.objects.Sphere;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SphereIntersectionTest {

    @Test
    void hitsFrontOfUnitSphere() {
        Sphere sphere = new Sphere(Vector3D.ZERO(), 1f, Color.RED, Material.DEFAULT);
        Ray ray = new Ray(new Vector3D(0, 0, -5), new Vector3D(0, 0, 1));
        Intersection hit = sphere.getIntersection(ray);

        assertNotNull(hit);
        assertEquals(4.0, hit.getDistance(), 1e-6);
        assertEquals(0.0, hit.getPosition().getX(), 1e-6);
        assertEquals(0.0, hit.getPosition().getY(), 1e-6);
        assertEquals(-1.0, hit.getPosition().getZ(), 1e-6);
    }

    @Test
    void ignoresNegativeHitsBehindTheRay() {
        Sphere sphere = new Sphere(new Vector3D(0, 0, -10), 1f, Color.RED, Material.DEFAULT);
        Ray ray = new Ray(new Vector3D(0, 0, 0), new Vector3D(0, 0, 1));
        assertNull(sphere.getIntersection(ray));
    }

    @Test
    void missReturnsNull() {
        Sphere sphere = new Sphere(Vector3D.ZERO(), 1f, Color.RED, Material.DEFAULT);
        Ray ray = new Ray(new Vector3D(0, 5, -5), new Vector3D(0, 0, 1));
        assertNull(sphere.getIntersection(ray));
    }

    @Test
    void refractionHasSnellDirection() {
        Vector3D incident = Vector3D.normalize(new Vector3D(0.3, -1, 0));
        Vector3D normal = new Vector3D(0, 1, 0);
        Vector3D t = Raytracer.refract(incident, normal, 1.5);
        assertNotNull(t);
        assertEquals(1.0, Vector3D.magnitude(t), 1e-6);
        assertTrue(t.getY() < 0, "refracted ray should continue downward");
    }
}
