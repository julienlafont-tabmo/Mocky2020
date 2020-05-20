package services

import cats.effect.{ ContextShift, IO, Timer }
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext

class StatusService()(implicit cs: ContextShift[IO], ec: ExecutionContext, timer: Timer[IO]) extends Http4sDsl[IO] {

  val routes = HttpRoutes.of[IO] {
    case GET -> Root / "api" / "status" => Ok()
  }
}
