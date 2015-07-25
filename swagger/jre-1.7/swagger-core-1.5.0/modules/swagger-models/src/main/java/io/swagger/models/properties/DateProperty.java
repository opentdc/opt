package io.swagger.models.properties;

import io.swagger.models.Xml;

public class DateProperty extends AbstractProperty implements Property {
    public DateProperty() {
        super.type = "string";
        super.format = "date";
    }

    public static boolean isType(String type, String format) {
        if ("string".equals(type) && "date".equals(format)) {
            return true;
        } else {
            return false;
        }
    }

    public DateProperty xml(Xml xml) {
        this.setXml(xml);
        return this;
    }

    public DateProperty example(String example) {
        this.setExample(example);
        return this;
    }
}