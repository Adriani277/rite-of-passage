package com.rockthejvm.jobsboard.playground

import zio._
import zio.interop.catz._
import zio.interop.catz.CatsApp
import doobie.*
import doobie.implicits.*
import doobie.util.*
import doobie.hikari.HikariTransactor
import com.zaxxer.hikari.HikariDataSource
import doobie.util.transactor.Transactor
import com.rockthejvm.jobsboard.domain.job.JobInfo

object JobsPlayground extends CatsApp:

  val postgres: ZIO[Scope, Throwable, Transactor[Task]] = for {
    ec <- ExecutionContexts.fixedThreadPool[Task](32).toScopedZIO
    xa <- HikariTransactor
            .newHikariTransactor[Task](
              "org.postgresql.Driver",
              "jdbc:postgresql:board",
              "docker",
              "docker",
              ec
            )
            .toScopedZIO
  } yield xa

  val jobInfo = JobInfo.minimal(
    company = "Rock the JVM",
    title = "Scala Developer",
    description = "We are looking for a Scala Developer",
    externalUrl = "https://rockthejvm.com",
    remote = true,
    location = "Remote"
  )

  override def run = ???
