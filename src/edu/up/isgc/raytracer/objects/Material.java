package edu.up.isgc.raytracer.objects;

public class Material
{
    private float Ambient;
    private float Diffuse;
    private float Shininess;
    private float Reflection;
    private float Refraction;

    public Material(float ambient, float diffuse, float shininess, float reflection, float refraction) {
        Ambient = ambient;
        Diffuse = diffuse;
        Shininess = shininess;
        Reflection = reflection;
        Refraction = refraction;
    }

    public float getReflection() {
        return Reflection;
    }

    public void setReflection(float reflection) {
        Reflection = reflection;
    }

    public float getRefraction() {
        return Refraction;
    }

    public void setRefraction(float refraction) {
        Refraction = refraction;
    }

    public float getAmbient() {
        return Ambient;
    }

    public void setAmbient(float ambient) {
        Ambient = ambient;
    }

    public float getDiffuse() {
        return Diffuse;
    }

    public void setDiffuse(float diffuse) {
        Diffuse = diffuse;
    }

    public float getShininess() {
        return Shininess;
    }

    public void setShininess(float shininess) {
        Shininess = shininess;
    }

}

