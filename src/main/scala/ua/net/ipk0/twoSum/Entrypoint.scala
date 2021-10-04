package ua.net.ipk0.twoSum

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import sun.misc.Signal
import ua.net.ipk0.twoSum.service.{RouteService, TwoSumProblemService}

import scala.util.Try

object Entrypoint extends App with LazyLogging {
  implicit val system = ActorSystem("two-sum-problem")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  val config = ConfigFactory.load

  val appConfig = config.getConfig("two-sum-problem")
  val uploadFieldName = appConfig.getString("upload.fieldName")
  val interface = appConfig.getString("server.interface")
  val port = appConfig.getInt("server.port")
  val maximumFrameLength = appConfig.getInt("parsing.maximumFrameLength")

  val bindingFuture = Http().bindAndHandle(
    new RouteService(UploadConfig(uploadFieldName, maximumFrameLength), TwoSumProblemService()).route,
    interface,
    port
  )

  logger.info(s"Server online at $interface:$port")

  List("USR2").foreach(sig =>
    Try(new Signal(sig)) foreach { s ⇒
      try Signal.handle(
        s,
        (signal: Signal) => {
          logger.info(s"[GracefullyShutdown] >>>>>>> Signal $signal received")
          bindingFuture
            .flatMap(_.unbind())
            .onComplete(_ => system.terminate())
        })
      catch {
        case ex: Throwable ⇒
          logger.warn("[GracefullyShutdown] Error registering handler for signal: {}", s, ex)
      }
    })
}
