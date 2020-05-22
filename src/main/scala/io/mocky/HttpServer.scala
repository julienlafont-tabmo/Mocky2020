package io.mocky

import scala.concurrent.ExecutionContext.Implicits.global

import cats.effect._
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.server.blaze.BlazeServerBuilder

import io.mocky.config.Config
import io.mocky.db.Database

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
    contextShift: ContextShift[IO]): IO[ExitCode] = {

    for {
      _ <- Database.initialize(resources.transactor)
      routing = new Routing().wire(resources)
      exitCode <-
        BlazeServerBuilder[IO](global)
          .bindHttp(resources.config.server.port, resources.config.server.host)
          .withHttpApp(routing)
          .serve
          .compile
          .drain
          .as(ExitCode.Success)
    } yield exitCode
  }

  final case class Resources(transactor: HikariTransactor[IO], config: Config)
}
