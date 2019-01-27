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

package freestyle.tagless

import cats.Monad
import cats.instances.future._
import cats.syntax.flatMap._
import cats.syntax.functor._
import freestyle.async.AsyncContext
import freestyle.tagless._
import freestyle.tagless.async._
import freestyle.tagless.async.implicits._
import org.scalatest._

import scala.concurrent.{ExecutionContext, Future}

class AsyncTests extends AsyncWordSpec with Matchers {

  implicit override def executionContext = ExecutionContext.Implicits.global

  "Async Freestyle integration" should {
    "allow an Async to be interleaved inside a program monadic flow" in {

      def program[F[_]: Monad: AsyncM] =
        {
          for {
            a <- Monad[F].pure(1)
            b <- AsyncM[F].async[Int](cb => cb(Right(42)))
            c <- Monad[F].pure(1)
          } yield a + b + c
        }

      program[Future] map { _ shouldBe 44 }
    }

    "allow multiple Async to be interleaved inside a program monadic flow" in {
      def program[F[_]: Monad: AsyncM] =
        for {
          a <- Monad[F].pure(1)
          b <- AsyncM[F].async[Int](cb => cb(Right(42)))
          c <- Monad[F].pure(1)
          d <- AsyncM[F].async[Int](cb => cb(Right(10)))
        } yield a + b + c + d

      program[Future] map { _ shouldBe 54 }
    }

    case class OhNoException() extends Exception

    "allow Async errors to short-circuit a program" in {
      def program[F[_]: Monad: AsyncM] =
        for {
          a <- Monad[F].pure(1)
          b <- AsyncM[F].async[Int](cb => cb(Left(OhNoException())))
          c <- Monad[F].pure(3)
        } yield a + b + c

      program[Future] recover { case OhNoException() => 42 } map { _ shouldBe 42 }
    }

  }
}
