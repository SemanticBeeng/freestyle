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

package examples.todolist.persistence.runtime

import cats.Monad
import doobie.implicits._
import doobie.util.transactor.Transactor
import examples.todolist.TodoList
import examples.todolist.persistence.TodoListRepository

class TodoListRepositoryHandler[F[_]: Monad](implicit T: Transactor[F])
    extends TodoListRepository.Handler[F] {

  import examples.todolist.persistence.runtime.queries.TodoListQueries._

  def insert(item: TodoList): F[Option[TodoList]] =
    insertQuery(item)
      .withUniqueGeneratedKeys[Int]("id")
      .flatMap(getQuery(_).option)
      .transact(T)

  def get(id: Int): F[Option[TodoList]] =
    getQuery(id).option.transact(T)

  def update(input: TodoList): F[Option[TodoList]] =
    updateQuery(input).run
      .flatMap(_ => getQuery(input.id.get).option)
      .transact(T)

  def delete(id: Int): F[Int] =
    deleteQuery(id).run.transact(T)

  def list: F[List[TodoList]] =
    listQuery
      .to[List]
      .transact(T)

  def drop: F[Int] =
    dropQuery.run.transact(T)

  def create: F[Int] =
    createQuery.run.transact(T)

  def init: F[Int] =
    dropQuery.run
      .flatMap(
        drops =>
          createQuery.run
            .map(_ + drops))
      .transact(T)
}
