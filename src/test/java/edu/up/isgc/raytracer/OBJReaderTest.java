package edu.up.isgc.raytracer;

import edu.up.isgc.raytracer.objects.Material;
import edu.up.isgc.raytracer.objects.Polygon;
import edu.up.isgc.raytracer.tools.OBJReader;
import org.junit.jupiter.api.Test;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OBJReaderTest {

    @Test
    void loadsCubeWithVertexNormals() {
        Polygon cube = OBJReader.GetPolygon("Cube.obj", Vector3D.ZERO(), Color.WHITE, Material.DEFAULT);
        assertNotNull(cube, "Cube.obj should load");
        assertFalse(cube.getTriangles().isEmpty());
        assertTrue(cube.getTriangles().size() >= 12);
    }

    @Test
    void loadsCubeQuadWithoutNormals() {
        Polygon cube = OBJReader.GetPolygon("CubeQuad.obj", Vector3D.ZERO(), Color.GREEN, Material.DEFAULT, 0.5);
        assertNotNull(cube, "CubeQuad.obj has no vn entries and used to throw");
        assertFalse(cube.getTriangles().isEmpty());
        assertTrue(cube.getTriangles().size() >= 12);
    }

    @Test
    void missingFileReturnsNull() {
        assertTrue(OBJReader.GetPolygon("does-not-exist.obj", Vector3D.ZERO(), Color.WHITE, Material.DEFAULT) == null);
    }
}
