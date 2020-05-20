import cats.effect.{ ContextShift, IO, Timer }
import com.dimafeng.testcontainers.{ ForAllTestContainer, PostgreSQLContainer }
import config.{ Config, DatabaseConfig, ServerConfig }
import io.circe.Json
import io.circe.literal._
import io.circe.optics.JsonPath._
import org.http4s.circe._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.util.{ CaseInsensitiveString => IString }
import org.http4s.{ Method, Request, Status, Uri }
import org.scalatest.{ BeforeAndAfterAll, OptionValues }
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{ Millis, Seconds, Span }
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

class MockyServerSpec extends AnyWordSpec with Matchers with OptionValues with Eventually with ForAllTestContainer {

  implicit private val timer: Timer[IO] = IO.timer(ExecutionContext.global)
  implicit private val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit override val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(100, Millis)))

  private lazy val client = BlazeClientBuilder[IO](global).resource
  override val container = PostgreSQLContainer(dockerImageNameOverride = "postgres:12")

  private val serverPort = Random.nextInt(40000) + 1024
  private lazy val URL = s"http://localhost:$serverPort"

  override def afterStart(): Unit = {
    val config = Config(
      server = ServerConfig("localhost", serverPort),
      database = DatabaseConfig(
        driver = container.driverClassName,
        url = container.jdbcUrl,
        user = container.username,
        password = container.password
      )
    )

    // Launch the mocky server
    HttpServer.createFromConfig(config).unsafeRunAsyncAndForget()

    // Wait for the server to be available
    val _ = eventually {
      client.use(_.statusFromUri(Uri.unsafeFromString(s"$URL/api/status"))).unsafeRunSync() shouldBe Status.Ok
    }
  }

  "Mocky server" should {

    "play an UTF8 mock" in {
      val id = "48e9c41b-de8c-4aeb-99a8-2f3abe3e5efa" // from V003 migration
      val request = Request[IO](uri = Uri.unsafeFromString(s"$URL/v3/$id"))

      val response = client.use(_.expect[String](request)).unsafeRunSync()
      response shouldBe
        "Dès Noël, où un zéphyr haï me vêt de glaçons würmiens, je dîne d’exquis rôtis de bœuf au kir, à l’aÿ d’âge mûr, &cætera."

      val headers = client.use(_.fetch(request)(resp => IO.pure(resp.headers))).unsafeRunSync()
      headers.get(IString("X-SAMPLE-HEADER")).value.value shouldBe "Sample value"
      headers.get(IString("Content-TYPE")).value.value shouldBe "text/plain; charset=UTF-8"

      val status = client.use(_.status(request)).unsafeRunSync()
      status shouldBe Status.Created

      // api get stats count call
    }

    "play an ISO-8859-1 mock" in {
      val id = "6c23b606-29a7-4e0f-9343-87ec0a8ac5e5" // from V003 migration
      val request = Request[IO](uri = Uri.unsafeFromString(s"$URL/v3/$id"))

      val response = client.use(_.expect[String](request)).unsafeRunSync()
      response shouldBe
        "Dès Noël, où un zéphyr haï me vêt de glaçons würmiens, je dîne d'exquis rôtis de boeuf au kir, à l'aÿ d'âge mûr, &cætera."

      val headers = client.use(_.fetch(request)(resp => IO.pure(resp.headers))).unsafeRunSync()
      headers.get(IString("X-SAMPLE-HEADER")).value.value shouldBe "Sample value"
      headers.get(IString("Content-TYPE")).value.value shouldBe "text/plain; charset=ISO-8859-1"

      val status = client.use(_.status(request)).unsafeRunSync()
      status shouldBe Status.Ok
    }

    /*"update a todo" in {
      val id = createTodo("my todo 2", "low")

      val description = "updated todo"
      val importance = "medium"
      val updateJson = json"""
        {
          "description": $description,
          "importance": $importance
        }"""
      val request =
        Request[IO](method = Method.PUT, uri = Uri.unsafeFromString(s"$urlStart/todos/$id")).withEntity(updateJson)
      client.use(_.expect[Json](request)).unsafeRunSync() shouldBe json"""
        {
          "id": $id,
          "description": $description,
          "importance": $importance
        }"""
    }

    "return a single todo" in {
      val description = "my todo 3"
      val importance = "medium"
      val id = createTodo(description, importance)
      client.use(_.expect[Json](Uri.unsafeFromString(s"$urlStart/todos/$id"))).unsafeRunSync() shouldBe json"""
        {
          "id": $id,
          "description": $description,
          "importance": $importance
        }"""
    }

    "delete a todo" in {
      val description = "my todo 4"
      val importance = "low"
      val id = createTodo(description, importance)
      val deleteRequest = Request[IO](method = Method.DELETE, uri = Uri.unsafeFromString(s"$urlStart/todos/$id"))
      client.use(_.status(deleteRequest)).unsafeRunSync() shouldBe Status.NoContent

      val getRequest = Request[IO](method = Method.GET, uri = Uri.unsafeFromString(s"$urlStart/todos/$id"))
      client.use(_.status(getRequest)).unsafeRunSync() shouldBe Status.NotFound
    }

    "return all todos" in {
      // Remove all existing todos
      val json = client.use(_.expect[Json](Uri.unsafeFromString(s"$urlStart/todos"))).unsafeRunSync()
      root.each.id.long.getAll(json).foreach { id =>
        val deleteRequest = Request[IO](method = Method.DELETE, uri = Uri.unsafeFromString(s"$urlStart/todos/$id"))
        client.use(_.status(deleteRequest)).unsafeRunSync() shouldBe Status.NoContent
      }

      // Add new todos
      val description1 = "my todo 1"
      val description2 = "my todo 2"
      val importance1 = "high"
      val importance2 = "low"
      val id1 = createTodo(description1, importance1)
      val id2 = createTodo(description2, importance2)

      // Retrieve todos
      client.use(_.expect[Json](Uri.unsafeFromString(s"$urlStart/todos"))).unsafeRunSync shouldBe json"""
        [
          {
            "id": $id1,
            "description": $description1,
            "importance": $importance1
          },
          {
            "id": $id2,
            "description": $description2,
            "importance": $importance2
          }
        ]"""
    }
     */
  }

  private def createTodo(description: String, importance: String): Long = {
    val createJson = json"""
      {
        "description": $description,
        "importance": $importance
      }"""
    val request =
      Request[IO](method = Method.POST, uri = Uri.unsafeFromString(s"$URL/todos")).withEntity(createJson)
    val json = client.use(_.expect[Json](request)).unsafeRunSync()
    root.id.long.getOption(json).nonEmpty shouldBe true
    root.id.long.getOption(json).get
  }
}
