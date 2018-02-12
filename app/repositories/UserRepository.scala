package repositories

import com.google.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted

import scala.concurrent.Future

@Singleton
class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with UserTable {

  import profile.api._

  def save(user: User): Future[Int] = db.run(insUserTableQuery += user)

  def update(user: User): Future[Int] = db.run(userTableQuery.filter(_.id === user.id).update(user))

  def findOneByName(name: String): Future[Option[User]] =
    db.run(userTableQuery.filter(_.name === name).result.headOption)

  def findOne(id: Int): Future[Option[User]] = db.run(userTableQuery.filter(_.id === id).result.headOption)

  def findAll(): Future[List[User]] = db.run(userTableQuery.to[List].result)

  def delete(id: Int): Future[Int] = db.run(userTableQuery.filter(_.id === id).delete)
}

private[repositories] trait UserTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected val userTableQuery = lifted.TableQuery[UserTableHidden]
  protected val insUserTableQuery = userTableQuery returning userTableQuery.map(_.id)

  private[UserTable] class UserTableHidden(tag: Tag) extends Table[User](tag, "usersCreds") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.SqlType("VARCHAR(30)"), O.Unique)
    val hashedPassword: Rep[String] = column[String]("hashedPassword")

    def * = (id.?, name, hashedPassword) <>(User.tupled, User.unapply)
  }
}