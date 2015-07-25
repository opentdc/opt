import io.swagger.models.properties._
import io.swagger.util.Json
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.junit.JUnitRunner

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class PropertySerializationTest extends FlatSpec with Matchers {
  val m = Json.mapper()

  it should "serialize a BooleanProperty" in {
    val p = new BooleanProperty()
      ._default(true)

    m.writeValueAsString(p) should be( """{"type":"boolean","default":true}""")
  }

  it should "deserialize a BooleanProperty" in {
    val json = """{"type":"boolean","default":false}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("boolean")
    p.getFormat should be(null)
    p.getClass should be(classOf[BooleanProperty])
    p.asInstanceOf[BooleanProperty].getDefault should equal(false)
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a DateProperty" in {
    val p = new DateProperty()
    m.writeValueAsString(p) should be( """{"type":"string","format":"date"}""")
  }

  it should "deserialize a DateProperty" in {
    val json = """{"type":"string","format":"date"}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("string")
    p.getFormat should be("date")
    p.getClass should be(classOf[DateProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a DateTimeProperty" in {
    val p = new DateTimeProperty()
    m.writeValueAsString(p) should be( """{"type":"string","format":"date-time"}""")
  }

  it should "deserialize a DateTimeProperty" in {
    val json = """{"type":"string","format":"date-time"}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("string")
    p.getFormat should be("date-time")
    p.getClass should be(classOf[DateTimeProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a DoubleProperty" in {
    val p = new DoubleProperty()
      ._default(3.14159)
    m.writeValueAsString(p) should be( """{"type":"number","format":"double","default":3.14159}""")
  }

  it should "deserialize a DoubleProperty" in {
    val json = """{"type":"number","format":"double"}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("number")
    p.getFormat should be("double")
    p.getClass should be(classOf[DoubleProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a FloatProperty" in {
    val p = new FloatProperty()
      ._default(1.2f)
    m.writeValueAsString(p) should be( """{"type":"number","format":"float","default":1.2}""")
  }

  it should "deserialize a FloatProperty" in {
    val json = """{"type":"number","format":"float"}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("number")
    p.getFormat should be("float")
    p.getClass should be(classOf[FloatProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize an IntegerProperty" in {
    val p = new IntegerProperty()
      ._default(32)
    m.writeValueAsString(p) should be( """{"type":"integer","format":"int32","default":32}""")
  }

  it should "deserialize a IntegerProperty" in {
    val json = """{"type":"integer","format":"int32"}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("integer")
    p.getFormat should be("int32")
    p.getClass should be(classOf[IntegerProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a LongProperty" in {
    val p = new LongProperty()
      ._default(8675309L)
    m.writeValueAsString(p) should be( """{"type":"integer","format":"int64","default":8675309}""")
  }

  it should "deserialize a LongProperty" in {
    val json = """{"type":"integer","format":"int64"}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("integer")
    p.getFormat should be("int64")
    p.getClass should be(classOf[LongProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a string MapProperty" in {
    val p = new MapProperty(new StringProperty())
    m.writeValueAsString(p) should be( """{"type":"object","additionalProperties":{"type":"string"}}""")
  }

  it should "deserialize a string MapProperty" in {
    val json = """{"type":"object","additionalProperties":{"type":"string"}}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("object")
    p.getClass should be(classOf[MapProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a integer MapProperty" in {
    val p = new MapProperty(new IntegerProperty())
    m.writeValueAsString(p) should be( """{"type":"object","additionalProperties":{"type":"integer","format":"int32"}}""")
  }

  it should "deserialize a integer MapProperty" in {
    val json = """{"type":"object","additionalProperties":{"type":"integer","format":"int32"}}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("object")
    p.getClass should be(classOf[MapProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a long MapProperty" in {
    val p = new MapProperty(new LongProperty())
    m.writeValueAsString(p) should be( """{"type":"object","additionalProperties":{"type":"integer","format":"int64"}}""")
  }

  it should "deserialize a long MapProperty" in {
    val json = """{"type":"object","additionalProperties":{"type":"integer","format":"int64"}}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("object")
    p.getClass should be(classOf[MapProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a RefProperty" in {
    val p = new RefProperty("Dog")
    m.writeValueAsString(p) should be( """{"$ref":"#/definitions/Dog"}""")
  }

  it should "deserialize a RefProperty" in {
    val json = """{"$ref":"#/definitions/Dog"}"""
    val p = m.readValue(json, classOf[Property])
    p.getClass should be(classOf[RefProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a StringProperty" in {
    val p = new StringProperty()
      ._default("Bob")
    m.writeValueAsString(p) should be( """{"type":"string","default":"Bob"}""")
  }

  it should "deserialize a StringProperty" in {
    val json = """{"type":"string"}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("string")
    p.getClass should be(classOf[StringProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a StringProperty with enums" in {
    val p = new StringProperty()._enum("a")._enum("b")
    m.writeValueAsString(p) should be( """{"type":"string","enum":["a","b"]}""")
  }

  it should "deserialize a StringProperty with enums" in {
    val json = """{"type":"string","enum":["a","b"]}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("string")
    val _enum = (p.asInstanceOf[StringProperty]).getEnum
    _enum should not be (null)
    _enum.asScala.toSet should equal(Set("a", "b"))
    p.getClass should be(classOf[StringProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a string array property" in {
    val p = new ArrayProperty().items(new StringProperty())
    m.writeValueAsString(p) should equal( """{"type":"array","items":{"type":"string"}}""")
  }

  it should "deserialize a string array property" in {
    val json = """{"type":"array","items":{"type":"string"}}"""
    val p = m.readValue(json, classOf[Property])
    p.getType should be("array")
    p.getClass should be(classOf[ArrayProperty])
    m.writeValueAsString(p) should equal(json)
  }

  it should "serialize a string property with readOnly set" in {
    val p = new StringProperty().readOnly()
    m.writeValueAsString(p) should equal( """{"type":"string","readOnly":true}""")
  }

  it should "serialize a string property with readOnly unset" in {
    val p = new StringProperty()
    p.setReadOnly(false)
    m.writeValueAsString(p) should equal( """{"type":"string"}""")
  }

  /*
    it should "read a file property" in {
      val json = """{"type":"File"}"""
      val p = m.readValue(json, classOf[Property])
      p.getType should be ("File")
      p.getClass should be (classOf[FileProperty])
      m.writeValueAsString(p) should equal (json)
    }
  */
}