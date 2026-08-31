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
        scene.setCamera(camera(new Vector3D(0, 1.15, -4.2), 50f, width, height));

        Material floorMat = new Material(0.08f, 0.75f, 24f, 0.18f, 0f).specular(0.25f);
        scene.addObject(new Plane(
                new Vector3D(0, -1, 0),
                new Vector3D(0, 1, 0),
                new Color(232, 232, 232),
                new Color(42, 48, 62),
                0.9,
                floorMat
        ));

        Material mirror = new Material(0.04f, 0.12f, 96f, 0.82f, 0f).specular(0.7f);
        Material glass = new Material(0.03f, 0.05f, 128f, 0.12f, 0.82f, 1.5f).specular(0.8f);
        Material red = new Material(0.08f, 0.9f, 32f, 0.04f, 0f).specular(0.35f);
        Material blue = new Material(0.08f, 0.85f, 48f, 0.2f, 0f).specular(0.45f);
        Material gold = new Material(0.08f, 0.45f, 80f, 0.4f, 0f).specular(0.7f);

        scene.addObject(new Sphere(new Vector3D(-1.25, -0.2, 1.4), 0.8f, new Color(210, 215, 225), mirror));
        scene.addObject(new Sphere(new Vector3D(1.15, -0.35, 0.55), 0.65f, new Color(190, 230, 255), glass));
        scene.addObject(new Sphere(new Vector3D(0.05, -0.7, 2.15), 0.3f, new Color(200, 55, 55), red));
        scene.addObject(new Sphere(new Vector3D(-0.15, -0.75, 0.15), 0.25f, new Color(55, 95, 210), blue));
        scene.addObject(new Sphere(new Vector3D(1.85, -0.78, 1.7), 0.22f, new Color(218, 165, 32), gold));

        scene.addLight(new PointLight(new Vector3D(2.4, 4.2, -1.5), Color.WHITE, 55, null));
        scene.addLight(new PointLight(new Vector3D(-3.2, 2.8, 1.2), new Color(255, 210, 170), 22, null));
        scene.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(-0.2, -1.0, 0.35), new Color(200, 220, 255), 0.25, null));
        return scene;
    }

    public static Scene teapot(int width, int height) {
        Scene scene = new Scene();
        scene.setCamera(camera(new Vector3D(0.15, 1.35, -4.6), 46f, width, height));

        Material floorMat = new Material(0.08f, 0.7f, 18f, 0.12f, 0f).specular(0.2f);
        scene.addObject(new Plane(
                new Vector3D(0, -1, 0),
                new Vector3D(0, 1, 0),
                new Color(240, 236, 226),
                new Color(70, 78, 92),
                1.0,
                floorMat
        ));

        Material ceramic = new Material(0.1f, 0.85f, 48f, 0.18f, 0f).specular(0.45f);
        Material gold = new Material(0.08f, 0.4f, 90f, 0.45f, 0f).specular(0.75f);
        Material redPlastic = new Material(0.08f, 0.9f, 28f, 0.05f, 0f).specular(0.3f);
        Material glass = new Material(0.04f, 0.08f, 100f, 0.1f, 0.8f, 1.5f).specular(0.7f);

        scene.addObject(OBJReader.GetPolygon("SmallTeapot.obj", new Vector3D(0.1, -1.0, 1.3), new Color(236, 214, 188), ceramic, 1.35));
        double ringScale = 0.7;
        scene.addObject(OBJReader.GetPolygon("Ring.obj", new Vector3D(-1.55, -1.0 + 1.25 * ringScale, 0.85), new Color(212, 175, 55), gold, ringScale));
        scene.addObject(OBJReader.GetPolygon("Cube.obj", new Vector3D(1.65, -1.0, 1.55), new Color(188, 52, 52), redPlastic, 0.7));
        scene.addObject(OBJReader.GetPolygon("CubeQuad.obj", new Vector3D(0.95, -1.0 + 0.28, 2.55), new Color(170, 220, 255), glass, 0.28));

        scene.addLight(new PointLight(new Vector3D(2.2, 4.0, -1.8), Color.WHITE, 50, null));
        scene.addLight(new PointLight(new Vector3D(-2.8, 3.2, 2.0), new Color(120, 170, 255), 16, null));
        scene.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(0.15, -1.0, 0.2), Color.WHITE, 0.2, null));
        return scene;
    }

    private static Camera camera(Vector3D position, float fovH, int width, int height) {
        float fovV = fovH * height / (float) width;
        return new Camera(position, fovH, fovV, width, height, 0.1f, 200f, null);
    }
}
