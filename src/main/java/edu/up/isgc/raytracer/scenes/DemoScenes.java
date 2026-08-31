package edu.up.isgc.raytracer.scenes;

import edu.up.isgc.raytracer.Scene;
import edu.up.isgc.raytracer.Vector3D;
import edu.up.isgc.raytracer.lights.DirectionalLight;
import edu.up.isgc.raytracer.lights.PointLight;
import edu.up.isgc.raytracer.objects.Camera;
import edu.up.isgc.raytracer.objects.Material;
import edu.up.isgc.raytracer.objects.Plane;
import edu.up.isgc.raytracer.objects.Sphere;
import edu.up.isgc.raytracer.tools.OBJReader;

import java.awt.Color;

/**
 * Demo scenes that only use models shipped in the repository.
 */
public final class DemoScenes {

    private DemoScenes() {
    }

    public static Scene create(String name, int width, int height) {
        if (name == null) {
            name = "spheres";
        }
        switch (name.toLowerCase()) {
            case "teapot":
                return teapot(width, height);
            case "spheres":
            default:
                return spheres(width, height);
        }
    }

    public static Scene spheres(int width, int height) {
        Scene scene = new Scene();
        scene.setCamera(camera(new Vector3D(0, 1.05, -5.0), new Vector3D(0, -0.25, 1.2), 42f, width, height));

        Material floorMat = new Material(0.12f, 0.78f, 18f, 0.12f, 0f).specular(0.18f);
        scene.addObject(new Plane(
                new Vector3D(0, -1, 0),
                new Vector3D(0, 1, 0),
                new Color(236, 236, 232),
                new Color(36, 42, 58),
                0.85,
                floorMat
        ));

        Material mirror = new Material(0.05f, 0.12f, 96f, 0.8f, 0f).specular(0.65f);
        Material glass = new Material(0.04f, 0.06f, 128f, 0.08f, 0.86f, 1.5f).specular(0.7f);
        Material red = new Material(0.1f, 0.9f, 32f, 0.04f, 0f).specular(0.3f);
        Material blue = new Material(0.1f, 0.85f, 40f, 0.16f, 0f).specular(0.4f);
        Material gold = new Material(0.1f, 0.5f, 80f, 0.35f, 0f).specular(0.65f);

        scene.addObject(new Sphere(new Vector3D(-1.35, -0.2, 1.35), 0.8f, new Color(220, 225, 235), mirror));
        scene.addObject(new Sphere(new Vector3D(1.2, -0.35, 0.7), 0.65f, new Color(210, 235, 255), glass));
        scene.addObject(new Sphere(new Vector3D(0.05, -0.68, 2.35), 0.32f, new Color(200, 55, 55), red));
        scene.addObject(new Sphere(new Vector3D(-0.2, -0.72, 0.05), 0.28f, new Color(55, 95, 210), blue));
        scene.addObject(new Sphere(new Vector3D(1.85, -0.76, 1.85), 0.24f, new Color(218, 165, 32), gold));

        scene.addLight(new PointLight(new Vector3D(2.6, 4.4, -1.8), Color.WHITE, 18, null));
        scene.addLight(new PointLight(new Vector3D(-3.0, 2.6, 1.4), new Color(255, 210, 170), 8, null));
        scene.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(-0.25, -1.0, 0.3), new Color(210, 225, 255), 0.35, null));
        return scene;
    }

    public static Scene teapot(int width, int height) {
        Scene scene = new Scene();
        scene.setCamera(camera(new Vector3D(0.4, 0.55, -5.4), new Vector3D(0.1, -0.25, 1.1), 40f, width, height));

        Material floorMat = new Material(0.12f, 0.72f, 16f, 0.1f, 0f).specular(0.15f);
        scene.addObject(new Plane(
                new Vector3D(0, -1, 0),
                new Vector3D(0, 1, 0),
                new Color(240, 236, 226),
                new Color(58, 66, 82),
                1.0,
                floorMat
        ));

        Material ceramic = new Material(0.12f, 0.82f, 40f, 0.12f, 0f).specular(0.35f);
        Material gold = new Material(0.1f, 0.42f, 90f, 0.4f, 0f).specular(0.7f);
        Material redPlastic = new Material(0.1f, 0.88f, 24f, 0.05f, 0f).specular(0.25f);
        Material glass = new Material(0.05f, 0.08f, 100f, 0.08f, 0.82f, 1.5f).specular(0.6f);

        scene.addObject(OBJReader.GetPolygon("SmallTeapot.obj", new Vector3D(0.05, -1.0, 1.15), new Color(236, 214, 188), ceramic, 1.25));
        double ringScale = 0.38;
        scene.addObject(OBJReader.GetPolygon(
                "Ring.obj",
                new Vector3D(-1.7, -1.0 + 1.25 * ringScale, 1.35),
                new Color(212, 175, 55),
                gold,
                ringScale
        ));
        scene.addObject(OBJReader.GetPolygon("Cube.obj", new Vector3D(1.55, -1.0, 1.7), new Color(188, 52, 52), redPlastic, 0.55));
        scene.addObject(OBJReader.GetPolygon("CubeQuad.obj", new Vector3D(1.15, -1.0 + 0.22, 0.55), new Color(170, 220, 255), glass, 0.22));

        scene.addLight(new PointLight(new Vector3D(2.4, 3.8, -2.0), Color.WHITE, 16, null));
        scene.addLight(new PointLight(new Vector3D(-2.6, 2.8, 2.2), new Color(140, 175, 255), 7, null));
        scene.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(0.2, -1.0, 0.25), Color.WHITE, 0.28, null));
        return scene;
    }

    private static Camera camera(Vector3D position, Vector3D target, float fovH, int width, int height) {
        float fovV = fovH * height / (float) width;
        return new Camera(position, fovH, fovV, width, height, 0.1f, 200f, null).lookAt(target);
    }
}
