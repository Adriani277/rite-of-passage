package com.rockthejvm.jobsboard.http.responses

import zio.json.*

final case class FailureResponse(error: String)
object FailureResponse:
  given JsonEncoder[FailureResponse] = DeriveJsonEncoder.gen[FailureResponse]
  given JsonDecoder[FailureResponse] = DeriveJsonDecoder.gen[FailureResponse]
