/**
 * [1968] - [2021] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer;

import edu.up.isgc.raytracer.lights.DirectionalLight;
import edu.up.isgc.raytracer.lights.Light;
import edu.up.isgc.raytracer.objects.Camera;
import edu.up.isgc.raytracer.objects.Material;
import edu.up.isgc.raytracer.objects.Object3D;
import edu.up.isgc.raytracer.scenes.DemoScenes;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author Jafet Rodríguez
 */
public class Raytracer {

    private static final double EPSILON = 1e-4;
    private static final Color SKY_ZENITH = new Color(88, 148, 228);
    private static final Color SKY_HORIZON = new Color(232, 240, 255);

    public static void main(String[] args) {
        Options options = Options.parse(args);
        System.out.println("javaRaytracer  " + new Date());
        List<Job> jobs = options.jobs();
        for (Job job : jobs) {
            renderJob(job);
        }
        System.out.println("done  " + new Date());
    }

    private static void renderJob(Job job) {
        Scene scene = DemoScenes.create(job.scene, job.width, job.height);
        System.out.printf(Locale.US, "Rendering scene '%s' at %dx%d, %dx SSAA, depth %d -> %s%n",
                job.scene, job.width, job.height, job.samples, job.depth, job.out);
        long start = System.nanoTime();
        BufferedImage image = raytrace(scene, job.samples, job.depth);
        long ms = (System.nanoTime() - start) / 1_000_000L;
        File outputImage = new File(job.out);
        File parent = outputImage.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        try {
            ImageIO.write(image, "png", outputImage);
            System.out.printf(Locale.US, "Wrote %s (%d ms)%n", outputImage.getPath(), ms);
        } catch (IOException ioe) {
            System.err.println("Failed to write " + job.out + ": " + ioe.getMessage());
        }
    }

    public static BufferedImage raytrace(Scene scene) {
        return raytrace(scene, 1, 4);
    }

    public static BufferedImage raytrace(Scene scene, int samples, int maxDepth) {
        Camera camera = scene.getCamera();
        int width = camera.getResolutionWidth();
        int height = camera.getResolutionHeight();
        int safeSamples = Math.max(1, samples);
        int safeDepth = Math.max(1, maxDepth);
        float[] nearFar = camera.getNearFarPlanes();
        double tMin = nearFar != null && nearFar[0] > 0 ? nearFar[0] : EPSILON;
        double tMax = nearFar != null && nearFar[1] > tMin ? nearFar[1] : 1_000.0;

        int[] pixels = new int[width * height];
        AtomicInteger rowsDone = new AtomicInteger();
        final int reportEvery = Math.max(1, height / 10);

        IntStream.range(0, height).parallel().forEach(y -> {
            for (int x = 0; x < width; x++) {
                float[] acc = new float[3];
                for (int sy = 0; sy < safeSamples; sy++) {
                    for (int sx = 0; sx < safeSamples; sx++) {
                        double px = x + (sx + 0.5) / safeSamples;
                        double py = y + (sy + 0.5) / safeSamples;
                        Ray ray = camera.rayThrough(px, py);
                        Color sample = trace(ray, scene, 0, safeDepth, tMin, tMax);
                        Colors.addScaled(acc, Colors.from(sample), 1f);
                    }
                }
                Colors.scale(acc, 1f / (safeSamples * safeSamples));
                pixels[y * width + x] = Colors.toColor(acc).getRGB();
            }
            int done = rowsDone.incrementAndGet();
            if (height >= 32 && (done == height || done % reportEvery == 0)) {
                System.out.printf(Locale.US, "  %d/%d rows (%.0f%%)%n", done, height, 100.0 * done / height);
            }
        });

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return image;
    }

    public static Color trace(Ray ray, Scene scene, int depth, int maxDepth, double tMin, double tMax) {
        if (depth > maxDepth) {
            return Color.BLACK;
        }
        Intersection hit = raycast(ray, scene.getObjects(), tMin, tMax);
        if (hit == null) {
            return sky(ray.getDirection());
        }
        return shade(hit, ray, scene, depth, maxDepth, tMax);
    }

    public static Intersection raycast(Ray ray, ArrayList<Object3D> objects, double tMin, double tMax) {
        Intersection closest = null;
        double closestT = tMax;
        if (objects == null) {
            return null;
        }
        for (Object3D object : objects) {
            if (object == null) {
                continue;
            }
            Intersection hit = object.getIntersection(ray);
            if (hit == null) {
                continue;
            }
            double t = hit.getDistance();
            if (t >= tMin && t < closestT) {
                closestT = t;
                closest = hit;
            }
        }
        return closest;
    }

