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

package freestyle.async
package guava

import com.google.common.util.concurrent._
import java.util.concurrent.{Executor => JavaExecutor}

import scala.concurrent.ExecutionContext

trait AsyncGuavaImplicits {

  implicit def listenableFuture2Async[F[_], A](
      fa: => ListenableFuture[A])(implicit AC: AsyncContext[F], E: ExecutionContext): F[A] =
    AC.runAsync { cb =>
      Futures.addCallback(
        fa,
        new FutureCallback[A] {
          override def onSuccess(result: A): Unit = cb(Right(result))

          override def onFailure(t: Throwable): Unit = cb(Left(t))
        },
        new JavaExecutor {
          override def execute(command: Runnable): Unit = E.execute(command)
        }
      )
    }

  def listenableVoidToListenableUnit(fa: => ListenableFuture[Void])(
      implicit E: ExecutionContext): ListenableFuture[Unit] =
    Futures.transformAsync(
      fa,
      new AsyncFunction[Void, Unit] {
        override def apply(input: Void): ListenableFuture[Unit] =
          Futures.immediateFuture((): Unit)
      },
      new JavaExecutor {
        override def execute(command: Runnable): Unit = E.execute(command)
      }
    )

}

object implicits extends AsyncGuavaImplicits
