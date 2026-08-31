/**
 * [1968] - [2021] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;
import edu.up.isgc.raytracer.tools.Barycentric;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jafet Rodríguez
 */
public class Polygon extends Object3D {

    public List<Triangle> triangles;
    private double minX, minY, minZ, maxX, maxY, maxZ;
    private boolean hasBounds;

    public List<Triangle> getTriangles() {
        return triangles;
    }

    public Polygon(Vector3D position, Triangle[] triangles, Color color, Material material) {
        super(position, color, material);
        setTriangles(triangles);
    }

    public void setTriangles(Triangle[] triangles) {
        Vector3D position = getPosition();
        List<Triangle> translated = new ArrayList<>();
        minX = minY = minZ = Double.POSITIVE_INFINITY;
        maxX = maxY = maxZ = Double.NEGATIVE_INFINITY;
        hasBounds = false;

        if (triangles == null) {
            this.triangles = translated;
            return;
        }

        for (Triangle triangle : triangles) {
            if (triangle == null) {
                continue;
            }
            Vector3D[] srcVerts = triangle.getVertices();
            Vector3D[] dstVerts = new Vector3D[3];
            for (int i = 0; i < 3; i++) {
                Vector3D src = srcVerts[i];
                dstVerts[i] = new Vector3D(
                        src.getX() + position.getX(),
                        src.getY() + position.getY(),
                        src.getZ() + position.getZ()
                );
                expandBounds(dstVerts[i]);
            }

            Vector3D[] srcNormals = triangle.normalsOrNull();
            Vector3D[] dstNormals = null;
            if (srcNormals != null && srcNormals.length == 3) {
                dstNormals = new Vector3D[3];
                for (int i = 0; i < 3; i++) {
                    dstNormals[i] = srcNormals[i] != null ? srcNormals[i].clone() : null;
                }
            }
            translated.add(new Triangle(dstVerts, dstNormals));
        }

        if (hasBounds) {
            final double pad = 1e-4;
            minX -= pad;
            minY -= pad;
            minZ -= pad;
            maxX += pad;
            maxY += pad;
            maxZ += pad;
        }
        this.triangles = translated;
    }

    private void expandBounds(Vector3D vertex) {
        hasBounds = true;
        minX = Math.min(minX, vertex.getX());
        minY = Math.min(minY, vertex.getY());
        minZ = Math.min(minZ, vertex.getZ());
        maxX = Math.max(maxX, vertex.getX());
        maxY = Math.max(maxY, vertex.getY());
        maxZ = Math.max(maxZ, vertex.getZ());
    }

    private boolean intersectsAABB(Ray ray) {
        if (!hasBounds) {
            return true;
        }
        Vector3D origin = ray.getOrigin();
        Vector3D dir = ray.getDirection();
        double[] orig = {origin.getX(), origin.getY(), origin.getZ()};
        double[] direction = {dir.getX(), dir.getY(), dir.getZ()};
        double[] minB = {minX, minY, minZ};
        double[] maxB = {maxX, maxY, maxZ};
        double tMin = 0.0;
        double tMax = Double.POSITIVE_INFINITY;

        for (int i = 0; i < 3; i++) {
            if (Math.abs(direction[i]) < 1e-12) {
                if (orig[i] < minB[i] || orig[i] > maxB[i]) {
                    return false;
                }
                continue;
            }
            double inv = 1.0 / direction[i];
            double t0 = (minB[i] - orig[i]) * inv;
            double t1 = (maxB[i] - orig[i]) * inv;
            if (t0 > t1) {
                double tmp = t0;
                t0 = t1;
                t1 = tmp;
            }
            tMin = Math.max(tMin, t0);
            tMax = Math.min(tMax, t1);
            if (tMin > tMax) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        if (!intersectsAABB(ray)) {
            return null;
        }

        double distance = -1;
        Vector3D normal = Vector3D.ZERO();
        Vector3D position = Vector3D.ZERO();

        for (Triangle triangle : getTriangles()) {
            Intersection intersection = triangle.getIntersection(ray);
            if (intersection == null) {
                continue;
            }
            double intersectionDistance = intersection.getDistance();
            if (intersectionDistance > 0 && (distance < 0 || intersectionDistance < distance)) {
                distance = intersectionDistance;
                position = ray.pointAt(distance);

                normal = Vector3D.ZERO();
                double[] uVw = Barycentric.CalculateBarycentricCoordinates(position, triangle);
                Vector3D[] normals = triangle.getNormals();
                for (int i = 0; i < uVw.length; i++) {
                    if (normals[i] == null) {
                        continue;
                    }
                    normal = Vector3D.add(normal, Vector3D.scalarMultiplication(normals[i], uVw[i]));
                }
            }
        }

        if (distance < 0) {
            return null;
        }

        return new Intersection(position, distance, Vector3D.normalize(normal), this);
    }
}
