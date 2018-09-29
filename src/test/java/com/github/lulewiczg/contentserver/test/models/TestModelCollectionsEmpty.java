package com.github.lulewiczg.contentserver.test.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.lulewiczg.contentserver.utils.json.JSONModel;
import com.github.lulewiczg.contentserver.utils.json.JSONProperty;

public class TestModelCollectionsEmpty extends JSONModel<TestModelCollectionsEmpty> {

    @JSONProperty(propertyName = "intsArr")
    private int[] prop1 = new int[] {};

    @JSONProperty(propertyName = "strArr")
    private String[] prop2 = new String[] {};

    @JSONProperty(propertyName = "intList")
    private List<Integer> prop3 = new ArrayList<>();

    @JSONProperty(propertyName = "strList")
    private List<String> prop4 = new ArrayList<>();

    @JSONProperty(propertyName = "map")
    private Map<String, Integer> prop5 = new HashMap<>();

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
