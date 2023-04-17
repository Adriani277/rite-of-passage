package com.rockthejvm.jobsboard.config

import zio.config._
import zio.config.magnolia._
import zio.config.typesafe._
import zio.ConfigProvider
import zio.ZLayer
import com.comcast.ip4s.{ Host, Port }
import zio.Config
import zio.Chunk

final case class EmberConfig(host: Host, port: Port)

object EmberConfig:
  val layer: ZLayer[Any, Config.Error, EmberConfig] = ZLayer.fromZIO(
    ConfigProvider
      .fromHoconFile(new java.io.File("src/main/resources/application.conf"))
      .load(deriveConfig[EmberConfig])
  )

  given hostConfig: DeriveConfig[Host] = DeriveConfig[String].mapOrFail(Host.fromString(_) match
    case None        => Left(zio.Config.Error.InvalidData(Chunk("host"), "Invalid host"))
    case Some(value) => Right(value)
  )

  given portConfig: DeriveConfig[Port] = DeriveConfig[Int].mapOrFail(Port.fromInt(_) match
    case None        => Left(zio.Config.Error.InvalidData(Chunk("port"), "Invalid port"))
    case Some(value) => Right(value)
  )
