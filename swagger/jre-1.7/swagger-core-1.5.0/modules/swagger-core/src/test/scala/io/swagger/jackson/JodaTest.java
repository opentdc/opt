package io.swagger.jackson;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContextImpl;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import org.joda.time.DateTime;

import java.util.Map;


public class JodaTest extends SwaggerTestBase {
    public void testSimple() throws Exception {
        final ModelConverter mr = modelResolver();
        Model model = mr.resolve(ModelWithJodaDateTime.class, new ModelConverterContextImpl(mr), null);
        assertNotNull(model);

        Map<String, Property> props = model.getProperties();
        assertEquals(2, props.size());

        for (Map.Entry<String, Property> entry : props.entrySet()) {
            String name = entry.getKey();
            Property prop = entry.getValue();

            if ("name".equals(name)) {
                assertEquals("string", prop.getType());
            } else if ("createdAt".equals(name)) {
                assertEquals("string", prop.getType());
                assertEquals("date-time", prop.getFormat());
            } else {
                fail("Unknown property '" + name + "'");
            }
        }
    }

    static class ModelWithJodaDateTime {
        @ApiModelProperty(value = "Name!", position = 2)
        public String name;

        @ApiModelProperty(value = "creation timestamp", required = true, position = 1)
        public DateTime createdAt;
    }
}
