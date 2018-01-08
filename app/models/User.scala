package models


case class User(id: Option[Int] = None, name: String, hashedPassword: String)

case class UserRegisterRequest(name: String, password: String, email: Option[String], workField: Option[String]) {
  def toUser(hashedPass: String = ""): models.User = User(name = this.name, hashedPassword = hashedPass)

  def toUserDto(id: Int = 0): models.UserDto = UserDto(id, this.name, this.email, this.workField)
}

case class UserSignInRequest(name: String, password: String)

@SerialVersionUID(1L)
case class UserDto(id: Int, name: String, email: Option[String], workField: Option[String]) extends Serializable
