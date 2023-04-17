package com.rockthejvm.jobsboard.domain

import java.util.UUID
import zio.json._

object job {
  final case class Job(
      id: UUID,
      date: Long,
      ownerEmail: String,
      jobInfo: JobInfo,
      active: Boolean = false
  )
  object Job {
    given decoder: JsonDecoder[Job]          = DeriveJsonDecoder.gen[Job]
    given encoder: zio.json.JsonEncoder[Job] = DeriveJsonEncoder.gen[Job]
  }

  final case class JobInfo(
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
      other: Option[String]
  )

  object JobInfo {
    given decoder: JsonDecoder[JobInfo]          = DeriveJsonDecoder.gen[JobInfo]
    given encoder: zio.json.JsonEncoder[JobInfo] = DeriveJsonEncoder.gen[JobInfo]

    val empty = JobInfo(
      company = "",
      title = "",
      description = "",
      externalUrl = "",
      salaryLo = None,
      salaryHi = None,
      currency = None,
      remote = false,
      location = "",
      country = None,
      tags = None,
      image = None,
      seniority = None,
      other = None
    )

    def minimal(
        company: String,
        title: String,
        description: String,
        externalUrl: String,
        remote: Boolean,
        location: String
    ) =
      JobInfo(
        company = company,
        title = title,
        description = description,
        externalUrl = externalUrl,
        salaryLo = None,
        salaryHi = None,
        currency = None,
        remote = remote,
        location = location,
        country = None,
        tags = None,
        image = None,
        seniority = None,
        other = None
      )
  }
}
