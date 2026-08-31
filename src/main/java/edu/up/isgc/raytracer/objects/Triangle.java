/**
 * [1968] - [2021] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.IIntersectable;
import edu.up.isgc.raytracer.Intersection;
import edu.up.isgc.raytracer.Ray;
import edu.up.isgc.raytracer.Vector3D;

/**
 * @author Jafet Rodríguez
 * @see <a href="https://cadxfem.org/inf/Fast%20MinimumStorage%20RayTriangle%20Intersection.pdf">Moller-Trumbore intersection algorithm</a>
 */
public class Triangle implements IIntersectable {

    public static final double EPSILON = 1e-8;

    private Vector3D[] vertices;
    private Vector3D[] normals;

    public Triangle(Vector3D vertex1, Vector3D vertex2, Vector3D vertex3) {
        setVertices(vertex1, vertex2, vertex3);
        setNormals(null);
    }

    public Triangle(Vector3D vertex1, Vector3D vertex2, Vector3D vertex3, Vector3D normal1, Vector3D normal2, Vector3D normal3) {
        this(vertex1, vertex2, vertex3);
        setNormals(normal1, normal2, normal3);
    }

    public Triangle(Vector3D[] vertices, Vector3D[] normal) {
        if (vertices != null && vertices.length == 3) {
            setVertices(vertices[0], vertices[1], vertices[2]);
        } else {
            setVertices(Vector3D.ZERO(), Vector3D.ZERO(), Vector3D.ZERO());
        }
        setNormals(normal);
    }

    public Vector3D[] getVertices() {
        return vertices;
    }

    public void setVertices(Vector3D vertex1, Vector3D vertex2, Vector3D vertex3) {
        setVertices(new Vector3D[]{vertex1, vertex2, vertex3});
    }

    private void setVertices(Vector3D[] vertices) {
        this.vertices = vertices;
    }

    public Vector3D[] normalsOrNull() {
        return normals;
    }

    public Vector3D[] getNormals() {
        if (normals == null) {
            Vector3D normal = getNormal();
            setNormals(new Vector3D[]{normal, normal, normal});
        }
        return normals;
    }

    public Vector3D getNormal() {
        if (normals == null) {
            Vector3D[] verts = getVertices();
            Vector3D v = Vector3D.substract(verts[1], verts[0]);
            Vector3D w = Vector3D.substract(verts[2], verts[0]);
            return Vector3D.normalize(Vector3D.crossProduct(v, w));
        }

        Vector3D normal = Vector3D.ZERO();
        for (Vector3D n : normals) {
            if (n == null) {
                continue;
            }
            normal.setX(normal.getX() + n.getX());
            normal.setY(normal.getY() + n.getY());
            normal.setZ(normal.getZ() + n.getZ());
        }
        return Vector3D.normalize(normal);
    }

    public void setNormals(Vector3D[] normals) {
        this.normals = normals;
    }

    public void setNormals(Vector3D normal1, Vector3D normal2, Vector3D normal3) {
        setNormals(new Vector3D[]{normal1, normal2, normal3});
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        Vector3D[] verts = getVertices();
        Vector3D v0v1 = Vector3D.substract(verts[1], verts[0]);
        Vector3D v0v2 = Vector3D.substract(verts[2], verts[0]);
        Vector3D pVec = Vector3D.crossProduct(ray.getDirection(), v0v2);
        double determinant = Vector3D.dotProduct(v0v1, pVec);
        if (Math.abs(determinant) < EPSILON) {
            return null;
        }

        double invDet = 1.0 / determinant;
        Vector3D tVec = Vector3D.substract(ray.getOrigin(), verts[0]);
        double u = Vector3D.dotProduct(tVec, pVec) * invDet;
        if (u < 0 || u > 1) {
            return null;
        }

        Vector3D qVec = Vector3D.crossProduct(tVec, v0v1);
        double v = Vector3D.dotProduct(ray.getDirection(), qVec) * invDet;
        if (v < 0 || (u + v) > 1.0 + EPSILON) {
            return null;
        }

        double t = Vector3D.dotProduct(v0v2, qVec) * invDet;
        if (t < EPSILON) {
            return null;
        }

        return new Intersection(null, t, null, null);
    }
}
