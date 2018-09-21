package com.github.lulewiczg.contentserver.models;

import com.github.lulewiczg.contentserver.utils.json.JSONModel;
import com.github.lulewiczg.contentserver.utils.json.JSONProperty;

public class TestModelEmpty extends JSONModel<TestModelEmpty> {

    @JSONProperty(propertyName = "int1")
    private int prop1;

    @JSONProperty(propertyName = "int2")
    private Integer prop2;

    @JSONProperty(propertyName = "string")
    private String prop3;

    @JSONProperty(propertyName = "bool")
    private boolean prop4;

    @JSONProperty(propertyName = "byte")
    private byte prop5;

    @JSONProperty(propertyName = "inner")
    private TestModelInner inner = new TestModelInner();

    public int getProp1() {
        return prop1;
    }

    public Integer getProp2() {
        return prop2;
    }

    public String getProp3() {
        return prop3;
    }

    public boolean isProp4() {
        return prop4;
    }

    public byte getProp5() {
        return prop5;
    }

    public TestModelInner getInner() {
        return inner;
    }

    public class TestModelInner extends JSONModel<TestModelInner> {

        @JSONProperty(propertyName = "innerStr")
        private String innerStr;

        @JSONProperty(propertyName = "innerInt")
        private int innerInt;

        public String getInnerStr() {
            return innerStr;
        }

        public int getInnerInt() {
            return innerInt;
        }
    }
}
