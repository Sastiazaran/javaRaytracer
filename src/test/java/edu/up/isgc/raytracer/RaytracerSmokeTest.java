package edu.up.isgc.raytracer;

import edu.up.isgc.raytracer.lights.PointLight;
import edu.up.isgc.raytracer.objects.Camera;
import edu.up.isgc.raytracer.objects.Material;
import edu.up.isgc.raytracer.objects.Sphere;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RaytracerSmokeTest {

    @Test
    void sphereSceneProducesLitPixels() {
        Scene scene = new Scene();
        scene.setCamera(new Camera(new Vector3D(0, 0, -4), 50f, 50f, 16, 16, 0.1f, 100f, null));
        scene.addObject(new Sphere(new Vector3D(0, 0, 0), 1f, Color.RED, new Material(0.15f, 0.9f, 32f, 0f, 0f)));
        scene.addLight(new PointLight(new Vector3D(2, 3, -2), Color.WHITE, 40, null));

        BufferedImage image = Raytracer.raytrace(scene, 1, 2);
        int lit = 0;
        int skyish = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));
                if (color.getRed() > 40 && color.getRed() > color.getBlue()) {
                    lit++;
                }
                if (color.getBlue() > color.getRed()) {
                    skyish++;
                }
            }
        }
        assertTrue(lit > 10, "expected a visible red sphere, lit=" + lit);
        assertTrue(skyish > 10, "expected sky around the sphere, sky=" + skyish);
    }
}
