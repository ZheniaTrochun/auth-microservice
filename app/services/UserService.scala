package services

import akka.util.ByteString
import com.google.inject.{Inject, Singleton}
import models.{User, UserDto, UserRegisterRequest, UserSignInRequest}
import repositories.UserRepository
import com.github.t3hnar.bcrypt._
import exceptions.InvalidCredsException
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.libs.Json

import scala.concurrent.Await
import scala.concurrent.duration.Duration

@Singleton
class UserService @Inject()(userRepository: UserRepository, configuration: Configuration, ws: WSClient) {

  def register(userRequest: UserRegisterRequest): Int = {
    val hashedPassword = userRequest.password.bcrypt

    val userCreds: User = userRequest.toUser(hashedPassword)

    val userId = Await.result(userRepository.save(userCreds), Duration.Inf)

    val userDto = userRequest.toUserDto(userId)

    ws.url(configuration.underlying.getString("api.users.create"))
      .withHttpHeaders("Sertificate" -> configuration.underlying.getString("api.sertificate"))
      .post(Json.toJson(userDto).asText())

    userId
  }

  def signIn(userRequest: UserSignInRequest): Int = {
    val user: User = Await.result(userRepository.findOneByName(userRequest.name), Duration.Inf)
      .getOrElse(throw new InvalidCredsException)

    if (userRequest.password.isBcrypted(user.hashedPassword))
      user.id.get
    else
      throw new InvalidCredsException
  }
}
