package com.rockthejvm.jobsboard.core
import com.rockthejvm.jobsboard.domain.job.*
import zio.{ System => _, _ }
import zio.interop.catz.*
import java.util.UUID
import doobie.*
import doobie.implicits.*
import doobie.postgres.implicits.*
import doobie.util.*
import doobie.util.Read

trait Jobs:
  // create crud methods for jobs using zio
  def create(ownerEmail: String, jobInfo: JobInfo): Task[UUID]
  val all: Task[List[Job]]
  def find(id: UUID): Task[Option[Job]]
  def update(id: UUID, jobInfo: JobInfo): Task[Option[Job]]
  def delete(id: UUID): Task[Int]

  /*
      id: UUID,
      date: Long,
      ownerEmail: String,
      company: String,
      title: String,
      description: String,
      externalUrl: String,
      salaryLo: Option[Int],
      salaryHi: Option[Int],
      currency: Option[String],
      remote: Boolean,
      location: String,
      country: Option[String],
      tags: Option[List[String]],
      image: Option[String],
      seniority: Option[String],
      other: Option[String],
      active: Boolean
   */
final case class LiveJobs private (xa: Transactor[Task]) extends Jobs:
  def create(ownerEmail: String, jobInfo: JobInfo): Task[UUID] =
    sql"""
         |INSERT INTO jobs (
         |date,
         |ownerEmail,
         |company,
         |title,
         |description,
         |externalUrl,
         |salaryLo,
         |salaryHi,
         |currency,
         |remote,
         |location,
         |country,
         |tags,
         |image,
         |seniority,
         |other,
         |active
         |) VALUES (
         |${System.currentTimeMillis()},
         |$ownerEmail,
         |${jobInfo.company},
         |${jobInfo.title},
         |${jobInfo.description},
         |${jobInfo.externalUrl},
         |${jobInfo.salaryLo},
         |${jobInfo.salaryHi},
         |${jobInfo.currency},
         |${jobInfo.remote},
         |${jobInfo.location},
         |${jobInfo.country},
         |${jobInfo.tags},
         |${jobInfo.image},
         |${jobInfo.seniority},
         |${jobInfo.other},
         |false
         |)
    """".stripMargin.update
      .withUniqueGeneratedKeys[UUID]("id")
      .transact(xa)

  val all: Task[List[Job]]              =
    sql"""
         |SELECT
         |    id,
         |    date,
         |    ownerEmail,
         |    company,
         |    title,
         |    description,
         |    externalUrl,
         |    salaryLo,
         |    salaryHi,
         |    currency,
         |    remote,
         |    location,
         |    country,
         |    tags,
         |    image,
         |    seniority,
         |    other,
         |    active
         |FROM jobs
    """.stripMargin.query[Job].to[List].transact(xa)
  def find(id: UUID): Task[Option[Job]] =
    sql"""
         |SELECT
         |    id,
         |    date,
         |    ownerEmail,
         |    company,
         |    title,
         |    description,
         |    externalUrl,
         |    salaryLo,
         |    salaryHi,
         |    currency,
         |    remote,
         |    location,
         |    country,
         |    tags,
         |    image,
         |    seniority,
         |    other,
         |    active
         |FROM jobs
         |WHERE id = $id
    """.stripMargin.query[Job].option.transact(xa)

  def update(id: UUID, jobInfo: JobInfo): Task[Option[Job]] =
    sql"""
         |UPDATE jobs
         |SET
         |    company = ${jobInfo.company},
         |    title = ${jobInfo.title},
         |    description = ${jobInfo.description},
         |    externalUrl = ${jobInfo.externalUrl},
         |    salaryLo = ${jobInfo.salaryLo},
         |    salaryHi = ${jobInfo.salaryHi},
         |    currency = ${jobInfo.currency},
         |    remote = ${jobInfo.remote},
         |    location = ${jobInfo.location},
         |    country = ${jobInfo.country},
         |    tags = ${jobInfo.tags},
         |    image = ${jobInfo.image},
         |    seniority = ${jobInfo.seniority},
         |    other = ${jobInfo.other}
         |WHERE id = $id
    """.stripMargin.update.run.transact(xa) *> (find(id))

  def delete(id: UUID): Task[Int] =
    sql"""
         |DELETE FROM jobs
         |WHERE id = $id
    """.stripMargin.update.run.transact(xa)

object LiveJobs:
  given jobRead: Read[Job] = Read[
    (
        UUID,
        Long,
        String,
        String,
        String,
        String,
        String,
        Option[Int],
        Option[Int],
        Option[String],
        Boolean,
        String,
        Option[String],
        Option[List[String]],
        Option[String],
        Option[String],
        Option[String],
        Boolean
    )
  ].map {
    case (
          id,
          date,
          ownerEmail,
          company,
          title,
          description,
          externalUrl,
          salaryLo,
          salaryHi,
          currency,
          remote,
          location,
          country,
          tags,
          image,
          seniority,
          other,
          active
        ) =>
      Job(
        id,
        date,
        ownerEmail,
        JobInfo(
          company,
          title,
          description,
          externalUrl,
          salaryLo,
          salaryHi,
          currency,
          remote,
          location,
          country,
          tags,
          image,
          seniority,
          other
        ),
        active
      )
  }
