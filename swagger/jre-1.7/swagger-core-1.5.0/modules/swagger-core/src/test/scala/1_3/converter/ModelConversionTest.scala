package converter

import java.util.Date

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.swagger.annotations.ApiModelProperty
import io.swagger.converter.ModelConverters
import io.swagger.util.Json
import matchers.SerializationMatchers._
import org.junit.runner.RunWith
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.junit.JUnitRunner

import scala.annotation.meta.field

@RunWith(classOf[JUnitRunner])
class ModelConversionTest extends FlatSpec with Matchers {
  Json.mapper().registerModule(DefaultScalaModule)

  it should "format a date" in {
    val models = ModelConverters.getInstance().read(classOf[DateModel])
    val model = models.get("DateModel")
    model.getProperties().size should be(5)
    model should serializeToJson(
      """{
  "type": "object",
  "properties" : {
    "date" : {
      "type" : "string",
      "format" : "date-time",
      "position" : 1
    },
    "intValue" : {
      "type" : "integer",
      "format" : "int32",
      "position" : 2
    },
    "longValue" : {
      "type" : "integer",
      "format" : "int64",
      "position" : 3
    },
    "floatValue" : {
      "type" : "number",
      "format" : "float",
      "position" : 4
    },
    "doubleValue" : {
      "type" : "number",
      "format" : "double",
      "position" : 5
    }
  }
}""")
  }

  it should "format a set" in {
    val models = ModelConverters.getInstance().read(classOf[SetModel])
    val model = models.get("SetModel")
    model.getProperties().size should be(1)
    model should serializeToJson(
      """{
  "type": "object",
  "properties" : {
    "longs" : {
      "type" : "array",
      "uniqueItems" : true,
      "items" : {
        "type" : "integer",
        "format" : "int64"
      }
    }
  }
}""")
  }
}

case class DateModel(
                      @(ApiModelProperty@field)(position = 1) date: Date,
                      @(ApiModelProperty@field)(position = 2) intValue: Int,
                      @(ApiModelProperty@field)(position = 3) longValue: Long,
                      @(ApiModelProperty@field)(position = 4) floatValue: Float,
                      @(ApiModelProperty@field)(position = 5) doubleValue: Double)

case class SetModel(longs: Set[java.lang.Long])
