package com.rockthejvm.jobsboard.http.routes

import zio.interop.catz.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.dsl.impl.*
import org.http4s.server.*
import cats.implicits.*

import zio._
import scala.collection.mutable
import java.util.UUID
import com.rockthejvm.jobsboard.domain.job.*
import zio.json._
import zio.json.interop.http4s.ZIOEntityCodec.*
import com.rockthejvm.jobsboard.http.responses.*
import java.util.concurrent.TimeUnit

object JobRoutes extends Http4sDsl[Task]:
  private val database = mutable.Map.empty[UUID, Job]

  private val allJobsRoutes: HttpRoutes[Task] =
    HttpRoutes.of[Task] { case POST -> Root =>
      Ok(database.values)
    }

  private val findJobRoutes: HttpRoutes[Task] = HttpRoutes.of[Task] { case GET -> Root / UUIDVar(id) =>
    database.get(id) match
      case Some(job) => Ok(job)
      case None      => NotFound(FailureResponse(s"Job with id $id not found"))
  }

  private def createJob(jobInfo: JobInfo): Task[Job] =
    for {
      id   <- zio.Random.nextUUID
      date <- zio.Clock.currentTime(TimeUnit.MILLISECONDS)
    } yield Job(
      id = id,
      date = date,
      ownerEmail = "TODO",
      jobInfo = jobInfo,
      active = true
    )

  private val createJobRoutes: HttpRoutes[Task] = HttpRoutes.of[Task] { case req @ POST -> Root / "create" =>
    for {
      _        <- ZIO.log("Creating job")
      jobInfo  <- req.as[JobInfo].tapError(err => ZIO.logError(s"Error parsing job info: $err"))
      job      <- createJob(jobInfo)
      _        <- ZIO.succeed(database.put(job.id, job))
      _        <- ZIO.log(s"Created job: $job")
      response <- Created(job)
    } yield response
  }
  private val updateJobRoutes: HttpRoutes[Task] = HttpRoutes.of[Task] { case req @ PUT -> Root / UUIDVar(id) =>
    database.get(id) match {
      case Some(job) =>
        for {
          jobInfo  <- req.as[JobInfo]
          _         = ZIO.succeed(database.update(id, job.copy(jobInfo = jobInfo)))
          response <- Ok()
        } yield response

      case None => NotFound(FailureResponse(s"cannot update job $id not found"))
    }
  }

  private val deleteJobRoutes: HttpRoutes[Task] = HttpRoutes.of[Task] { case req @ DELETE -> Root / UUIDVar(id) =>
    database.get(id) match {
      case Some(job) =>
        for {
          _        <- ZIO.succeed(database.remove(id))
          response <- Ok()
        } yield response

      case None => NotFound(FailureResponse(s"cannot delete job $id not found"))
    }
  }

  val routes = Router(
    "/jobs" -> (allJobsRoutes <+> findJobRoutes <+> createJobRoutes <+> updateJobRoutes <+> deleteJobRoutes)
  )