    /**
     * Legacy signature kept for older call sites; clips by positive ray distance.
     */
    public static Intersection raycast(Ray ray, ArrayList<Object3D> objects, Object3D caster, float[] clippingPlanes) {
        double tMin = EPSILON;
        double tMax = 1_000.0;
        if (clippingPlanes != null && clippingPlanes.length >= 2 && clippingPlanes[1] > clippingPlanes[0]) {
            tMin = Math.max(EPSILON, clippingPlanes[0]);
            tMax = clippingPlanes[1];
        }
        Intersection hit = raycast(ray, objects, tMin, tMax);
        if (hit != null && caster != null && hit.getObject() == caster) {
            return raycast(ray, objects, tMin + hit.getDistance() + EPSILON, tMax);
        }
        return hit;
    }

    private static Color shade(Intersection hit, Ray ray, Scene scene, int depth, int maxDepth, double tMax) {
        Object3D object = hit.getObject();
        Material material = object.getMaterial();
        Vector3D geometricNormal = Vector3D.normalize(hit.getNormal());
        Vector3D incident = ray.getDirection();
        boolean frontFace = Vector3D.dotProduct(incident, geometricNormal) < 0;
        Vector3D normal = frontFace ? geometricNormal : Vector3D.scalarMultiplication(geometricNormal, -1);
        Vector3D view = Vector3D.scalarMultiplication(incident, -1.0);
        Color objColor = object.getColorAt(hit.getPosition());
        float[] rgb = new float[3];
        float[] albedo = Colors.from(objColor);

        Colors.addScaled(rgb, albedo, material.getAmbient());

        for (Light light : scene.getLights()) {
            Vector3D lightDir;
            double lightDistance;
            float attenuation;
            if (light instanceof DirectionalLight) {
                lightDir = Vector3D.scalarMultiplication(((DirectionalLight) light).getDirection(), -1.0);
                lightDistance = Double.POSITIVE_INFINITY;
                attenuation = 1f;
            } else {
                Vector3D toLight = Vector3D.substract(light.getPosition(), hit.getPosition());
                lightDistance = Vector3D.magnitude(toLight);
                if (lightDistance < EPSILON) {
                    continue;
                }
                lightDir = Vector3D.scalarMultiplication(toLight, 1.0 / lightDistance);
                attenuation = (float) (1.0 / (1.0 + lightDistance * lightDistance));
            }

            Vector3D shadowOrigin = Vector3D.add(hit.getPosition(), Vector3D.scalarMultiplication(normal, EPSILON * 8));
            double shadowTMax = Double.isInfinite(lightDistance) ? tMax : Math.max(EPSILON, lightDistance - EPSILON);
            Intersection shadowHit = raycast(new Ray(shadowOrigin, lightDir), scene.getObjects(), EPSILON, shadowTMax);
            if (shadowHit != null) {
                continue;
            }

            float nDotL = (float) Math.max(0.0, Vector3D.dotProduct(normal, lightDir));
            float intensity = (float) light.getIntensity() * attenuation;
            float[] lightColor = Colors.from(light.getColor());

            float diffuseScale = material.getDiffuse() * nDotL * intensity;
            rgb[0] += albedo[0] * lightColor[0] * diffuseScale;
            rgb[1] += albedo[1] * lightColor[1] * diffuseScale;
            rgb[2] += albedo[2] * lightColor[2] * diffuseScale;

            if (material.getShininess() > 0 && material.getSpecular() > 0 && nDotL > 0) {
                Vector3D half = Vector3D.normalize(Vector3D.add(lightDir, view));
                float nDotH = (float) Math.max(0.0, Vector3D.dotProduct(normal, half));
                float spec = (float) Math.pow(nDotH, material.getShininess()) * material.getSpecular() * intensity;
                Colors.addScaled(rgb, lightColor, spec);
            }
        }

        float kr = Colors.clamp(material.getReflection());
        float kt = Colors.clamp(material.getRefraction());
        float localWeight = Colors.clamp(1f - kr - kt);
        Colors.scale(rgb, localWeight);

        if (depth < maxDepth && kr > 0) {
            Vector3D reflected = Vector3D.reflect(incident, normal);
            Ray reflectRay = new Ray(Vector3D.add(hit.getPosition(), Vector3D.scalarMultiplication(normal, EPSILON * 8)), reflected);
            Color reflectedColor = trace(reflectRay, scene, depth + 1, maxDepth, EPSILON, tMax);
            Colors.addScaled(rgb, Colors.from(reflectedColor), kr);
        }

        if (depth < maxDepth && kt > 0) {
            Vector3D refracted = refract(incident, geometricNormal, material.getIor());
            if (refracted != null) {
                Vector3D offset = Vector3D.scalarMultiplication(normal, frontFace ? -EPSILON * 8 : EPSILON * 8);
                Ray refractRay = new Ray(Vector3D.add(hit.getPosition(), offset), refracted);
                Color refractedColor = trace(refractRay, scene, depth + 1, maxDepth, EPSILON, tMax);
                Colors.addScaled(rgb, Colors.from(refractedColor), kt);
            } else {
                Vector3D reflected = Vector3D.reflect(incident, normal);
                Ray reflectRay = new Ray(Vector3D.add(hit.getPosition(), Vector3D.scalarMultiplication(normal, EPSILON * 8)), reflected);
                Color reflectedColor = trace(reflectRay, scene, depth + 1, maxDepth, EPSILON, tMax);
                Colors.addScaled(rgb, Colors.from(reflectedColor), kt);
            }
        }

        return Colors.toColor(rgb);
    }

