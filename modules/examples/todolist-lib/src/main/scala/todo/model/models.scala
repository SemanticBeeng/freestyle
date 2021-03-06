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

sealed abstract class Entity extends Product with Serializable {
  def id: Option[Int]
}

final case class Tag(name: String, id: Option[Int] = None) extends Entity

final case class TodoForm(list: TodoList, tag: Tag, items: List[TodoItem])

final case class TodoItem(
    item: String,
    todoListId: Option[Int] = None,
    completed: Boolean = false,
    id: Option[Int] = None)
    extends Entity

final case class TodoList(title: String, tagId: Option[Int], id: Option[Int] = None) extends Entity
