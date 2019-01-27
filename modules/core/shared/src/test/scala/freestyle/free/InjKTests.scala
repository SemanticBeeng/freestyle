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

package freestyle
package free

import cats.data.EitherK
import iota._

import org.scalacheck._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Prop._

object InjKChecks {
  def roundTripInj[F[_], G[_], A](
      implicit
      arbFA: Arbitrary[F[A]],
      injK: InjK[F, G]): Prop =
    forAll((fa: F[A]) => injK.prj(injK.inj(fa)) ?= Some(fa))

  def roundTripPrj[F[_], G[_], A](
      implicit
      arbGA: Arbitrary[G[A]],
      injK: InjK[F, G]): Prop =
    forAll((ga: G[A]) => injK.prj(ga).map(fa => injK.inj(fa)) ?= Some(ga))
}

class InjKTests extends Properties("InjK") {
  import TListK.:::

  case class Foo[A](foo: A)
  case class Bar[A](bar: A)

  implicit def arbFoo[A](implicit arbA: Arbitrary[A]): Arbitrary[Foo[A]] =
    Arbitrary(arbA.arbitrary.map(Foo.apply))
  implicit def arbBar[A](implicit arbA: Arbitrary[A]): Arbitrary[Bar[A]] =
    Arbitrary(arbA.arbitrary.map(Bar.apply))

  object arbCoproduct {

    implicit def left[F[_], G[_], A](
        implicit
        arbFA: Arbitrary[F[A]]): Arbitrary[EitherK[F, G, A]] =
      Arbitrary(arbFA.arbitrary.map(v => EitherK.left(v)))

    implicit def right[F[_], G[_], A](
        implicit
        arbGA: Arbitrary[G[A]]): Arbitrary[EitherK[F, G, A]] =
      Arbitrary(arbGA.arbitrary.map(v => EitherK.right(v)))

    implicit def both[F[_], G[_], A](
        implicit
        arbFA: Arbitrary[F[A]],
        arbGA: Arbitrary[G[A]]): Arbitrary[EitherK[F, G, A]] =
      Arbitrary(arbitrary[Boolean].flatMap(toggle =>
        if (toggle) left[F, G, A].arbitrary else right[F, G, A].arbitrary))
  }

  object arbCopK {

    implicit def left[F[_], G[_], A](
        implicit
        arbFA: Arbitrary[F[A]]): Arbitrary[CopK[F ::: G ::: TNilK, A]] =
      Arbitrary(arbFA.arbitrary.map(v => CopK.unsafeApply(0, v)))

    implicit def right[F[_], G[_], A](
        implicit
        arbGA: Arbitrary[G[A]]): Arbitrary[CopK[F ::: G ::: TNilK, A]] =
      Arbitrary(arbGA.arbitrary.map(v => CopK.unsafeApply(1, v)))

    implicit def both[F[_], G[_], A](
        implicit
        arbFA: Arbitrary[F[A]],
        arbGA: Arbitrary[G[A]]): Arbitrary[CopK[F ::: G ::: TNilK, A]] =
      Arbitrary(arbitrary[Boolean].flatMap(toggle =>
        if (toggle) left[F, G, A].arbitrary else right[F, G, A].arbitrary))
  }

  property("roundtrip inj [EitherK]") = {
    type F[A] = Foo[A]
    type G[A] = EitherK[Foo, Bar, A]
    InjKChecks.roundTripInj[F, G, String]
  }

  property("roundtrip prj [EitherK, left]") = {
    type F[A] = Foo[A]
    type G[A] = EitherK[Foo, Bar, A]
    import arbCoproduct.left
    InjKChecks.roundTripPrj[F, G, String]
  }

  property("roundtrip prj [EitherK, right]") = {
    type F[A] = Bar[A]
    type G[A] = EitherK[Foo, Bar, A]
    import arbCoproduct.right
    InjKChecks.roundTripPrj[F, G, String]
  }

  property("roundtrip inj [CopK]") = {
    type F[A] = Foo[A]
    type G[A] = CopK[Foo ::: Bar ::: TNilK, A]
    InjKChecks.roundTripInj[F, G, String]
  }

  property("roundtrip prj [CopK, left]") = {
    type F[A] = Foo[A]
    type G[A] = CopK[Foo ::: Bar ::: TNilK, A]
    import arbCopK.left
    InjKChecks.roundTripPrj[F, G, String]
  }

  property("roundtrip prj [CopK, right]") = {
    type F[A] = Bar[A]
    type G[A] = CopK[Foo ::: Bar ::: TNilK, A]
    import arbCopK.right
    InjKChecks.roundTripPrj[F, G, String]
  }

}
