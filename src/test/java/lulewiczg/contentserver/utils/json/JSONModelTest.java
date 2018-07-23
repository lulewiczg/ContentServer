package lulewiczg.contentserver.utils.json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lulewiczg.contentserver.models.TestModel;
import lulewiczg.contentserver.models.TestModelCollections;
import lulewiczg.contentserver.models.TestModelCollectionsEmpty;
import lulewiczg.contentserver.models.TestModelCollision;
import lulewiczg.contentserver.models.TestModelEmpty;

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
        Assert.assertEquals(new JSONObject(expected).toString(), new JSONObject(json).toString());
    }

    @Test
    @DisplayName("Model serialization")
    public void testModel() throws IOException {
        String json = new TestModel().toJSON();
        String expected = loadJSON("model.json");
        Assert.assertEquals(new JSONObject(expected).toString(), new JSONObject(json).toString());
    }

    @Test
    @DisplayName("Model with collisions serialization")
    public void testModelwithcollisions() throws JSONException {
        Assertions.assertThrows(JSONException.class, () -> new TestModelCollision().toJSON(),
                "Duplicated fields found!");
    }

    @Test
    @DisplayName("Model with collections serialization")
    public void testCollectionsModels() throws IOException {
        String json = new TestModelCollections().toJSON();
        String expected = loadJSON("collectionsModel.json");
        Assert.assertEquals(new JSONObject(expected).toString(), new JSONObject(json).toString());
    }

    @Test
    @DisplayName("Model with empty collections serialization")
    public void testEmptyCollectionsModels() throws IOException {
        String json = new TestModelCollectionsEmpty().toJSON();
        String expected = loadJSON("emptyCollectionsModel.json");
        Assert.assertEquals(new JSONObject(expected).toString(), new JSONObject(json).toString());
    }

    @Test
    @DisplayName("Array with primitives")
    public void testPrimitiveArrays() throws IOException {
        int[] data = new int[] { 1, 2, 3, 5, 7 };
        String jsonArray = JSONModel.toJSONArray(Arrays.stream(data).boxed().collect(Collectors.toList()));
        Assert.assertEquals(new JSONArray(data).toString(), new JSONArray(jsonArray).toString());
    }

    @Test
    @DisplayName("Array with Strings")
    public void testStringArrays() throws IOException {
        String[] data = new String[] { "a", "\n\t as,dasda \" asd'' a'das", "ggg" };
        String jsonArray = JSONModel.toJSONArray(Arrays.asList(data));
        Assert.assertEquals(new JSONArray(data).toString(), new JSONArray(jsonArray).toString());
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
        byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/jsons/" + path));
        return new String(bytes);
    }
}
