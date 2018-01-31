package services

import akka.util.ByteString
import com.google.inject.{Inject, Singleton}
import models.{User, UserDto, UserRegisterRequest, UserSignInRequest}
import repositories.UserRepository
import com.github.t3hnar.bcrypt._
import exceptions.InvalidCredsException
import play.api.Configuration
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UserService @Inject()(userRepository: UserRepository, configuration: Configuration, ws: WSClient) {

  private implicit val writable = Json.writes[UserDto]

  def register(userRequest: UserRegisterRequest): Future[Int] = {
    val hashedPassword = userRequest.password.bcrypt
    val userCreds: User = userRequest.toUser(hashedPassword)

    userRepository.save(userCreds) flatMap {
      id: Int =>
//        ws.url(configuration.underlying.getString("api.users.create"))
//          .withHttpHeaders("Sertificate" -> configuration.underlying.getString("api.sertificate"))
//          .post(Json.toJson(userRequest.toUserDto(id)))

        Future.successful(id)
    }
  }

  def signIn(userRequest: UserSignInRequest): Future[Int] = {
    userRepository.findOneByName(userRequest.name) flatMap {
      case Some(user) =>
        if (userRequest.password.isBcrypted(user.hashedPassword))
          Future.successful(user.id.get)
        else
          throw new InvalidCredsException

      case None =>
        throw new InvalidCredsException
    }
  }
}
