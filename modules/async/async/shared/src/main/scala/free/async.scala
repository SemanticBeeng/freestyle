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

package freestyle.free

import scala.concurrent._
import scala.util._
import freestyle.async._

object async {

  /** Async computation algebra. **/
  @free sealed trait AsyncM {
    def async[A](fa: Proc[A]): FS[A]
  }

  class Future2AsyncM[F[_]](implicit AC: AsyncContext[F], E: ExecutionContext)
    extends FSHandler[Future, F] {
    override def apply[A](future: Future[A]): F[A] = future2AsyncM[F, A](future)

  }

  trait FreeImplicits extends Implicits with Syntax {

    implicit def freeStyleAsyncMHandler[M[_]](
        implicit MA: AsyncContext[M]
    ): AsyncM.Handler[M] =
      new AsyncM.Handler[M] {
        def async[A](fa: Proc[A]): M[A] =
          MA.runAsync(fa)
      }
  }

  object implicits extends FreeImplicits
}
