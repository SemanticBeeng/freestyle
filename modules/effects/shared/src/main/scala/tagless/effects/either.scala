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
package effects

import cats.{Eval, MonadError}

import scala.util.control.NonFatal

object either {

  final class ErrorProvider[E] {

    @tagless(true) sealed trait EitherM {
      def either[A](fa: Either[E, A]): FS[A]
      def error[A](e: E): FS[A]
      def catchNonFatal[A](a: Eval[A], f: Throwable => E): FS[A]
    }

    trait Implicits {
      implicit def freeStyleEitherMHandler[M[_]](
          implicit ME: MonadError[M, E]): EitherM.Handler[M] = new EitherM.Handler[M] {
        def either[A](fa: Either[E, A]): M[A] = ME.fromEither(fa)
        def error[A](e: E): M[A]              = ME.raiseError[A](e)
        def catchNonFatal[A](a: Eval[A], f: Throwable => E): M[A] =
          try ME.pure(a.value)
          catch {
            case NonFatal(e) => ME.raiseError(f(e))
          }
      }

      implicit class EitherFSLift[A](fa: Either[E, A])  {
        def liftF[F[_]: EitherM]: F[A] = EitherM[F].either(fa)
      }
    }

    object implicits extends Implicits
  }

  def apply[E]: ErrorProvider[E] = new ErrorProvider
}
