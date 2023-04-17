package com.rockthejvm.jobsboard

import zio.interop.catz.*
import zio.interop.catz.implicits.*
import zio.*
import http.HttpApi

import org.http4s.ember.server.EmberServerBuilder
import com.rockthejvm.jobsboard.config.EmberConfig

object Application extends CatsApp:
  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] =
    ZIO
      .service[EmberConfig]
      .flatMap { config =>
        EmberServerBuilder
          .default[Task]
          .withHost(config.host)
          .withPort(config.port)
          .withHttpApp(HttpApi.endpoints.orNotFound)
          .build
          .toScopedZIO
          .flatMap(_ => ZIO.log("Server started") *> ZIO.never)
      }
      .provideSome[zio.Scope](EmberConfig.layer)
      .catchAllCause(err => ZIO.logErrorCause(s"Error running the application", err))
