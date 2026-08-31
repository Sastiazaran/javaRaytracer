package edu.up.isgc.raytracer;

import edu.up.isgc.raytracer.objects.Material;
import edu.up.isgc.raytracer.objects.Sphere;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RefractionPathTest {

    @Test
    void glassSphereLetsRayExit() {
        Material glass = new Material(0.03f, 0.05f, 128f, 0.1f, 0.9f, 1.5f);
        Sphere sphere = new Sphere(Vector3D.ZERO(), 1f, new Color(200, 230, 255), glass);
        Ray incoming = new Ray(new Vector3D(0, 0, -5), new Vector3D(0, 0, 1));
        Intersection enter = sphere.getIntersection(incoming);
        assertNotNull(enter);

        Vector3D n = Vector3D.normalize(enter.getNormal());
        Vector3D refracted = Raytracer.refract(incoming.getDirection(), n, 1.5);
        assertNotNull(refracted);

        Vector3D originInside = Vector3D.add(enter.getPosition(), Vector3D.scalarMultiplication(refracted, 1e-3));
        Intersection exit = sphere.getIntersection(new Ray(originInside, refracted));
        assertNotNull(exit, "ray should hit the back face");
        assertTrue(exit.getDistance() > 0.5, "exit should be across the sphere, t=" + exit.getDistance());
    }
}
