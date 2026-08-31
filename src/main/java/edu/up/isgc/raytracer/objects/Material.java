package edu.up.isgc.raytracer.objects;

public class Material {

    public static final Material DEFAULT = new Material(0.1f, 0.8f, 16f, 0f, 0f);

    private float ambient;
    private float diffuse;
    private float specular;
    private float shininess;
    private float reflection;
    private float refraction;
    private float ior;

    public Material(float ambient, float diffuse, float shininess, float reflection, float refraction) {
        this(ambient, diffuse, shininess, reflection, refraction, 1.5f);
    }

    public Material(float ambient, float diffuse, float shininess, float reflection, float refraction, float ior) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.shininess = shininess;
        this.reflection = reflection;
        this.refraction = refraction;
        this.ior = ior;
        this.specular = shininess > 0f ? 0.4f : 0f;
    }

    public Material specular(float specular) {
        this.specular = specular;
        return this;
    }

    public float getReflection() {
        return reflection;
    }

    public void setReflection(float reflection) {
        this.reflection = reflection;
    }

    public float getRefraction() {
        return refraction;
    }

    public void setRefraction(float refraction) {
        this.refraction = refraction;
    }

    public float getAmbient() {
        return ambient;
    }

    public void setAmbient(float ambient) {
        this.ambient = ambient;
    }

    public float getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(float diffuse) {
        this.diffuse = diffuse;
    }

    public float getShininess() {
        return shininess;
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    public float getSpecular() {
        return specular;
    }

    public void setSpecular(float specular) {
        this.specular = specular;
    }

    public float getIor() {
        return ior;
    }

    public void setIor(float ior) {
        this.ior = ior;
    }
}
