package lulewiczg.contentserver.utils.json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.models.TestModel;
import lulewiczg.contentserver.models.TestModelCollections;
import lulewiczg.contentserver.models.TestModelCollectionsEmpty;
import lulewiczg.contentserver.models.TestModelCollision;
import lulewiczg.contentserver.models.TestModelEmpty;
import lulewiczg.contentserver.models.TestModelNoFields;
import lulewiczg.contentserver.test.utils.TestUtil;

/**
 * Tests JSON serialization
 *
 * @author lulewiczg
 */
public class JSONModelTest {

    @Test
    @DisplayName("Empty model serialization")
    public void testEmptyModel() throws IOException {
        String json = new TestModelEmpty().toJSON();
        String expected = loadJSON("emptyModel.json");
        Assertions.assertEquals(new JSONObject(expected).toString(), new JSONObject(json).toString());
    }

    @Test
    @DisplayName("Model serialization")
    public void testModel() throws IOException {
        String json = new TestModel().toJSON();
        String expected = loadJSON("model.json");
        Assertions.assertEquals(new JSONObject(expected).toString(), new JSONObject(json).toString());
    }

    @Test
    @DisplayName("Model with collisions serialization")
    public void testModelwithcollisions() throws JSONException {
        Exception e = Assertions.assertThrows(JSONException.class, () -> new TestModelCollision().toJSON());
        Assertions.assertEquals("Duplicated fields found!", e.getMessage());
    }

    @Test
    @DisplayName("Model with collections serialization")
    public void testCollectionsModels() throws IOException {
        String json = new TestModelCollections().toJSON();
        String expected = loadJSON("collectionsModel.json");
        Assertions.assertEquals(new JSONObject(expected).toString(), new JSONObject(json).toString());
    }

    @Test
    @DisplayName("Model with empty collections serialization")
    public void testEmptyCollectionsModels() throws IOException {
        String json = new TestModelCollectionsEmpty().toJSON();
        String expected = loadJSON("emptyCollectionsModel.json");
        Assertions.assertEquals(new JSONObject(expected).toString(), new JSONObject(json).toString());
    }

    @Test
    @DisplayName("Array with primitives")
    public void testPrimitiveArrays() throws IOException {
        int[] data = new int[] { 1, 2, 3, 5, 7 };
        String jsonArray = JSONModel.toJSONArray(Arrays.stream(data).boxed().collect(Collectors.toList()));
        Assertions.assertEquals(new JSONArray(data).toString(), new JSONArray(jsonArray).toString());
    }

    @Test
    @DisplayName("Array with Strings")
    public void testStringArrays() throws IOException {
        String[] data = new String[] { "a", "\n\t as,dasda \" asd'' a'das", "ggg" };
        String jsonArray = JSONModel.toJSONArray(Arrays.asList(data));
        Assertions.assertEquals(new JSONArray(data).toString(), new JSONArray(jsonArray).toString());
    }

    @Test
    @DisplayName("Array with JSON objects")
    public void testJSONObjectArrays() throws IOException {
        String json = new TestModel().toJSON();
        String data = String.format("[%s, %s, %s]", json, json, json);
        String jsonArray = JSONModel.toJSONArray(Arrays.asList(new TestModel(), new TestModel(), new TestModel()));
        Assertions.assertEquals(new JSONArray(data).toString(), new JSONArray(jsonArray).toString());
    }

    @Test
    @DisplayName("JSON object without fields")
    public void testJSONEmptyModel() throws IOException {
        String json = new TestModelNoFields().toJSON();
        Assertions.assertEquals("{\n}", json);
    }

    /**
     * Loads JSON from file
     *
     * @param path
     *            path
     * @return json string
     * @throws IOException
     *             the IOException
     */
    private String loadJSON(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(TestUtil.LOC + "jsons/" + path));
        return new String(bytes);
    }
}
