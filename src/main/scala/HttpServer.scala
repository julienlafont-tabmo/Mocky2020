import cats.effect._
import config.Config
import db.Database
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware._
import repositories._
import services._

import scala.concurrent.ExecutionContext.Implicits.global

object HttpServer {
  def create(configFile: String = "application.conf")(implicit
    contextShift: ContextShift[IO],
    concurrentEffect: ConcurrentEffect[IO],
    timer: Timer[IO]): IO[ExitCode] = {

    Config.load(configFile).flatMap(config => resources(config)).use(create)
  }

  def createFromConfig(config: Config)(implicit
    contextShift: ContextShift[IO],
    concurrentEffect: ConcurrentEffect[IO],
    timer: Timer[IO]): IO[ExitCode] = {

    resources(config).use(create)
  }

  private def resources(config: Config)(implicit contextShift: ContextShift[IO]): Resource[IO, Resources] = {
    for {
      ec <- ExecutionContexts.fixedThreadPool[IO](config.database.threadPoolSize)
      blocker <- Blocker[IO]
      transactor <- Database.transactor(config.database, ec, blocker)
    } yield Resources(transactor, config)
  }

  private def create(resources: Resources)(implicit
    concurrentEffect: ConcurrentEffect[IO],
    timer: Timer[IO],
    cs: ContextShift[IO]): IO[ExitCode] = {

    val corsAPIConfig = CORS.DefaultCORSConfig.copy(
      anyOrigin = false,
      allowedOrigins = origin => origin == resources.config.server.domain
    )

    import cats.implicits._
    import org.http4s.implicits._

    for {
      _ <- Database.initialize(resources.transactor)

      mockV2Service = new MockV2Service(new MockV2Repository(resources.transactor))
      mockV3Service = new MockV3Service(new MockV3Repository(resources.transactor))
      statusService = new StatusService()

      mockRoutes = mockV2Service.mockRoutes <+> mockV3Service.mockRoutes
      apiRoutes = statusService.routes <+> mockV3Service.apiRoutes

      mockMW = CORS(GZip(Jsonp("callback")(mockRoutes)))
      apiMW = CORS(apiRoutes, corsAPIConfig)

      exitCode <-
        BlazeServerBuilder[IO](global)
          .bindHttp(resources.config.server.port, resources.config.server.host)
          .withHttpApp((mockMW <+> apiMW).orNotFound)
          .serve
          .compile
          .lastOrError

    } yield exitCode
  }

  final case class Resources(transactor: HikariTransactor[IO], config: Config)
}
