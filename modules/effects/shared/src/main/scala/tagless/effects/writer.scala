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

import cats.mtl.FunctorTell

object writer {

  final class AccumulatorProvider[W] {

    @tagless(true) sealed abstract class WriterM {
      def writer[A](aw: (W, A)): FS[A]
      def tell(w: W): FS[Unit]
    }

    trait Implicits {

      implicit def freestyleWriterMHandler[M[_]](
          implicit FT: FunctorTell[M, W]): WriterM.Handler[M] =
        new WriterM.Handler[M] {
          def writer[A](aw: (W, A)): M[A] = FT.tuple(aw)
          def tell(w: W): M[Unit]         = FT.tell(w)
        }

    }

    object implicits extends Implicits
  }

  def apply[W] = new AccumulatorProvider[W]

}