    /**
     * Snell's law. Returns null on total internal reflection.
     */
    public static Vector3D refract(Vector3D incident, Vector3D geometricNormal, double ior) {
        Vector3D n = Vector3D.normalize(geometricNormal);
        double cosI = -Vector3D.dotProduct(n, incident);
        double etaI = 1.0;
        double etaT = ior;
        if (cosI < 0) {
            n = Vector3D.scalarMultiplication(n, -1.0);
            cosI = -cosI;
            etaI = ior;
            etaT = 1.0;
        }
        double eta = etaI / etaT;
        double k = 1.0 - eta * eta * (1.0 - cosI * cosI);
        if (k < 0) {
            return null;
        }
        return Vector3D.add(
                Vector3D.scalarMultiplication(incident, eta),
                Vector3D.scalarMultiplication(n, eta * cosI - Math.sqrt(k))
        );
    }

    public static Color sky(Vector3D direction) {
        double t = 0.5 * (Vector3D.normalize(direction).getY() + 1.0);
        return Colors.lerp(SKY_HORIZON, SKY_ZENITH, t);
    }

    public static float clamp(float value, float min, float max) {
        return Colors.clamp(value, min, max);
    }

    public static Color addColor(Color original, Color otherColor) {
        float[] rgb = Colors.from(original);
        Colors.addScaled(rgb, Colors.from(otherColor), 1f);
        return Colors.toColor(rgb);
    }

    static final class Options {
        String scene = "spheres";
        int width = 640;
        int height = 360;
        int samples = 2;
        int depth = 5;
        String out = "image.png";

        static Options parse(String[] args) {
            Options options = new Options();
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "--scene":
                        options.scene = next(args, ++i, arg);
                        break;
                    case "--width":
                        options.width = Integer.parseInt(next(args, ++i, arg));
                        break;
                    case "--height":
                        options.height = Integer.parseInt(next(args, ++i, arg));
                        break;
                    case "--samples":
                        options.samples = Integer.parseInt(next(args, ++i, arg));
                        break;
                    case "--depth":
                        options.depth = Integer.parseInt(next(args, ++i, arg));
                        break;
                    case "--out":
                        options.out = next(args, ++i, arg);
                        break;
                    case "--help":
                    case "-h":
                        printHelp();
                        System.exit(0);
                        break;
                    default:
                        System.err.println("Unknown argument: " + arg);
                        printHelp();
                        System.exit(1);
                }
            }
            return options;
        }

        List<Job> jobs() {
            List<Job> jobs = new ArrayList<>();
            if ("all".equalsIgnoreCase(scene)) {
                jobs.add(new Job("spheres", width, height, samples, depth, "renders/spheres.png"));
                jobs.add(new Job("teapot", width, height, samples, depth, "renders/teapot.png"));
            } else {
                jobs.add(new Job(scene, width, height, samples, depth, out));
            }
            return jobs;
        }

        private static String next(String[] args, int index, String flag) {
            if (index >= args.length) {
                throw new IllegalArgumentException("Missing value for " + flag);
            }
            return args[index];
        }

        private static void printHelp() {
            System.out.println("Usage: Raytracer [--scene spheres|teapot|all] [--width N] [--height N] [--samples N] [--depth N] [--out file.png]");
        }
    }

    static final class Job {
        final String scene;
        final int width;
        final int height;
        final int samples;
        final int depth;
        final String out;

        Job(String scene, int width, int height, int samples, int depth, String out) {
            this.scene = scene;
            this.width = width;
            this.height = height;
            this.samples = samples;
            this.depth = depth;
            this.out = out;
        }
    }
}
