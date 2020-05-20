package http

import cats.Applicative
import cats.effect.{ ContextShift, IO, Timer }
import models.http.RequestOptions.{ Delay, JsonP }
import models.mocks.MockResponse
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{ `Content-Length`, `Content-Type` }
import org.http4s._

import scala.concurrent.ExecutionContext

trait HttpMockResponse extends Http4sDsl[IO] {

  protected def respondWithMock[F[_]](
    mock: MockResponse,
    jsonpCallback: Option[JsonP.Value],
    delay: Option[Delay.Value]
  )(implicit F: Applicative[IO], cs: ContextShift[IO], timer: Timer[IO], ec: ExecutionContext): IO[Response[IO]] = {

    val entity = prepareEntity(mock, jsonpCallback)
    val contentType = prepareContentType(mock, jsonpCallback)
    val headers = prepareHeaders(mock, entity, contentType)

    val waitIO = delayIO(delay)

    val response = F.pure(
      Response[IO](
        status = mock.status,
        headers = headers,
        body = entity.body
      )
    )

    waitIO *> response
  }

  private def entityLengthHeader(entity: Entity[IO]): Headers = {
    entity.length
      .flatMap(length => `Content-Length`.fromLong(length).toOption)
      .map(header => Headers.of(header))
      .getOrElse(Headers.empty)
  }

  private def prepareJsonpEntity(mock: MockResponse, callback: String): Entity[IO] = {
    val originalPayload = new String(mock.content, mock.charset.nioCharset)
    val wrappedOriginalPayload = s"$callback($originalPayload);"
    val payloadInDestinationCharset = wrappedOriginalPayload.getBytes(mock.charset.nioCharset)

    EntityEncoder.byteArrayEncoder[IO].toEntity(payloadInDestinationCharset)
  }

  private def prepareNonJsonpEntity(mock: MockResponse): Entity[IO] = {
    EntityEncoder.byteArrayEncoder[IO].toEntity(mock.content)
  }

  private def prepareEntity(mock: MockResponse, jsonpCallback: Option[JsonP.Value]): Entity[IO] = {
    jsonpCallback match {
      case Some(JsonP.Value(callback)) =>
        prepareJsonpEntity(mock, callback)

      case None =>
        prepareNonJsonpEntity(mock)
    }
  }

  private def prepareContentType(mock: MockResponse, jsonpCallback: Option[JsonP.Value]): `Content-Type` = {
    jsonpCallback match {
      case Some(_) => `Content-Type`(MediaType.application.javascript).withCharset(mock.charset)
      case None => mock.contentType
    }
  }

  private def prepareHeaders(mock: MockResponse, entity: Entity[IO], contentType: `Content-Type`): Headers = {
    val userHeaders = mock.headers
    val lengthHeaders = entityLengthHeader(entity)
    val contentTypeHeader = Headers.of(contentType)

    userHeaders ++ lengthHeaders ++ contentTypeHeader
  }

  private def delayIO(delay: Option[Delay.Value])(implicit timer: Timer[IO]): IO[Unit] = {
    delay match {
      case Some(Delay.Value(duration)) => IO.sleep(duration)
      case None => IO.unit
    }
  }

}
