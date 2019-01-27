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

package freestyle.tagless.loggingJVM

import cats.Applicative
import freestyle.logging._
import freestyle.tagless.logging._
import org.log4s._

object log4s {

  sealed abstract class TaglessLoggingMHandler[M[_]] extends LoggingM.Handler[M] {

    import sourcecode.{File, Line}

    protected def withLogger[A](f: Logger => A): M[A]

    def debug(msg: String, srcInfo: Boolean)(implicit line: Line, file: File): M[Unit] =
      withLogger(_.debug(formatMessage(msg, srcInfo, line, file)))

    def debugWithCause(msg: String, cause: Throwable, srcInfo: Boolean)(
        implicit
        line: Line,
        file: File): M[Unit] =
      withLogger(_.debug(cause)(formatMessage(msg, srcInfo, line, file)))

    def error(msg: String, srcInfo: Boolean)(implicit line: Line, file: File): M[Unit] =
      withLogger(_.error(formatMessage(msg, srcInfo, line, file)))

    def errorWithCause(msg: String, cause: Throwable, srcInfo: Boolean)(
        implicit
        line: Line,
        file: File): M[Unit] =
      withLogger(_.error(cause)(formatMessage(msg, srcInfo, line, file)))

    def info(msg: String, srcInfo: Boolean)(implicit line: Line, file: File): M[Unit] =
      withLogger(_.info(formatMessage(msg, srcInfo, line, file)))

    def infoWithCause(msg: String, cause: Throwable, srcInfo: Boolean)(
        implicit
        line: Line,
        file: File): M[Unit] =
      withLogger(_.info(cause)(formatMessage(msg, srcInfo, line, file)))

    def warn(msg: String, srcInfo: Boolean)(implicit line: Line, file: File): M[Unit] =
      withLogger(_.warn(formatMessage(msg, srcInfo, line, file)))

    def warnWithCause(msg: String, cause: Throwable, srcInfo: Boolean)(
        implicit
        line: Line,
        file: File): M[Unit] =
      withLogger(_.warn(cause)(formatMessage(msg, srcInfo, line, file)))

  }

  trait Implicits {
    implicit def taglessLoggingApplicative[M[_]: Applicative](
        implicit log: Logger = getLogger("")): LoggingM.Handler[M] = new TaglessLoggingMHandler[M] {

      protected def withLogger[A](f: Logger => A): M[A] = Applicative[M].pure(f(log))

    }
  }

  object implicits extends Implicits
}
