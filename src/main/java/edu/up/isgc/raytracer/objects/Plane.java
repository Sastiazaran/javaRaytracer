package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;

import java.awt.Color;

/**
 * Infinite plane with an optional XZ checkerboard pattern.
 */
public class Plane extends Object3D {

    private static final double EPSILON = 1e-6;

    private final Vector3D normal;
    private final Color secondaryColor;
    private final double checkerScale;

    public Plane(Vector3D point, Vector3D normal, Color color, Material material) {
        this(point, normal, color, color, 0, material);
    }

    public Plane(Vector3D point, Vector3D normal, Color color, Color secondaryColor, double checkerScale, Material material) {
        super(point, color, material);
        this.normal = Vector3D.normalize(normal);
        this.secondaryColor = secondaryColor;
        this.checkerScale = checkerScale;
    }

    @Override
    public Color getColorAt(Vector3D point) {
        if (checkerScale <= 0) {
            return getColor();
        }
        int cx = (int) Math.floor(point.getX() / checkerScale);
        int cz = (int) Math.floor(point.getZ() / checkerScale);
        Color checker = ((cx + cz) & 1) == 0 ? getColor() : secondaryColor;
        double dist = Math.hypot(point.getX(), point.getZ());
        double fade = Math.min(1.0, Math.max(0.0, (dist - 10.0) / 18.0));
        if (fade <= 0) {
            return checker;
        }
        Color average = new Color(
                (getColor().getRed() + secondaryColor.getRed()) / 2,
                (getColor().getGreen() + secondaryColor.getGreen()) / 2,
                (getColor().getBlue() + secondaryColor.getBlue()) / 2
        );
        return edu.up.isgc.raytracer.Colors.lerp(checker, average, fade);
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        double denom = Vector3D.dotProduct(normal, ray.getDirection());
        if (Math.abs(denom) < EPSILON) {
            return null;
        }
        Vector3D toPlane = Vector3D.substract(getPosition(), ray.getOrigin());
        double t = Vector3D.dotProduct(toPlane, normal) / denom;
        if (t < EPSILON) {
            return null;
        }
        Vector3D position = ray.pointAt(t);
        return new Intersection(position, t, normal, this);
    }
}
