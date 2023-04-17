package com.rockthejvm.jobsboard.http

import zio.interop.catz.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import cats.implicits.*
import com.rockthejvm.jobsboard.http.routes.{ HealthRoutes, JobRoutes }

object HttpApi:
  private val healthRoutes = HealthRoutes.routes
  private val jobRoutes    = JobRoutes.routes

  val endpoints = Router("/api" -> (healthRoutes <+> jobRoutes))
