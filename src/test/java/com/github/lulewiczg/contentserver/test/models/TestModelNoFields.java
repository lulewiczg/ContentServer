package com.github.lulewiczg.contentserver.test.models;

import com.github.lulewiczg.contentserver.utils.json.JSONModel;

/**
 * Test model with no serializable fields.
 * 
 * @author lulewiczg
 */
public class TestModelNoFields extends JSONModel<TestModelNoFields> {

    private int prop1 = 123;

    private Integer prop2 = 321;

    public int getProp1() {
        return prop1;
    }

    public Integer getProp2() {
        return prop2;
    }

}
