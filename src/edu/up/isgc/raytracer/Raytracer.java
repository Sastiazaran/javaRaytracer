/**
 * [1968] - [2021] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer;

import edu.up.isgc.raytracer.lights.DirectionalLight;
import edu.up.isgc.raytracer.lights.Light;
import edu.up.isgc.raytracer.lights.PointLight;
import edu.up.isgc.raytracer.objects.*;
import edu.up.isgc.raytracer.tools.OBJReader;


import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Jafet Rodríguez
 */
public class Raytracer {

    public static void main(String[] args) {
        System.out.println(new Date());
        Scene scene01 = new Scene();

        Material sphere = new Material(.2f, .2f, .1f, 0, 1);
        Material teaPot = new Material(.2f, 1f, 10f, .2f, 0);
        Material plane = new Material(.2f, 10f, 20f, .2f, 0);
        Material ring = new Material(.2f, 0f, 40f, 1, 0);
        Material Wolf = new Material(.2f, 0f, 0f, 0, 0);
        Material Candle = new Material(.2f, 0f, 0f, 0, 0);
        Material Batarang = new Material(.2f, 0f, 0f, 0, 1);
        Material Joker = new Material(.2f, 10f, 40f, 1, 0);



        scene01.setCamera(new Camera(new Vector3D(0, 0, -8), 160, 160, 50, 50, -10f, 50f, null));
        /*scene01.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(0.0, 0.0, 1.0), Color.WHITE, 0.8));
        scene01.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(0.0, -0.1, 0.1), Color.WHITE, 0.2));*/
        //scene01.addLight(new DirectionalLight(Vector3D.ZERO(), new Vector3D(-0.2, -0.1, 0.0), Color.WHITE, 0.2, null));
        //scene01.addLight(new PointLight(new Vector3D(-3f, 3.5f, 4f), Color.WHITE, 2, null));
        scene01.addLight(new PointLight(new Vector3D(0f, 3.5f, 8f), Color.GREEN, .76, null));
        scene01.addLight(new PointLight(new Vector3D(2f, 3.5f, 8f), Color.RED, .2, null));
        scene01.addLight(new PointLight(new Vector3D(-1f, 3.5f, 8f), new Color(0, 123, 107), .3, null));
        //scene01.addLight(new PointLight(new Vector3D(1f, 6f, 0f), new Color(255, 75, 75), 1.5, null));
        //scene01.addLight(new PointLight(new Vector3D(4f, 6f, -1f), new Color(255, 191, 0), 1.5 , null));
        //scene01.addLight(new PointLight(new Vector3D(0f, 1f, -5f), Color.WHITE, 1.7, null));
        //scene01.addObject(new Sphere(new Vector3D(1f, -2f, 2f), 0.5f, Color.GRAY, sphere));
        //scene01.addObject(new Sphere(new Vector3D(0.5f, 1f, 4.5f), 0.25f, new Color(200, 255, 0)));
        //scene01.addObject(new Sphere(new Vector3D(0.35f, 1f, 4.5f), 0.3f, Color.BLUE));
        //scene01.addObject(new Sphere(new Vector3D(4.85f, 1f, 4.5f), 0.3f, Color.PINK));
        //scene01.addObject(new Sphere(new Vector3D(2.85f, 1f, 304.5f), 0.5f, Color.BLUE, teaPot));
        //scene01.addObject(OBJReader.GetPolygon("Cube.obj", new Vector3D(0f, -2.5f, 1f), Color.WHITE));
        //scene01.addObject(OBJReader.GetPolygon("CubeQuad.obj", new Vector3D(-3f, -2.5f, 3f), Color.GREEN));
        //scene01.addObject(OBJReader.GetPolygon("skullrotated.obj", new Vector3D(0f, -5.5f, 1f), Color.DARK_GRAY, teaPot));
        scene01.addObject(OBJReader.GetPolygon("SizedJoker3.obj", new Vector3D(0f, -3f, 4f), new Color(124, 0, 199), teaPot));
        scene01.addObject(OBJReader.GetPolygon("BatarangSized.obj", new Vector3D(0f, -2.5f, 1f), Color.DARK_GRAY, Batarang));
        //scene01.addObject(OBJReader.GetPolygon("Ring.obj", new Vector3D(2f, -1.0f, 1.5f), Color.BLUE));
        //scene01.addObject(OBJReader.GetPolygon("BigWolfRotated.obj", new Vector3D(1,-6.5,2), new Color(52, 52, 52), Wolf));
        //scene01.addObject((OBJReader.GetPolygon("candle1.obj", new Vector3D(.3f, -7f, 5f), new Color(255, 242, 202), Candle)));
        //scene01.addObject((OBJReader.GetPolygon("candle1.obj", new Vector3D(5.5f, -7f, 5f), new Color(255, 242, 202), Candle)));
        scene01.addObject(OBJReader.GetPolygon("yamelosaque.obj", new Vector3D(0f, -6f, 0f), Color.DARK_GRAY, plane));
        scene01.addObject(OBJReader.GetPolygon("yamelosaque.obj", new Vector3D(-6.3f, -6f, 0f), Color.DARK_GRAY, plane));
        scene01.addObject(OBJReader.GetPolygon("yamelosaque.obj", new Vector3D(6.3f, -6f, 0f), Color.DARK_GRAY, plane));
        scene01.addObject(OBJReader.GetPolygon("yamelosaque.obj", new Vector3D(0f, -6f, 6.3f), Color.DARK_GRAY, plane));
        scene01.addObject(OBJReader.GetPolygon("yamelosaque.obj", new Vector3D(-6.3f, -6f, 6.3f), Color.DARK_GRAY, plane));
        scene01.addObject(OBJReader.GetPolygon("yamelosaque.obj", new Vector3D(6.3f, -6f, 6.3f), Color.DARK_GRAY, plane));




        BufferedImage image = raytrace(scene01);
        File outputImage = new File("image.png");
        try {
            ImageIO.write(image, "png", outputImage);
        } catch (IOException ioe) {
            System.out.println("Something failed");
        }
        System.out.println(new Date());
    }

