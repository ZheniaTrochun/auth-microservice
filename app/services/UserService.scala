package services

import com.google.inject.{Inject, Singleton}
import models.{User, UserDto, UserRegisterRequest, UserSignInRequest}
import repositories.UserRepository
import com.github.t3hnar.bcrypt._
import exceptions.InvalidCredsException
import play.api.{Configuration, Logger}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

trait UserService {
  def register(userRequest: UserRegisterRequest): Future[Int]

  def signIn(userRequest: UserSignInRequest): Future[Int]
}

@Singleton
class UserServiceImpl @Inject()(userRepository: UserRepository, configuration: Configuration, ws: WSClient)
  extends UserService {

  private implicit val writable = Json.writes[UserDto]

  def register(userRequest: UserRegisterRequest): Future[Int] = {
    val hashedPassword = userRequest.password.bcrypt

    ws.url(configuration.underlying.getString("api.users.create"))
      .withHttpHeaders("Sertificate" -> configuration.underlying.getString("api.sertificate"))
        .withRequestTimeout(5 second)
          .post(Json.toJson(userRequest.toUserDto))
            .flatMap {resp: WSResponse =>

              if (resp.status == 200)
                userRepository.save(userRequest.toUser(hashedPassword)) flatMap { id: Int =>
                  Logger.info(s"User saved id = $id")
                  Future.successful(id)
                }

              else
                Future.failed(new InternalError())
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
