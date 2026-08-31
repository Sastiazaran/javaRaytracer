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
public class Camera extends Object3D {

    private float[] fieldOfView = new float[2];
    private float defaultZ = 1f;
    private int[] resolution;
    private float[] nearFarPlanes = new float[2];
    private Vector3D forward = new Vector3D(0, 0, 1);
    private Vector3D right = new Vector3D(1, 0, 0);
    private Vector3D up = new Vector3D(0, 1, 0);

    public Camera(Vector3D position, float fieldOfViewHorizontal, float fieldOfViewVertical, int widthResolution, int heightResolution, float nearPlane, float farPlane, Material material) {
        super(position, Color.black, material);
        setFieldOfViewHorizontal(fieldOfViewHorizontal);
        setFieldOfViewVertical(fieldOfViewVertical);
        setResolution(new int[]{widthResolution, heightResolution});
        setNearFarPlanes(new float[]{nearPlane, farPlane});
    }

    public Camera lookAt(Vector3D target) {
        Vector3D direction = Vector3D.substract(target, getPosition());
        Vector3D worldUp = new Vector3D(0, 1, 0);
        this.forward = Vector3D.normalize(direction);
        Vector3D rightVec = Vector3D.crossProduct(worldUp, this.forward);
        if (Vector3D.magnitude(rightVec) < 1e-8) {
            rightVec = new Vector3D(1, 0, 0);
        }
        this.right = Vector3D.normalize(rightVec);
        this.up = Vector3D.normalize(Vector3D.crossProduct(this.forward, this.right));
        return this;
    }

    public float[] getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(float[] fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    public float getFieldOfViewHorizontal() {
        return fieldOfView[0];
    }

    public void setFieldOfViewHorizontal(float fov) {
        fieldOfView[0] = fov;
    }

    public float getFieldOfViewVertical() {
        return fieldOfView[1];
    }

    public void setFieldOfViewVertical(float fov) {
        fieldOfView[1] = fov;
    }

    public float getDefaultZ() {
        return defaultZ;
    }

    public void setDefaultZ(float defaultZ) {
        this.defaultZ = defaultZ;
    }

    public int[] getResolution() {
        return resolution;
    }

    public void setResolution(int[] resolution) {
        this.resolution = resolution;
    }

    public void setResolution(int width, int height) {
        setResolution(new int[]{width, height});
        if (width > 0 && getFieldOfViewHorizontal() > 0) {
            setFieldOfViewVertical(getFieldOfViewHorizontal() * height / (float) width);
        }
    }

    public int getResolutionWidth() {
        return getResolution()[0];
    }

    public int getResolutionHeight() {
        return getResolution()[1];
    }

    /**
     * Pinhole ray through a continuous pixel coordinate. (0, 0) is the top-left of the image.
     * Pass pixel centers such as (x + 0.5, y + 0.5).
     */
    public Ray rayThrough(double pixelX, double pixelY) {
        double nx = pixelX / (double) getResolutionWidth();
        double ny = pixelY / (double) getResolutionHeight();
        double halfWidth = Math.tan(Math.toRadians(getFieldOfViewHorizontal() * 0.5)) * getDefaultZ();
        double halfHeight = Math.tan(Math.toRadians(getFieldOfViewVertical() * 0.5)) * getDefaultZ();
        double x = (nx * 2.0 - 1.0) * halfWidth;
        double y = (1.0 - ny * 2.0) * halfHeight;
        Vector3D direction = Vector3D.add(
                Vector3D.add(Vector3D.scalarMultiplication(right, x), Vector3D.scalarMultiplication(up, y)),
                Vector3D.scalarMultiplication(forward, getDefaultZ())
        );
        return new Ray(getPosition(), direction);
    }

    public Vector3D[][] calculatePositionsToRay() {
        Vector3D[][] positions = new Vector3D[getResolutionWidth()][getResolutionHeight()];
        for (int x = 0; x < positions.length; x++) {
            for (int y = 0; y < positions[x].length; y++) {
                Ray ray = rayThrough(x + 0.5, y + 0.5);
                Vector3D dir = ray.getDirection();
                positions[x][y] = Vector3D.scalarMultiplication(dir, getDefaultZ() / dir.getZ());
            }
        }
        return positions;
    }

    public float[] getNearFarPlanes() {
        return nearFarPlanes;
    }

    public void setNearFarPlanes(float[] nearFarPlanes) {
        this.nearFarPlanes = nearFarPlanes;
    }

    @Override
    public Intersection getIntersection(Ray ray) {
        return null;
    }
}
