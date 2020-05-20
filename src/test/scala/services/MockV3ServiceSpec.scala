package services

import cats.effect.IO
import io.circe.Json
import io.circe.literal._
import io.circe.syntax._
import models.mocks.actions.CreateUpdateMock
import models.mocks.actions.CreateUpdateMock.CreateUpdateMockHeaders
import models.mocks.feedbacks.MockCreated
import org.http4s.{ Request, Response, Status, Uri }
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import cats.implicits._
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repositories.MockV3Repository

class MockV3ServiceSpec extends AnyWordSpec with MockFactory with Matchers {
  implicit val ec = scala.concurrent.ExecutionContext.global
  implicit val shift = IO.contextShift(ec)
  implicit val timer = IO.timer(ec)

  private val repository = stub[MockV3Repository]
  private val service = new MockV3Service(repository)
  private val routes = service.apiRoutes <+> service.mockRoutes

  "MockV3Service" should {

    val id = java.util.UUID.randomUUID()

    val mock = CreateUpdateMock(
      "Hello World",
      "text/plain",
      200,
      "UTF-8",
      Some(CreateUpdateMockHeaders(Map("X-FOO" -> "bar"))),
      "secret")

    val createJson =
      json"""
        {
          "content": ${mock.content},
          "content_type": ${mock.contentType},
          "charset": ${mock.charset},
          "status": ${mock.status},
          "headers": ${mock.headers.get.underlyingMap.asJson},
          "secret": ${mock.secret}
        }"""

    "create a mock" in {
      (repository.insert _).when(mock).returns(IO.pure(MockCreated(id)))
      val response = serve(Request[IO](POST, uri"/api").withEntity(createJson))
      response.status shouldBe Status.Created
      response.as[Json].unsafeRunSync() shouldBe json"""{ "id": $id }"""
    }

    "reject mock with invalid http status" in {
      val json = createJson.hcursor.downField("status").set(0.asJson).top.get

      val response = serve(Request[IO](POST, uri"/api").withEntity(json))

      response.status shouldBe Status.UnprocessableEntity
      response.as[Json].unsafeRunSync() shouldBe json"""{ "errors": { ".status": "error.min.size" } }"""
    }

    "reject mock with too long content" in {
      val json = createJson.hcursor.downField("content").withFocus(_.mapString(_ * 99999)).top.get

      val response = serve(Request[IO](POST, uri"/api").withEntity(json))

      response.status shouldBe Status.UnprocessableEntity
      response.as[Json].unsafeRunSync() shouldBe json"""{ "errors": { ".content": "error.maximum.length" } }"""
    }

    "reject mock with too long secret" in {
      val json = createJson.hcursor.downField("secret").withFocus(_.mapString(_ * 99)).top.get

      val response = serve(Request[IO](POST, uri"/api").withEntity(json))

      response.status shouldBe Status.UnprocessableEntity
      response.as[Json].unsafeRunSync() shouldBe json"""{ "errors": { ".secret": "error.maximum.length" } }"""
    }

    "update an existing mock" in {
      (repository.update _).when(id, mock).returns(IO.pure(true))
      val response = serve(Request[IO](PUT, Uri.unsafeFromString(s"/api/$id")).withEntity(createJson))
      response.status shouldBe Status.NoContent
    }

    "refuse to update a mock if the secret is invalid an existing mock" in {
      (repository.update _).when(id, *).returns(IO.pure(false))
      val response = serve(Request[IO](PUT, Uri.unsafeFromString(s"/api/$id")).withEntity(createJson))
      response.status shouldBe Status.NotFound
    }

    "delete an existing mock" in {
      (repository.delete _).when(id, *).returns(IO.pure(true))
      val response = serve(Request[IO](DELETE, Uri.unsafeFromString(s"/api/$id")).withEntity(createJson))
      response.status shouldBe Status.NoContent
    }
  }

  private def serve(request: Request[IO]): Response[IO] = {
    routes.orNotFound(request).unsafeRunSync()
  }
}
