package http

import cats.{ Applicative, FlatMap }
import cats.data.NonEmptyList
import cats.effect.Sync
import io.circe.{ CursorOp, Decoder, DecodingFailure, Encoder, Json, Printer }
import org.http4s.{ DecodeFailure, HttpVersion, Response, Status }
import org.http4s.circe.CirceInstances

/**
  * Trait allowing to decode/encode JSON request/response, with custom decoding errors
  */
trait JsonMarshalling {

  import org.http4s._
  import cats.syntax.all._

  private val instances = CirceInstances.builder
    .withJsonDecodeError(UnprocessableEntityFailure)
    .build

  /**
    * Decode a JSON payload as T, and run a response requiring T
    */
  def decodeJson[F[_], T](request: Request[F])(block: T => F[Response[F]])(implicit
    F: Applicative[F],
    sync: Sync[F],
    flat: FlatMap[F],
    decoder: Decoder[T]): F[Response[F]] = {

    instances.accumulatingJsonOf[F, T].decode(request, false).value.flatMap {
      case Right(obj) => block(obj)
      case Left(error) => F.pure(error.toHttpResponse[F](request.httpVersion))
    }

  }

  /**
    * Allow to return any A as JSON response when an Encoder[A] is in the implicit scope
    */
  implicit def jsonEncoder[F[_], A](implicit encoder: Encoder[A]): EntityEncoder[F, A] = instances.jsonEncoderOf
}

final case class UnprocessableEntityFailure(json: Json, failures: NonEmptyList[DecodingFailure])
    extends DecodeFailure
    with CirceInstances {

  /**
    * Format JSON errors
    * {"errors": [ ".path": "error-code ] }
    */
  override def toHttpResponse[F[_]](httpVersion: HttpVersion): Response[F] = {
    val errors = Json.fromFields(failures.toList.map { failure =>
      val path = CursorOp.opsToPath(failure.history)
      val error = failure.message
      path -> Json.fromString(error)
    })
    Response(Status.UnprocessableEntity, httpVersion).withEntity(Json.obj("errors" -> errors))
  }

  override val message = s"Invalid JSON received"
  override def cause: Option[Throwable] = None
}