    public static BufferedImage raytrace(Scene scene) {
        Camera mainCamera = scene.getCamera();
        ArrayList<Light> lights = scene.getLights();
        float[] nearFarPlanes = mainCamera.getNearFarPlanes();
        BufferedImage image = new BufferedImage(mainCamera.getResolutionWidth(), mainCamera.getResolutionHeight(), BufferedImage.TYPE_INT_RGB);
        ArrayList<Object3D> objects = scene.getObjects();

        Vector3D[][] positionsToRaytrace = mainCamera.calculatePositionsToRay();
        for (int i = 0; i < positionsToRaytrace.length; i++) {
            for (int j = 0; j < positionsToRaytrace[i].length; j++) {
                double x = positionsToRaytrace[i][j].getX() + mainCamera.getPosition().getX();
                double y = positionsToRaytrace[i][j].getY() + mainCamera.getPosition().getY();
                double z = positionsToRaytrace[i][j].getZ() + mainCamera.getPosition().getZ();

                Ray ray = new Ray(mainCamera.getPosition(), new Vector3D(x, y, z));
                float cameraZ = (float) mainCamera.getPosition().getZ();
                Intersection closestIntersection = raycast(ray, objects, null, new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]});

                //Background color
                Color pixelColor = Color.BLACK;
                if (closestIntersection != null)
                {
                    pixelColor = Color.BLACK;
                    for (Light light : lights)
                    {
                        pixelColor = addColor(pixelColor, getObjectColor(closestIntersection,light,objects,pixelColor,mainCamera, nearFarPlanes));

                        if(closestIntersection.getObject().getMaterial().getReflection() != 0)
                        {

                            //pixelColor = addColor(pixelColor, specular(light, closestIntersection, closestIntersection.getObject(), nearFarPlanes));

                            //adding reflection
                            pixelColor = addColor(pixelColor, getObjectColor(closestIntersection,light, objects, pixelColor, mainCamera, nearFarPlanes));
                            Color reflectiveIntersection = reflect(closestIntersection, mainCamera, objects, nearFarPlanes, light, cameraZ);
                            pixelColor = addColor(pixelColor, reflectiveIntersection);

                        }

                        if(closestIntersection.getObject().getMaterial().getRefraction() != 0)
                        {
                            pixelColor = addColor(pixelColor, getObjectColor(closestIntersection,light, objects, pixelColor, mainCamera, nearFarPlanes));
                            Color refractiveIntersection = refract(closestIntersection, mainCamera, objects, nearFarPlanes, light, cameraZ);
                            pixelColor = addColor(pixelColor, refractiveIntersection);

                        }

                    }
                }
                image.setRGB(i, j, pixelColor.getRGB());
            }
        }

        return image;
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static Color addColor(Color original, Color otherColor) {
        float red = clamp((original.getRed() / 255.0f) + (otherColor.getRed() / 255.0f), 0, 1);
        float green = clamp((original.getGreen() / 255.0f) + (otherColor.getGreen() / 255.0f), 0, 1);
        float blue = clamp((original.getBlue() / 255.0f) + (otherColor.getBlue() / 255.0f), 0, 1);
        return new Color(red, green, blue);
    }

    public static float[] ambient(Object3D object3D) {
        return new float[]
                {
                        (object3D.getColor().getRed() / 255.0f * object3D.getMaterial().getAmbient()),
                        (object3D.getColor().getGreen() / 255.0f * object3D.getMaterial().getAmbient()),
                        (object3D.getColor().getBlue() / 255.0f * object3D.getMaterial().getAmbient())
                };
    }

    public static Color specular(Light light, Intersection intersection, Object3D obj, float[] colors) {
        Vector3D h = Vector3D.add(light.getPosition(), intersection.getPosition());
        h = Vector3D.normalize(h);
        float specular = (float) Math.pow(Vector3D.dotProduct(intersection.getNormal(), h), obj.getMaterial().getShininess());
        for (int color = 0; color < 3; color++) colors[color] *= specular;
        Color specular_ = new Color(clamp(colors[0], 0, 1), clamp(colors[1], 0, 1), clamp(colors[2], 0, 1));
        return specular_;
    }

    public static Intersection raycast(Ray ray, ArrayList<Object3D> objects, Object3D caster, float[] clippingPlanes) {
        Intersection closestIntersection = null;

        for (int k = 0; k < objects.size(); k++) {
            Object3D currentObj = objects.get(k);
            if (caster == null || !currentObj.equals(caster)) {
                Intersection intersection = currentObj.getIntersection(ray);
                if (intersection != null) {
                    double distance = intersection.getDistance();
                    if (distance >= 0 &&
                            (closestIntersection == null || distance < closestIntersection.getDistance()) &&
                            (clippingPlanes == null || (intersection.getPosition().getZ() >= clippingPlanes[0] &&
                                    intersection.getPosition().getZ() <= clippingPlanes[1]))) {
                        closestIntersection = intersection;
                    }
                }
            }
        }
        return closestIntersection;
    }

    public static Color reflect(Intersection closestIntersection, Camera mainCamera, ArrayList<Object3D> objects,  float[] nearFarPlanes, Light light, float cameraZ) {
        Vector3D incidentRay = Vector3D.substract(closestIntersection.getPosition(), mainCamera.getPosition());
        double normalRay = Vector3D.dotProduct(closestIntersection.getNormal(), incidentRay);
        Vector3D reflect = Vector3D.scalarMultiplication(closestIntersection.getNormal(), normalRay);
        Vector3D two_reflect = Vector3D.scalarMultiplication(reflect, -2.0);

        Color reflectColor = Color.BLACK;

        Ray reflectHit = new Ray(closestIntersection.getPosition(), two_reflect);
        nearFarPlanes = new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]};
        Intersection reflectIntersection = raycast(reflectHit,objects,closestIntersection.getObject(),nearFarPlanes);

        if(reflectIntersection != null && closestIntersection.getObject() != reflectIntersection.getObject())
        {

            reflectColor = getObjectColor(reflectIntersection, light, objects, reflectColor, mainCamera, nearFarPlanes);
            //float reflectIndex = closestIntersection.getObject().getMaterial().getReflection();
            //float[] rgbColor = new float[]{reflectColor.getRed() * reflectIndex, reflectColor.getGreen() * reflectIndex, reflectColor.getBlue() * reflectIndex};
            //reflectColor = new Color(clamp(rgbColor[0], 0, .3f), clamp(rgbColor[1],0,.3f), clamp(rgbColor[2],0,.3f));
            //reflectColor = addColor(reflectColor, reflect(closestIntersection, mainCamera, objects, nearFarPlanes, light, cameraZ));

        }

        return reflectColor;
    }

    public static Color refract(Intersection closestIntersection, Camera mainCamera, ArrayList<Object3D> objects, float[] nearFarPlanes, Light light, float cameraZ)
    {
        double n1 = 1;
        double n2 = 1.5;

        Vector3D incidentRay = Vector3D.substract(closestIntersection.getPosition(), mainCamera.getPosition());
        double c1 = Vector3D.dotProduct(closestIntersection.getNormal(), incidentRay);
        double n =  n1 / n2;

        double c2 = Math.sqrt(1-Math.pow(n, 2) * (1 - Math.pow(c1, 2)));

        Vector3D n_I = Vector3D.scalarMultiplication(incidentRay, n);
        double n_c1MinusC2 = (n * c1) - c2;
        Vector3D n_c1MinusC2dotN = Vector3D.scalarMultiplication(closestIntersection.getNormal(),n_c1MinusC2);
        Vector3D T = Vector3D.add(n_I, n_c1MinusC2dotN);


        Color refractColor = Color.BLACK;

        Ray reflectHit = new Ray(closestIntersection.getPosition(), T);
        nearFarPlanes = new float[]{cameraZ + nearFarPlanes[0], cameraZ + nearFarPlanes[1]};
        Intersection reflectIntersection = raycast(reflectHit,objects,closestIntersection.getObject(),nearFarPlanes);

        if(reflectIntersection != null && closestIntersection.getObject() != reflectIntersection.getObject())
        {

            refractColor = getObjectColor(reflectIntersection, light, objects, refractColor, mainCamera, nearFarPlanes);
            //float reflectIndex = closestIntersection.getObject().getMaterial().getReflection();
            //float[] rgbColor = new float[]{reflectColor.getRed() * reflectIndex, reflectColor.getGreen() * reflectIndex, reflectColor.getBlue() * reflectIndex};
            //reflectColor = new Color(clamp(rgbColor[0], 0, .3f), clamp(rgbColor[1],0,.3f), clamp(rgbColor[2],0,.3f));
            //reflectColor = addColor(reflectColor, reflect(closestIntersection, mainCamera, objects, nearFarPlanes, light, cameraZ));

        }

        return refractColor;

    }

    public static Color getObjectColor(Intersection closestIntersection, Light light, ArrayList<Object3D> objects, Color pixelColor, Camera mainCamera, float[] nearFarPlanes)
    {
        Intersection shadowCheck = raycast(new Ray(closestIntersection.getPosition(), light.getPosition()), objects, closestIntersection.getObject(), nearFarPlanes);
        float nDotL = light.getNDotL(closestIntersection);
        float intensity = (float) light.getIntensity() * nDotL;
        Color lightColor = light.getColor();
        Color objColor = closestIntersection.getObject().getColor();
        float[] lightColors = new float[]{lightColor.getRed() / 255.0f, lightColor.getGreen() / 255.0f, lightColor.getBlue() / 255.0f};
        float[] objColors = new float[]{objColor.getRed() / 255.0f, objColor.getGreen() / 255.0f, objColor.getBlue() / 255.0f};
        for (int colorIndex = 0; colorIndex < objColors.length; colorIndex++) {
            objColors[colorIndex] *= (intensity * lightColors[colorIndex]) / closestIntersection.getDistance();
        }
        Color diffuse = new Color(clamp(objColors[0], 0, 1),clamp(objColors[1], 0, 1), clamp(objColors[2], 0, 1));
        float []colors = ambient(closestIntersection.getObject());
        diffuse = addColor(diffuse, specular(light, closestIntersection, closestIntersection.getObject(), colors));
        pixelColor = addColor(pixelColor, diffuse);

        if(shadowCheck != null)
        {
            float[] ambientColors = ambient(closestIntersection.getObject());
            pixelColor = new Color(ambientColors[0], ambientColors[1], ambientColors[2]);
        }
        return pixelColor;
    }

}
