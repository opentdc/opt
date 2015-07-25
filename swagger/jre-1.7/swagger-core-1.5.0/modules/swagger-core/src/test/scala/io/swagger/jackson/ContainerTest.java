package io.swagger.jackson;


import io.swagger.converter.ModelConverterContextImpl;
import io.swagger.models.Model;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;

import java.util.Map;

public class ContainerTest extends SwaggerTestBase {
    public void testArray() throws Exception {
        ModelResolver modelResolver = new ModelResolver(mapper());

        ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);

        Model model = context
                .resolve(ArrayBean.class);

        Map<String, Property> props = model.getProperties();
        assertEquals(1, props.size());
        Property prop = props.get("a");
        assertNotNull(prop);
        assertEquals("array", prop.getType());
//    assertEquals("[I", prop.getQualifiedType());

        Property items = ((ArrayProperty) prop).getItems();
        assertNotNull(items);
        assertEquals("integer", items.getType());
        // assertEquals("int", items.getQualifiedType());*/
    }

    public void testMap() throws Exception {
        ModelResolver modelResolver = new ModelResolver(mapper());
        ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);

        Model model = context
                .resolve(MapBean.class);

        Map<String, Property> props = model.getProperties();
        assertEquals(1, props.size());
        Property prop = props.get("stuff");
        assertNotNull(prop);
//    assertEquals("Map[integer,dateTime]", prop.getType());
        // assertEquals("object", prop.getType());
        // assertEquals("java.util.Map", prop.getQualifiedType());

        // Property items = ((MapProperty)prop).getAdditionalProperties();
        // assertNotNull(items);
        // assertEquals("java.util.Calendar", items.getQualifiedType());
    }

    public void testComplexMap() throws Exception {
//    Map<String, InnerType> test = new HashMap<String, InnerType>();
        ModelResolver resolver = new ModelResolver(mapper());

        ModelConverterContextImpl context = new ModelConverterContextImpl(resolver);
        context.resolve(WrapperType.class);

        for (String key : context.getDefinedModels().keySet()) {
            Model model = context.getDefinedModels().get(key);
            assertNotNull(model);
            // !!! TODO: verify
        }
    }

    static class ArrayBean {
        public int[] a;
    }

    static class MapBean {
        public Map<Short, java.util.Calendar> stuff;
    }

    static class WrapperType {
        public Map<String, InnerType> innerType;
    }
}
