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
package tagless

import freestyle.tagless.internal._

import scala.meta._
import scala.annotation.{StaticAnnotation, compileTimeOnly}

@compileTimeOnly("enable macro paradise to expand @tagless macro annotations")
class tagless(val stacksafe: Boolean) extends StaticAnnotation {
  import scala.meta._

  inline def apply(defn: Any): Any = meta {
    val stacksafe: Boolean = this match {
      case q"new $_(${Lit.Boolean(ss)})" => ss
      case q"new $_(stacksafe = ${Lit.Boolean(ss)})" => ss
      case _ => false
    }
    taglessImpl.tagless(defn, stacksafe)
  }
}

@compileTimeOnly("enable macro paradise to expand @module macro annotations")
class module extends StaticAnnotation {
  import scala.meta._

  inline def apply(defn: Any): Any = meta { moduleImpl.module(defn) }
}