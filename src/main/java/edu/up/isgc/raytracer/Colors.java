package edu.up.isgc.raytracer;

import java.awt.Color;

/**
 * Linear RGB helpers. Values are accumulated as 0..n floats and clamped when converted to AWT colors.
 */
public final class Colors {

    private Colors() {
    }

    public static float clamp(float value) {
        if (value < 0f) {
            return 0f;
        }
        if (value > 1f) {
            return 1f;
        }
        return value;
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

    public static float[] from(Color color) {
        return new float[]{
                color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f
        };
    }

    public static void addScaled(float[] dest, float[] source, float scale) {
        dest[0] += source[0] * scale;
        dest[1] += source[1] * scale;
        dest[2] += source[2] * scale;
    }

    public static void scale(float[] dest, float scale) {
        dest[0] *= scale;
        dest[1] *= scale;
        dest[2] *= scale;
    }

    public static Color toColor(float[] rgb) {
        return new Color(clamp(rgb[0]), clamp(rgb[1]), clamp(rgb[2]));
    }

    public static Color lerp(Color a, Color b, double t) {
        t = Math.min(1.0, Math.max(0.0, t));
        float[] ca = from(a);
        float[] cb = from(b);
        return new Color(
                clamp((float) (ca[0] + (cb[0] - ca[0]) * t)),
                clamp((float) (ca[1] + (cb[1] - ca[1]) * t)),
                clamp((float) (ca[2] + (cb[2] - ca[2]) * t))
        );
    }
}
