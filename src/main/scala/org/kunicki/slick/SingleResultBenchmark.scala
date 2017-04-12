package org.kunicki.slick

import com.danielasfregola.randomdatagenerator.RandomDataGenerator
import org.openjdk.jmh.annotations.{Benchmark, BenchmarkMode, Mode, Param, Scope, Setup, State}
import slick.jdbc.H2Profile.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


case class User(id: Int, name: String)

class Users(tag: Tag) extends Table[User](tag, Users.TableName) {

  def id = column[Int]("id")

  def name = column[String]("name")

  def * = (id, name) <> (User.tupled, User.unapply)
}

object Users {

  val TableName = "users"
}

@State(Scope.Benchmark)
class SingleResultBenchmark extends RandomDataGenerator {

  private val db = Database.forConfig("h2")
  private val users = TableQuery[Users]

  private[slick] val queries = Map(
    "take" -> users.take(1).result.head,
    "head" -> users.result.head
  )

  @Param(Array("take", "head"))
  var queryType: String = ""

  @Param(Array("10000", "50000", "100000", "500000"))
  var numberOfRecords: Int = 0

  @Setup
  def prepare(): Unit = {
    val result = for {
      schemaExists <- db.run(MTable.getTables(Users.TableName).headOption.map(_.nonEmpty))
      _ <- if (schemaExists) Future.successful() else db.run(users.schema.create)
      _ <- db.run(users.delete)
      _ <- db.run(users ++= random[User](numberOfRecords))
    } yield ()

    Await.ready(result, Duration.Inf)
  }

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  def query(): Unit = Await.ready(db.run(queries(queryType)), Duration.Inf)
}
