package ua.net.ipk0.twoSum

import spray.json.DefaultJsonProtocol

import scala.util.{Failure, Success, Try}

package object service {

  class ValidationException(message: String, cause: Throwable = None.orNull) extends Exception(message, cause)

  case class ErrorResponse(code: String = "wrong.input.type", message: String)

  object ErrorResponse extends DefaultJsonProtocol {
    implicit val errorJsonFormat = jsonFormat2(ErrorResponse.apply)
  }

  implicit val strToInt: String => Int = str => Try {
    Integer.parseInt(str)
  } match {
    case Success(intValue) => intValue
    case Failure(ex) =>
      throw new ValidationException(s"The value $str wasn't recognized as the appropriate number", ex)
  }
}
