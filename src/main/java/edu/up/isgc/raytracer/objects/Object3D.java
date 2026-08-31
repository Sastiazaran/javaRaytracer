/**
 * [1968] - [2021] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer.objects;

import edu.up.isgc.raytracer.IIntersectable;
import edu.up.isgc.raytracer.Vector3D;

import java.awt.Color;

/**
 * @author Jafet Rodríguez
 */
public abstract class Object3D implements IIntersectable {

    private Vector3D position;
    private Color color;
    private Material material;

    public Material getMaterial() {
        return material != null ? material : Material.DEFAULT;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Vector3D getPosition() {
        return position;
    }

    public void setPosition(Vector3D position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    public Color getColorAt(Vector3D point) {
        return getColor();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Object3D(Vector3D position, Color color, Material material) {
        setPosition(position);
        setColor(color);
        setMaterial(material);
    }
}
