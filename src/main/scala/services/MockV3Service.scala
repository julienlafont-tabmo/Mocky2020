package services

import cats.effect.{ ContextShift, IO, Timer }
import http.{ HttpMockResponse, JsonMarshalling }
import models.errors.MockNotFoundError
import models.http.RequestOptions._
import models.mocks.actions.{ CreateUpdateMock, DeleteMock }
import models.mocks.instances._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import repositories.MockV3Repository

import scala.concurrent.ExecutionContext

class MockV3Service(repository: MockV3Repository)(implicit cs: ContextShift[IO], ec: ExecutionContext, timer: Timer[IO])
    extends Http4sDsl[IO]
    with HttpMockResponse
    with JsonMarshalling {

  val mockRoutes = HttpRoutes.of[IO] {

    // Fetch and "play" a mock
    case _ -> "v3" /: UUIDVar(id) /: _ :? JsonP.opt(jsonp) +& Delay.opt(delay) =>
      repository.touchAndGetMockResponse(id).flatMap {
        case Left(MockNotFoundError) => NotFound()
        case Right(mock) => respondWithMock(mock, jsonp, delay)
      }
  }

  val apiRoutes = HttpRoutes.of[IO] {

    // Create new mock
    case req @ POST -> Root / "api" =>
      decodeJson[IO, CreateUpdateMock](req) { createMock =>
        for {
          created <- repository.insert(createMock)
          response <- Created(created)
        } yield response
      }

    // Get an existing mock
    case GET -> Root / "api" / UUIDVar(id) =>
      repository.get(id).flatMap {
        case Left(MockNotFoundError) => NotFound()
        case Right(mock) => Ok(mock)
      }

    // Update an existing mock
    case req @ PUT -> Root / "api" / UUIDVar(id) =>
      decodeJson[IO, CreateUpdateMock](req) { updateMock =>
        for {
          updated <- repository.update(id, updateMock)
          response <- if (updated) NoContent() else NotFound()
        } yield response
      }

    // Delete an existing mock
    case req @ DELETE -> Root / "api" / UUIDVar(id) =>
      decodeJson[IO, DeleteMock](req) { deleteMock =>
        for {
          deleted <- repository.delete(id, deleteMock)
          response <- if (deleted) NoContent() else NotFound()
        } yield response
      }

  }

}
