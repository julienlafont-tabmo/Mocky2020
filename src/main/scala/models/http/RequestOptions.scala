package models.http

import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher
import org.http4s.{ ParseFailure, QueryParamDecoder }

import scala.concurrent.duration.{ Duration, FiniteDuration, _ }
import scala.util.{ Failure, Success, Try }

object RequestOptions {

  /**
    * Allow to wrap the mock in a JsonP callback.
    * This callback must be passed in the `callback` parameter
    */
  object JsonP {
    implicit private val jsonpParamDecoder: QueryParamDecoder[Value] = QueryParamDecoder[String].map(Value.apply)
    case class Value(value: String) extends AnyVal

    object opt extends OptionalQueryParamDecoderMatcher[Value]("callback")
  }

  /**
    * Allow to simulate a slow endpoint by adding in delay before returning the mock
    * This delay must be passed in the `mocky-delay` parameter
    */
  object Delay {
    private val MAX_DELAY = 60.seconds

    implicit private val delayParamDecoder: QueryParamDecoder[Value] = QueryParamDecoder[String].emap(parse)
    case class Value(duration: FiniteDuration) extends AnyVal

    object opt extends OptionalQueryParamDecoderMatcher[Value]("mocky-delay")

    private[Delay] def parse(delay: String): Either[ParseFailure, Value] = {
      Try(Duration(delay)) match {
        case Success(d: FiniteDuration) if d.gteq(Duration.Zero) && d.lteq(MAX_DELAY) => Right(Value(d))
        case Success(_) => Left(ParseFailure("Delay must be between 0 and 60s", ""))
        case Failure(_) => Left(ParseFailure("Invalid duration (expected: 10s, 100ms, 50ns)", ""))
      }
    }
  }

}
