package services

import cats.effect.{ ContextShift, IO, Timer }
import http.HttpMockResponse
import models.http.RequestOptions._
import models.errors.MockNotFoundError
import org.http4s.dsl.Http4sDsl
import org.http4s._
import repositories.MockV2Repository

import scala.concurrent.ExecutionContext

/**
  * Legacy mocks, only used if you've run the mongodb mocky version
  */
class MockV2Service(repository: MockV2Repository)(implicit cs: ContextShift[IO], ec: ExecutionContext, timer: Timer[IO])
    extends Http4sDsl[IO]
    with HttpMockResponse {

  val mockRoutes = HttpRoutes.of[IO] {
    // Fetch and "play" a legacy mock
    case _ -> "v2" /: id /: _ :? JsonP.opt(jsonp) +& Delay.opt(delay) =>
      repository.touchAndGetMockResponse(id).flatMap {
        case Left(MockNotFoundError) => NotFound()
        case Right(mock) => respondWithMock(mock, jsonp, delay)
      }
  }
}
