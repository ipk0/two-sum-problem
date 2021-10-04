package ua.net.ipk0.twoSum

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Multipart, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import org.scalatest.{FlatSpec, Matchers}
import spray.json._
import ua.net.ipk0.twoSum.service.{ErrorResponse, RouteService, TwoSumProblemService}

import scala.io.Source

class RouteServiceSpec extends FlatSpec with Matchers with ScalatestRouteTest {

  val formField = "csv"

  val testRoute = new RouteService(UploadConfig(formField), new TwoSumProblemService()).route

  it should "return OK with the appropriate JSON response" in {

    val source = Source.fromResource("test-dataset.csv").mkString

    val multipartForm =
      Multipart.FormData(Multipart.FormData.BodyPart.Strict(
        formField,
        HttpEntity(ContentTypes.`text/csv(UTF-8)`, ByteString(source)),
        Map("filename" -> "data.csv"))
      )

    Post("/upload/7", multipartForm) ~> testRoute ~> check {
      status shouldEqual StatusCodes.OK
        responseAs[String].parseJson shouldBe "[[[4,3],[-4,11]],[[5,2]],[[6,1],[-10,17]],[[5,2],[-2,9]],[]]".parseJson
    }
  }

  it should "be fail-fast and returns BadRequest in case of corrupted input data" in {
    val expectedResponse = ErrorResponse(
      message = "The value xxx wasn't recognized as the appropriate number")

    val source = Source.fromResource("corrupted-dataset.csv").mkString

    val multipartForm =
      Multipart.FormData(Multipart.FormData.BodyPart.Strict(
        formField,
        HttpEntity(ContentTypes.`text/csv(UTF-8)`, ByteString(source)),
        Map("filename" -> "data.csv"))
      )

    Post("/upload/7", multipartForm) ~> testRoute ~> check {
      status shouldEqual StatusCodes.BadRequest
      responseAs[String].parseJson.convertTo[ErrorResponse] shouldBe expectedResponse
    }
  }

  it should "empty response in case of empty file" in {

    val multipartForm =
      Multipart.FormData(Multipart.FormData.BodyPart.Strict(
        formField,
        HttpEntity(ContentTypes.`text/csv(UTF-8)`, ByteString(" ")),
        Map("filename" -> "data.csv"))
      )

    Post("/upload/7", multipartForm) ~> testRoute ~> check {
      status shouldEqual StatusCodes.OK
      "[[]]".parseJson shouldBe responseAs[String].parseJson
    }
  }

  it should "BadRequest in case of one value in a file" in {
    val expectedResponse = ErrorResponse(
      message = "Row must contain more than one value: 1")

    val multipartForm =
      Multipart.FormData(Multipart.FormData.BodyPart.Strict(
        formField,
        HttpEntity(ContentTypes.`text/csv(UTF-8)`, ByteString("1")),
        Map("filename" -> "data.csv"))
      )

    Post("/upload/7", multipartForm) ~> testRoute ~> check {
      status shouldEqual StatusCodes.BadRequest
      responseAs[String].parseJson.convertTo[ErrorResponse] shouldBe expectedResponse
    }
  }

  it should "BadRequest in case of one number is a bigger than Integer.MAX" in {
    val expectedResponse = ErrorResponse(
      message = "The value 9999999999999999999999999999999999 wasn't recognized as the appropriate number")

    val multipartForm =
      Multipart.FormData(Multipart.FormData.BodyPart.Strict(
        formField,
        HttpEntity(ContentTypes.`text/csv(UTF-8)`, ByteString("9999999999999999999999999999999999,11")),
        Map("filename" -> "data.csv"))
      )

    Post("/upload/7", multipartForm) ~> testRoute ~> check {
      status shouldEqual StatusCodes.BadRequest
      responseAs[String].parseJson.convertTo[ErrorResponse] shouldBe expectedResponse
    }
  }
}
