package ua.net.ipk0.twoSum.utils

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.{Directive1, ValidationRejection}
import akka.http.scaladsl.server.Directives.{complete, fileUpload, onComplete, provide, reject}
import akka.stream.Materializer
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging
import ua.net.ipk0.twoSum.UploadConfig
import ua.net.ipk0.twoSum.service.ValidationException

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait CsvSupport extends LazyLogging {
  implicit val mat: Materializer

  def csv[A](config: UploadConfig)(implicit transform: String => A): Directive1[List[List[A]]] = {
    fileUpload(config.fieldName).flatMap {
      case (_, byteSource) =>
        onComplete(parseCsv(byteSource, transform, config.maximumFrameLength)).flatMap {
          case Success(t) =>
            provide(t)
          case Failure(ex) => ex match {
            case ex: ValidationException => reject(ValidationRejection(ex.getMessage, Some(ex)))
            case ex: Throwable =>
              logger.error("something bad happened", ex)
              complete(HttpResponse(InternalServerError, entity = "Something bad happened, sorry..."))
          }
        }
    }
  }

  def parseCsv[A](byteSource: Source[ByteString, _], transform: String => A, maximumFrameLength: Int = 1024): Future[List[List[A]]] =
    byteSource
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength, allowTruncation = true))
      .map(_.utf8String.split(",").map(_.trim).toList)
      .map(transformList(_, transform))
      .runFold(List.empty[List[A]])(_ :+ _)

  def transformList[A](target: List[String], transform: String => A): List[A] = target match {
    case "" :: Nil => Nil
    case first :: Nil => throw new ValidationException(s"Row must contain more than one value: $first")
    case list => list.map(transform)
  }
}
