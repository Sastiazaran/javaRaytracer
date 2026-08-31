/**
 * [1968] - [2021] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;

import java.awt.Color;

/**
 * @author Jafet Rodríguez
 */
public class Sphere extends Object3D {

    private static final double EPSILON = 1e-6;
    private float radius;

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Sphere(Vector3D position, float radius, Color color, Material material) {
        super(position, color, material);
        setRadius(radius);
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        Vector3D oc = Vector3D.substract(ray.getOrigin(), getPosition());
        double b = Vector3D.dotProduct(ray.getDirection(), oc);
        double c = Vector3D.magnitudeSquared(oc) - (double) getRadius() * getRadius();
        double discriminant = b * b - c;

        if (discriminant < 0) {
            return null;
        }

        double sqrt = Math.sqrt(discriminant);
        double t = -b - sqrt;
        if (t < EPSILON) {
            t = -b + sqrt;
        }
        if (t < EPSILON) {
            return null;
        }

        Vector3D position = ray.pointAt(t);
        Vector3D normal = Vector3D.normalize(Vector3D.substract(position, getPosition()));
        return new Intersection(position, t, normal, this);
    }
}
