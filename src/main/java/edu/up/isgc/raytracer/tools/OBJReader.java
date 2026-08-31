/**
 * [1968] - [2021] Centros Culturales de Mexico A.C / Universidad Panamericana
 * All Rights Reserved.
 */
package edu.up.isgc.raytracer.tools;

import edu.up.isgc.raytracer.Vector3D;
import edu.up.isgc.raytracer.objects.Material;
import edu.up.isgc.raytracer.objects.Polygon;
import edu.up.isgc.raytracer.objects.Triangle;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jafet Rodríguez
 */
public abstract class OBJReader {

    public static Polygon GetPolygon(String path, Vector3D origin, Color color, Material material) {
        return GetPolygon(path, origin, color, material, 1.0);
    }

    public static Polygon GetPolygon(String path, Vector3D origin, Color color, Material material, double scale) {
        try (BufferedReader reader = open(path)) {
            List<Triangle> triangles = new ArrayList<>();
            List<Vector3D> vertices = new ArrayList<>();
            List<Vector3D> normals = new ArrayList<>();
            String line;
            int defaultSmoothingGroup = -1;
            int smoothingGroup = defaultSmoothingGroup;
            Map<Integer, List<Triangle>> smoothingMap = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("v ")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 4) {
                        double x = Double.parseDouble(parts[1]) * scale;
                        double y = Double.parseDouble(parts[2]) * scale;
                        double z = Double.parseDouble(parts[3]) * scale;
                        vertices.add(new Vector3D(x, y, z));
                    }
                } else if (line.startsWith("vn ")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length >= 4) {
                        normals.add(Vector3D.normalize(new Vector3D(
                                Double.parseDouble(parts[1]),
                                Double.parseDouble(parts[2]),
                                Double.parseDouble(parts[3])
                        )));
                    }
                } else if (line.startsWith("f ")) {
                    String[] faceComponents = line.split("\\s+");
                    List<Integer> faceVertex = new ArrayList<>();
                    List<Integer> faceNormal = new ArrayList<>();

                    for (int i = 1; i < faceComponents.length; i++) {
                        if (faceComponents[i].isEmpty()) {
                            continue;
                        }
                        String[] infoVertex = faceComponents[i].split("/", -1);
                        int vertexIndex = resolveIndex(Integer.parseInt(infoVertex[0]), vertices.size());
                        faceVertex.add(vertexIndex);
                        if (infoVertex.length >= 3 && !infoVertex[2].isEmpty()) {
                            int normalIndex = resolveIndex(Integer.parseInt(infoVertex[2]), normals.size());
                            faceNormal.add(normalIndex);
                        } else {
                            faceNormal.add(-1);
                        }
                    }

                    if (faceVertex.size() >= 3) {
                        Vector3D[] polygonVerts = new Vector3D[faceVertex.size()];
                        Vector3D[] polygonNormals = new Vector3D[faceVertex.size()];
                        boolean hasNormals = !normals.isEmpty();
                        for (int i = 0; i < faceVertex.size(); i++) {
                            polygonVerts[i] = vertices.get(faceVertex.get(i));
                            int nIdx = faceNormal.get(i);
                            if (hasNormals && nIdx >= 0 && nIdx < normals.size()) {
                                polygonNormals[i] = normals.get(nIdx);
                            }
                        }

                        for (int i = 1; i < faceVertex.size() - 1; i++) {
                            Vector3D[] triVerts = new Vector3D[]{polygonVerts[0], polygonVerts[i], polygonVerts[i + 1]};
                            Vector3D[] triNormals = null;
                            if (hasValidNormals(polygonNormals, 0, i, i + 1)) {
                                triNormals = new Vector3D[]{polygonNormals[0], polygonNormals[i], polygonNormals[i + 1]};
                            }
                            Triangle tmpTriangle = new Triangle(triVerts, triNormals);
                            triangles.add(tmpTriangle);

                            if (smoothingGroup != defaultSmoothingGroup) {
                                smoothingMap.computeIfAbsent(smoothingGroup, key -> new ArrayList<>()).add(tmpTriangle);
                            }
                        }
                    }
                } else if (line.startsWith("s ")) {
                    String[] smoothingComponents = line.split("\\s+");
                    if (smoothingComponents.length > 1) {
                        if (smoothingComponents[1].equals("off")) {
                            smoothingGroup = defaultSmoothingGroup;
                        } else {
                            try {
                                smoothingGroup = Integer.parseInt(smoothingComponents[1]);
                            } catch (NumberFormatException nfe) {
                                smoothingGroup = defaultSmoothingGroup;
                            }
                        }
                    }
                }
            }

            class NormalPair {
                Vector3D normal = Vector3D.ZERO();
                int count;
            }

            for (Integer key : smoothingMap.keySet()) {
                Map<Vector3D, NormalPair> vertexMap = new HashMap<>();
                List<Triangle> trianglesInMap = smoothingMap.get(key);
                for (Triangle triangle : trianglesInMap) {
                    Vector3D[] triangleVertices = triangle.getVertices();
                    Vector3D[] triangleNormals = triangle.getNormals();
                    for (int i = 0; i < triangleVertices.length; i++) {
                        NormalPair normalsVertex = vertexMap.get(triangleVertices[i]);
                        if (normalsVertex == null) {
                            normalsVertex = new NormalPair();
                            vertexMap.put(triangleVertices[i], normalsVertex);
                        }
                        if (triangleNormals.length > 0 && i < triangleNormals.length && triangleNormals[i] != null) {
                            normalsVertex.normal = Vector3D.add(normalsVertex.normal, triangleNormals[i]);
                            normalsVertex.count++;
                        }
                    }
                }
                for (Triangle triangle : trianglesInMap) {
                    Vector3D[] triangleVertices = triangle.getVertices();
                    Vector3D[] triangleNormals = triangle.getNormals();
                    for (int i = 0; i < triangleVertices.length; i++) {
                        NormalPair normalsVertex = vertexMap.get(triangleVertices[i]);
                        if (normalsVertex != null && normalsVertex.count > 0) {
                            triangleNormals[i] = Vector3D.scalarMultiplication(normalsVertex.normal, 1.0 / normalsVertex.count);
                        }
                    }
                    triangle.setNormals(triangleNormals);
                }
            }

            if (triangles.isEmpty()) {
                Logger.getLogger(OBJReader.class.getName()).log(Level.WARNING, "No faces found in {0}", path);
                return null;
            }
            return new Polygon(origin, triangles.toArray(new Triangle[0]), color, material);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OBJReader.class.getName()).log(Level.WARNING, "OBJ not found: {0}", path);
        } catch (IOException | RuntimeException ex) {
            Logger.getLogger(OBJReader.class.getName()).log(Level.SEVERE, "Failed to read OBJ: " + path, ex);
        }
        return null;
    }

    private static boolean hasValidNormals(Vector3D[] normals, int... indices) {
        for (int index : indices) {
            if (index < 0 || index >= normals.length || normals[index] == null) {
                return false;
            }
        }
        return true;
    }

    private static int resolveIndex(int index, int size) {
        if (index < 0) {
            return size + index;
        }
        return index - 1;
    }

    private static BufferedReader open(String path) throws IOException {
        File file = new File(path);
        if (file.isFile()) {
            return new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        }
        InputStream stream = OBJReader.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            stream = OBJReader.class.getClassLoader().getResourceAsStream("models/" + path);
        }
        if (stream == null) {
            throw new FileNotFoundException(path);
        }
        return new BufferedReader(new InputStreamReader(stream));
    }
}
