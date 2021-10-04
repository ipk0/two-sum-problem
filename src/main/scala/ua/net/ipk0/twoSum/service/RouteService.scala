package ua.net.ipk0.twoSum.service

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, NotFound}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler, Route, ValidationRejection}
import akka.stream.Materializer
import spray.json.DefaultJsonProtocol
import ua.net.ipk0.twoSum.utils.CsvSupport
import ua.net.ipk0.twoSum.UploadConfig

class RouteService(
  config: UploadConfig,
  service: TwoSumProblemService
)(implicit val system: ActorSystem, val mat: Materializer) extends CsvSupport with SprayJsonSupport with DefaultJsonProtocol {

  implicit def rejectionHandler = RejectionHandler.newBuilder()
      .handle {
        case ValidationRejection(msg, _) =>
          complete(BadRequest, ErrorResponse(message = msg))
      }
      .handleNotFound {
        complete(NotFound)
      }
      .result()

  implicit def exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case ex: Exception =>
        extractUri { _ =>
          logger.error("unexpected:", ex)
          complete(HttpResponse(InternalServerError, entity = "Something bad happened, sorry..."))
        }
    }

  val route = Route.seal(
    post {
      pathPrefix("upload"){
        path(IntNumber) { targetNumber =>
          csv(config)(strToInt){ dataList =>
            complete(service.solve(targetNumber, dataList))
          }
        }
      }
    }
  )
}