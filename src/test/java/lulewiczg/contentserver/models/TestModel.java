package lulewiczg.contentserver.models;

import lulewiczg.contentserver.utils.json.JSONModel;
import lulewiczg.contentserver.utils.json.JSONProperty;

/**
 * Test model.
 * 
 * @author lulewiczg
 */
public class TestModel extends JSONModel<TestModel> {

    @JSONProperty(propertyName = "int1")
    private int prop1 = 123;

    @JSONProperty(propertyName = "int2")
    private Integer prop2 = 321;

    @JSONProperty(propertyName = "string")
    private String prop3 = "asdasd'    asd 'asdadsad\"\t\nsdfsdf\"123123";

    @JSONProperty(propertyName = "bool")
    private boolean prop4 = true;

    @JSONProperty(propertyName = "byte")
    private byte prop5 = 127;

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
        private String innerStr = "inner";

        @JSONProperty(propertyName = "innerInt")
        private int innerInt = 999;

        public String getInnerStr() {
            return innerStr;
        }

        public int getInnerInt() {
            return innerInt;
        }
    }
}
