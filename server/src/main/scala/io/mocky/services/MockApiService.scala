package io.mocky.services

import cats.effect.IO
import io.circe.Decoder

import io.mocky.http.JsonMarshalling
import io.mocky.models.errors.MockNotFoundError
import io.mocky.models.mocks.actions.{ CreateUpdateMock, DeleteMock }
import io.mocky.models.mocks.instances._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.middleware.CORS

import io.mocky.config.Settings
import io.mocky.repositories.MockV3Repository
import io.mocky.utils.HttpUtil

class MockApiService(repository: MockV3Repository, settings: Settings) extends Http4sDsl[IO] with JsonMarshalling {

  // Allow only request coming from `settings.domain`
  private val corsAPIConfig = CORS.DefaultCORSConfig.copy(anyOrigin = false, allowedOrigins = _ == settings.cors.domain)

  // Expose the routes wrapped into their middleware
  lazy val routing: HttpRoutes[IO] = CORS(routes, corsAPIConfig)

  // Prepare a decoder with dynamic configuration
  implicit private val createUpdateMockDecoder: Decoder[CreateUpdateMock] = CreateUpdateMock.decoder(settings.mock)

  private def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    // Create new mock
    case req @ POST -> Root / "api" =>
      decodeJson[IO, CreateUpdateMock](req) { createMock =>
        for {
          created <- repository.insert(createMock.withIp(HttpUtil.getIP(req)))
          response <- Created(created)
        } yield response
      }

    // Get an existing mock
    case GET -> Root / "api" / UUIDVar(id) =>
      repository.get(id).flatMap {
        case Left(MockNotFoundError) => NotFound()
        case Right(mock) => Ok(mock)
      }

    // Get the stats of a mock
    case GET -> Root / "api" / UUIDVar(id) / "stats" =>
      repository.stats(id).flatMap {
        case Left(MockNotFoundError) => NotFound()
        case Right(stats) => Ok(stats)
      }

    // Update an existing mock
    case req @ PUT -> Root / "api" / UUIDVar(id) =>
      decodeJson[IO, CreateUpdateMock](req) { updateMock =>
        for {
          updated <- repository.update(id, updateMock.withIp(HttpUtil.getIP(req)))
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
