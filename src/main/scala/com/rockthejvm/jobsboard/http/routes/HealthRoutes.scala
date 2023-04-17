package com.rockthejvm.jobsboard.http.routes

import zio.interop.catz.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*

import zio._

object HealthRoutes extends Http4sDsl[Task]:
  private val healthRoute: HttpRoutes[Task] =
    HttpRoutes.of[Task] { case GET -> Root =>
      Ok("Healthy")
    }

  val routes = Router("/health" -> healthRoute)
