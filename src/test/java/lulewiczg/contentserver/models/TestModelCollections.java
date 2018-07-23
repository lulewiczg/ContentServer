package lulewiczg.contentserver.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lulewiczg.contentserver.utils.json.JSONModel;
import lulewiczg.contentserver.utils.json.JSONProperty;

public class TestModelCollections extends JSONModel<TestModelCollections> {

    @JSONProperty(propertyName = "intsArr")
    private int[] prop1 = new int[] { 1, 2, 3 };

    @JSONProperty(propertyName = "strArr")
    private String[] prop2 = new String[] { "a", "b", "c" };

    @JSONProperty(propertyName = "intList")
    private List<Integer> prop3 = new ArrayList<>(Arrays.asList(4, 5, 6));

    @JSONProperty(propertyName = "strList")
    private List<String> prop4 = new ArrayList<>(Arrays.asList("d", "e", "f"));

    @JSONProperty(propertyName = "map")
    private Map<String, Integer> prop5 = new HashMap<>();

    public TestModelCollections() {
        // TODO
//        prop5.put("ab", 11);
//        prop5.put("cd", 22);
    }

    public int[] getProp1() {
        return prop1;
    }

    public String[] getProp2() {
        return prop2;
    }

    public List<Integer> getProp3() {
        return prop3;
    }

    public List<String> getProp4() {
        return prop4;
    }

    public Map<String, Integer> getProp5() {
        return prop5;
    }

}
