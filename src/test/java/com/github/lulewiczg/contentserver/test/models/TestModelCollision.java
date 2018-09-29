package com.github.lulewiczg.contentserver.test.models;

import com.github.lulewiczg.contentserver.utils.json.JSONModel;
import com.github.lulewiczg.contentserver.utils.json.JSONProperty;

public class TestModelCollision extends JSONModel<TestModelCollision> {

    @JSONProperty(propertyName = "int1")
    private int prop1;

    @JSONProperty(propertyName = "int1")
    private Integer prop2;

    public int getProp1() {
        return prop1;
    }

    public Integer getProp2() {
        return prop2;
    }

}
