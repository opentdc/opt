package converter

import java.util.Date

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.swagger.converter.ModelConverters
import io.swagger.util.Json
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.junit.JUnitRunner

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class CaseClassConverterTest extends FlatSpec with Matchers {
  val m = Json.mapper()
  m.registerModule(DefaultScalaModule)

  it should "read a simple case class" in {
    val models = ModelConverters.getInstance().read(classOf[SimpleCaseClass])
    models.size should be(1)

    val model = models.get("SimpleCaseClass")
    val properties = model.getProperties()
    properties should not be (null)
    properties.size should be(3)
    properties.asScala.map {
      _._1
    }.toSet should equal(Set("id", "name", "date"))
  }

  it should "read a nested case class" in {
    val models = ModelConverters.getInstance().read(classOf[NestedCaseClass])
    models.size should be(1)

    val model = models.get("NestedCaseClass")
    val properties = model.getProperties()
    properties should not be (null)
    properties.asScala.map {
      _._1
    }.toSet should equal(Set("id", "firstName", "innerObject"))
  }

  it should "read a case class with Options" in {
    val models = ModelConverters.getInstance().read(classOf[CaseClassWithOptions])
    models.size should be(1)

    val model = models.get("CaseClassWithOptions")
    val properties = model.getProperties()
    properties should not be (null)
    properties.asScala.map {
      _._1
    }.toSet should equal(Set("id", "firstName", "age"))
  }
}

case class SimpleCaseClass(
                            id: Long,
                            name: String,
                            date: Date
                            )

case class NestedCaseClass(
                            id: Long,
                            firstName: String,
                            innerObject: SimpleCaseClass
                            )

case class CaseClassWithOptions(
                                 id: Long,
                                 firstName: String,
                                 age: Option[Int]
                                 )
