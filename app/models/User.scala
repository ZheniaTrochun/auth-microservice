package models


case class User(id: Option[Int] = None, name: String, hashedPassword: String)

case class UserRegisterRequest(name: String, password: String, email: String) {
  def toUser(hashedPass: String = ""): models.User = User(name = this.name, hashedPassword = hashedPass)

  def toUserDto: models.UserDto = UserDto(this.name, this.email)
}

case class UserSignInRequest(name: String, password: String)

@SerialVersionUID(1L)
case class UserDto(name: String, email: String) extends Serializable
