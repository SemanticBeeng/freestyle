/*
 * Copyright 2017-2019 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package examples.todolist
package http

import cats._
import cats.implicits._
import com.twitter.util.Future
import examples.todolist.model.Pong
import freestyle.tagless.logging.LoggingM
import io.finch._

class GenericApi[F[_]: Monad](implicit log: LoggingM[F], handler: F ~> Future) {

  import io.finch.syntax._

  val ping = get("ping") {
    handler(
      for {
        _ <- log.error("Not really an error")
        _ <- log.warn("Not really a warn")
        _ <- log.debug("GET /ping")
      } yield Ok(Pong.current)
    )
  }

  val hello = get("hello") {
    handler(
      for {
        _ <- log.error("Not really an error")
        _ <- log.warn("Not really a warn")
        _ <- log.debug("GET /Hello")
      } yield Ok("Hello World")
    )
  }

  val endpoints = hello :+: ping
}

object GenericApi {
  implicit def instance[F[_]: Monad](
      implicit log: LoggingM[F],
      handler: F ~> Future): GenericApi[F] =
    new GenericApi[F]
}
