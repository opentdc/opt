import javax.xml.bind.annotation._

import io.swagger.converter.ModelConverters
import io.swagger.models.ModelImpl
import io.swagger.util.Json
import models._
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.junit.JUnitRunner

import scala.beans.BeanProperty

@RunWith(classOf[JUnitRunner])
class XmlModelTest extends FlatSpec with Matchers {
  it should "process an XML model attribute" in {
    val schemas = ModelConverters.getInstance().readAll(classOf[Monster])
    val model = schemas.get("Monster")

    model should not be (null)
    model.isInstanceOf[ModelImpl] should be(true)
    var xml = model.asInstanceOf[ModelImpl].getXml()

    xml should not be (null)
    xml.getName() should equal("monster")
    val property = model.getProperties().get("children")
    property should not be (null)
    xml = property.getXml()
    xml.getWrapped should equal(true)
    xml.getName() should be("children")
  }

  it should "not create an xml object" in {
    val schemas = ModelConverters.getInstance().readAll(classOf[Address])
    val model = schemas.get("Address")

    model should not be (null)
    model.isInstanceOf[ModelImpl] should be(true)

    val property = model.getProperties().get("streetNumber")
    var xml = property.getXml()

    xml should be(null)
  }

  it should "stay hidden per 534" in {
    val schemas = ModelConverters.getInstance().readAll(classOf[Issue534])
    schemas.get("Issue534").getProperties().size() should be(1)
  }
}

@RunWith(classOf[JUnitRunner])
class XmlModelTest2 extends FlatSpec with Matchers {
  it should "honor xml annotations" in {
    val schemas = ModelConverters.getInstance().readAll(classOf[Message])
    Json.prettyPrint(schemas)
  }
}

@XmlRootElement(name = "monster")
class Monster {
  @BeanProperty
  var name: String = _

  @XmlElementWrapper()
  @XmlElement(name = "children")
  @BeanProperty
  var children: java.util.List[String] = _
}