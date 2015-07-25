import io.swagger.jaxrs.Reader
import io.swagger.jaxrs.config.DefaultReaderConfig
import io.swagger.models.parameters.{BodyParameter, PathParameter, QueryParameter, SerializableParameter}
import io.swagger.models.properties.{MapProperty, _}
import io.swagger.models.{ArrayModel, Model, ModelImpl, RefModel, Swagger}
import models.TestEnum
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}
import resources._

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class SimpleScannerTest extends FlatSpec with Matchers {
  it should "scan a simple resource" in {
    val swagger = new Reader(new Swagger()).read(classOf[SimpleResource])
    swagger.getPaths().size should be(3)

    val path = swagger.getPaths().get("/{id}")
    val get = path.getGet()
    get should not be (null)
    get.getParameters().size should be(2)

    val param1 = get.getParameters().get(0).asInstanceOf[PathParameter]

    param1.getIn() should be("path")
    param1.getName() should be("id")
    param1.getRequired() should be(true)
    param1.getDescription() should be("sample param data")
    param1.getDefaultValue() should be("5")

    val param2 = get.getParameters().get(1)
    param2.getIn() should be("query")
    param2.getName() should be("limit")
    param2.getRequired() should be(false)
    param2.getDescription() should be(null)

    val params = swagger.getPaths().get("/{bodyparams}").getPut().getParameters()
    val bodyParam1 = params.get(0).asInstanceOf[BodyParameter]
    bodyParam1.getIn() should be("body")
    bodyParam1.getName() should be("body")
    bodyParam1.getRequired() should be(true)

    val bodyParam2 = params.get(1).asInstanceOf[BodyParameter]
    bodyParam2.getIn() should be("body")
    bodyParam2.getName() should be("body")
    bodyParam2.getRequired() should be(false)
  }

  it should "scan a resource with void return type" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithVoidReturns])
    swagger.getDefinitions().size() should be(1)
    swagger.getDefinitions().get("NotFoundModel") should not be (null)
  }

  it should "scan a resource with map return type" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithMapReturnValue])

    val path = swagger.getPaths().get("/{id}")
    val get = path.getGet()
    get should not be (null)

    get.getResponses() should not be (null)
    val response = get.getResponses().get("200")
    response should not be (null)

    val schema = response.getSchema()
    schema.getClass should be(classOf[MapProperty])
  }

  it should "scan a resource with generics per 653" in {
    val swagger = new Reader(new Swagger()).read(classOf[Resource653])
    val path = swagger.getPaths().get("/external/info")
    val get = path.getGet()
    get should not be (null)

    get.getResponses() should not be (null)
    val response = get.getResponses().get("default")

    response should not be (null)
    response.getSchema should be(null)
  }

  it should "scan a resource with javax.ws.core.Response " in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithResponse])
    swagger.getDefinitions() should be(null)
  }

  it should "scan a resource with Response.Status return type per 877" in {
    val swagger = new Reader(new Swagger()).read(classOf[Resource877])
    val path = swagger.getPaths().get("/external/info")

    swagger.getTags() should not be (null)
    swagger.getTags().size() should be(1)
    val tag = swagger.getTags().get(0)

    tag.getName() should equal("externalinfo")
    tag.getDescription() should be(null)
    tag.getExternalDocs() should be(null)
  }

  it should "scan a resource with tags" in {
    val swagger = new Reader(new Swagger()).read(classOf[TaggedResource])
    swagger.getTags().size() should be(2)
  }

  it should "scan a resource with tags in test 841" in {
    val swagger = new Reader(new Swagger()).read(classOf[Resource841])
    swagger.getTags().size() should be(3)

    val rootTags = swagger.getPaths().get("/fun").getGet().getTags()
    rootTags.size() should be(2)
    (rootTags.asScala.toList.toSet & Set("tag1", "tag2")).size should be(2)

    val thisTags = swagger.getPaths().get("/fun/this").getGet().getTags()
    thisTags.size() should be(1)
    (thisTags.asScala.toList.toSet & Set("tag1")).size should be(1)

    val thatTags = swagger.getPaths().get("/fun/that").getGet().getTags()
    thatTags.size() should be(1)
    (thatTags.asScala.toList.toSet & Set("tag2")).size should be(1)
  }

  it should "scan a resource with param enums" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithEnums])
    val get = swagger.getPaths().get("/{id}").getGet()
    val param = get.getParameters().get(2).asInstanceOf[SerializableParameter]
    val _enum = param.getEnum()
    _enum.asScala.toSet should equal(Set("a", "b", "c", "d", "e"))

    val checkEnumHandling = swagger.getPath("/checkEnumHandling/{v0}").getGet().getParameters()
    val allEnumValues = (for (item <- TestEnum.values()) yield item.name()).toSet
    val v0 = checkEnumHandling.get(0).asInstanceOf[SerializableParameter]
    v0.getEnum().asScala.toSet should be(allEnumValues)
    val v1 = checkEnumHandling.get(1).asInstanceOf[SerializableParameter]
    v1.getItems().asInstanceOf[StringProperty].getEnum().asScala.toSet should be(allEnumValues)
    val v2 = checkEnumHandling.get(2).asInstanceOf[SerializableParameter]
    v2.getItems().asInstanceOf[StringProperty].getEnum().asScala.toSet should be(allEnumValues)
    val v3 = checkEnumHandling.get(3).asInstanceOf[SerializableParameter]
    v3.getEnum().asScala.toSet should be(Set("A", "B", "C"))
  }

  it should "scan a resource with param range" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithRanges])
    val get = swagger.getPaths().get("/{id}").getGet()
    val params = get.getParameters()

    val param0 = params.get(0).asInstanceOf[PathParameter]
    param0.getName should be("id")
    param0.getDefaultValue should be("5")
    Range(param0.getMinimum.toInt, param0.getMaximum.toInt) should equal(Range(0, 10))

    val param1 = params.get(1).asInstanceOf[PathParameter]
    param1.getName should be("minValue")
    param1.getMinimum should be(0)
    param1.getMaximum should be(null)

    val param2 = params.get(2).asInstanceOf[PathParameter]
    param2.getName should be("maxValue")
    param2.getMinimum should be(null)
    param2.getMaximum should be(100)

    val param3 = params.get(3).asInstanceOf[PathParameter]
    param3.getName should be("values")
    val items = param3.getItems.asInstanceOf[IntegerProperty]
    items.getMinimum should be(0)
    items.getMaximum should be(5)
    items.getExclusiveMinimum().booleanValue should be(true)
    items.getExclusiveMaximum().booleanValue should be(true)
  }

  it should "scan a resource with response headers" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithResponseHeaders])
    val get = swagger.getPaths().get("/{id}").getGet()
    val responses = get.getResponses()
    val response200 = responses.get("200")
    val headers200 = response200.getHeaders()
    headers200.size should be(1)
    headers200.get("foo").getDescription should be("description")
    headers200.get("foo").getType should be("string")

    val response400 = responses.get("400")
    val headers400 = response400.getHeaders()
    headers400.size should be(1)
    headers400.get("X-Rack-Cache").getDescription should be("Explains whether or not a cache was used")
    headers400.get("X-Rack-Cache").getType should be("boolean")
  }

  it should "not scan a hidden resource" in {
    val swagger = new Reader(new Swagger()).read(classOf[HiddenResource])
    val get = swagger.getPaths() should be(null)
  }

  it should "correctly model an empty model per 499" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithEmptyModel])
    swagger.getDefinitions.size should be(1)
    val empty = swagger.getDefinitions.get("EmptyModel").asInstanceOf[ModelImpl]
    empty.getType should be("object")
    empty.getProperties should be(null)
    empty.getAdditionalProperties should be(null)
  }

  it should "scan a simple resource without annotations" in {
    val config = new DefaultReaderConfig()
    config.setScanAllResources(true)
    val swagger = new Reader(new Swagger(), config).read(classOf[SimpleResourceWithoutAnnotations])
    swagger.getPaths().size should be(2)

    val path = swagger.getPaths().get("/{id}")
    val get = path.getGet()
    get should not be (null)
    get.getParameters().size should be(2)

    val param1 = get.getParameters().get(0).asInstanceOf[PathParameter]

    param1.getIn() should be("path")
    param1.getName() should be("id")
    param1.getRequired() should be(true)
    param1.getDescription() should be(null)
    param1.getDefaultValue() should be("5")

    val param2 = get.getParameters().get(1)
    param2.getIn() should be("query")
    param2.getName() should be("limit")
    param2.getRequired() should be(false)
    param2.getDescription() should be(null)
  }

  it should "scan resource with ApiOperation.code() value" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithApiOperationCode])
    val paths = swagger.getPaths().get("/{id}")
    val responses1 = paths.getGet.getResponses;
    responses1.size() should be(3);
    responses1.containsKey("202") should be(true);
    responses1.containsKey("200") should be(false);
    responses1.get("202").getDescription should be("successful operation");

    val responses2 = paths.getPut.getResponses;
    responses2.size() should be(3);
    responses2.containsKey("200") should be(true);
    responses2.get("200").getDescription should be("successful operation");
  }

  it should "scan resource with ApiResponse.responseContainer() value" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithApiResponseResponseContainer])
    val paths = swagger.getPaths().get("/{id}")
    val responses1 = paths.getGet.getResponses;
    responses1.get("200").getSchema().getClass() should be(classOf[MapProperty])
    responses1.get("400").getSchema().getClass() should be(classOf[ArrayProperty])

    val responses2 = paths.getPut.getResponses;
    responses2.get("201").getSchema().getClass() should be(classOf[RefProperty])
    responses2.get("401").getSchema().getClass() should be(classOf[ArrayProperty])

    val responses3 = paths.getPost.getResponses;
    responses3.get("202").getSchema().getClass() should be(classOf[RefProperty])
    responses3.get("402").getSchema().getClass() should be(classOf[RefProperty])

    val responses4 = paths.getDelete.getResponses;
    responses4.get("203").getSchema().getClass() should be(classOf[RefProperty])
    responses4.get("403").getSchema().getClass() should be(classOf[RefProperty])

    val paths2 = swagger.getPaths().get("/{id}/name")
    val responses5 = paths2.getGet.getResponses;
    responses5.get("203").getSchema().getClass() should be(classOf[ArrayProperty])
    responses5.get("203").getSchema().asInstanceOf[ArrayProperty].getUniqueItems() should be(null)
    responses5.get("203").getHeaders().get("foo").getClass() should not be (classOf[MapProperty])
    responses5.get("403").getSchema().getClass() should be(classOf[ArrayProperty])
    responses5.get("403").getSchema().asInstanceOf[ArrayProperty].getUniqueItems().booleanValue() should be(true)

    val responses6 = paths2.getPut.getResponses;
    responses6.get("203").getSchema().getClass() should be(classOf[ArrayProperty])
    responses6.get("203").getSchema().asInstanceOf[ArrayProperty].getUniqueItems().booleanValue() should be(true)
    responses6.get("203").getHeaders().get("foo").getClass() should be(classOf[ArrayProperty])
    responses6.get("203").getHeaders().get("foo").asInstanceOf[ArrayProperty].getUniqueItems.booleanValue() should be(true)
    responses6.get("403").getSchema().getClass() should be(classOf[ArrayProperty])
  }

  it should "scan a resource with inner class" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithInnerClass])
    swagger.getPath("/description").getGet().getResponses().get("200").getSchema().asInstanceOf[ArrayProperty].
      getItems().asInstanceOf[RefProperty].get$ref() should be("#/definitions/Description")
    swagger.getDefinitions().containsKey("Description") should be(true)
  }

  it should "scan defaultValue and required per #937" in {
    val swagger = new Reader(new Swagger()).read(classOf[Resource937])
    val get = swagger.getPaths().get("/external/info").getGet()
    val param = get.getParameters().get(0).asInstanceOf[QueryParameter]
    param.getRequired should be(false)
    param.getDefaultValue should be("dogs")
  }

  it should "scan a resource with all hidden values #1073" in {
    val swagger = new Reader(new Swagger()).read(classOf[Resource1073])
    swagger.getPaths() should be(null)
  }

  it should "scan a resource with body parameters" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithBodyParams])
    val param = swagger.getPaths().get("/testShort").getPost().getParameters().get(0).asInstanceOf[BodyParameter]
    param.getDescription() should be("a short input")
    val schema = param.getSchema.asInstanceOf[ModelImpl]

    schema.getType() should be("integer")
    schema.getFormat() should be("int32")

    swagger.getDefinitions().keySet().asScala should be(Set("Tag"))

    def testParam(path: String, name: String, description: String): Model = {
      val param = swagger.getPath(path).getPost().getParameters().get(0).asInstanceOf[BodyParameter]
      param.getIn should be("body")
      param.getName should be(name)
      param.getDescription should be(description)
      param.getSchema
    }

    def testString(path: String, name: String, description: String) = {
      testParam(path, name, description).asInstanceOf[ModelImpl].getType() should be("string")
    }
    testString("/testApiString", "input", "String parameter")
    testString("/testString", "body", null)

    def testObject(path: String, name: String, description: String) = {
      testParam(path, name, description).asInstanceOf[RefModel].getSimpleRef() should be("Tag")
    }
    testObject("/testApiObject", "input", "Object parameter")
    testObject("/testObject", "body", null)

    var list = swagger.getPaths().values().asScala.map {
      _.getPost()
    }.filter {
      _.getOperationId().startsWith("testPrimitive")
    }
    list.size should be(16)
    for (item <- list) {
      item.getParameters().size() should be(1)
    }
  }

  it should "verify top-level path params per #1085" in {
    val swagger = new Reader(new Swagger()).read(classOf[Resource1085])
    val params = swagger.getPaths().get("/external/info/{id}").getGet().getParameters()
    val param = params.get(0)
    param.getName() should be("id")
    param.isInstanceOf[PathParameter] should be(true)
  }

  it should "verify top-level auth #1041" in {
    val swagger = new Reader(new Swagger()).read(classOf[Resource1041])
    val path1 = swagger.getPaths().get("/external/info/path1").getGet()
    val security1 = path1.getSecurity()
    security1.size should be(1)
    security1.get(0).get("my_auth") should not be (null)

    val path2 = swagger.getPaths().get("/external/info/path2").getGet()
    val security2 = path2.getSecurity()
    security2.size should be(1)
    security2.get(0).get("your_auth") should not be (null)
  }

  it should "check response models processing" in {
    val swagger = new Reader(new Swagger()).read(classOf[ResourceWithTypedResponses])
    swagger.getDefinitions.keySet().asScala should be(Set("Tag"))
    for ((key, path) <- swagger.getPaths.asScala) {
      key.substring(key.lastIndexOf("/") + 1) match {
        case "testPrimitiveResponses" =>
          val expected = Map("400" ->("string", "uri"),
            "401" ->("string", "url"),
            "402" ->("string", "uuid"),
            "403" ->("integer", "int64"),
            "404" ->("string", null))
          for ((code, response) <- path.getGet.getResponses.asScala) {
            val property = expected(code)
            response.getSchema.getType should be(property._1)
            response.getSchema.getFormat should be(property._2)
          }
        case "testObjectResponse" =>
          val op = path.getGet
          val response = op.getResponses.get("200").getSchema
          response.asInstanceOf[RefProperty].getSimpleRef should be("Tag")
          op.getParameters.size should be(1)
          val model = op.getParameters.get(0).asInstanceOf[BodyParameter].getSchema
          model.asInstanceOf[RefModel].getSimpleRef should be("Tag")
        case "testObjectsResponse" =>
          val op = path.getGet
          val response = op.getResponses.get("200").getSchema
          response.asInstanceOf[ArrayProperty].getItems.asInstanceOf[RefProperty].getSimpleRef should be("Tag")
          op.getParameters.size should be(1)
          val model = op.getParameters.get(0).asInstanceOf[BodyParameter].getSchema
          model.asInstanceOf[ArrayModel].getItems.asInstanceOf[RefProperty].getSimpleRef should be("Tag")
        case "testStringResponse" =>
          val op = path.getGet
          val response = op.getResponses.get("200").getSchema
          response.getClass should be(classOf[StringProperty])
          op.getParameters.size should be(1)
          val model = op.getParameters.get(0).asInstanceOf[BodyParameter].getSchema
          model.asInstanceOf[ModelImpl].getType should be("string")
        case "testStringsResponse" =>
          val op = path.getGet
          val response = op.getResponses.get("200").getSchema
          response.asInstanceOf[ArrayProperty].getItems.getClass should be(classOf[StringProperty])
          op.getParameters.size should be(1)
          val model = op.getParameters.get(0).asInstanceOf[BodyParameter].getSchema
          model.asInstanceOf[ArrayModel].getItems.getClass should be(classOf[StringProperty])
        case "testMapResponse" =>
          val op = path.getGet
          val response = op.getResponses.get("200").getSchema
          response.asInstanceOf[MapProperty].getAdditionalProperties().asInstanceOf[RefProperty].getSimpleRef should be("Tag")
          op.getParameters.size should be(1)
          val model = op.getParameters.get(0).asInstanceOf[BodyParameter].getSchema.asInstanceOf[ModelImpl]
          model.getProperties should be(null)
          model.getAdditionalProperties.asInstanceOf[RefProperty].getSimpleRef should be("Tag")
      }
    }
  }
}
