package security

import com.google.inject.{Inject, Singleton}
import models.User
import play.api.mvc._
import play.api.mvc.Results.{Ok, Unauthorized}
import repositories.UserRepository

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class SecuredAuthenticator @Inject()(cc: ControllerComponents, jwtUtils: JwtUtils, userRepository: UserRepository) {

  object JwtAuthentication
    extends ActionBuilder[Request, AnyContent] {

    override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser

    override protected def executionContext: ExecutionContext = cc.executionContext

    override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
      implicit val req: Request[A] = request

      val jwt = request.headers.get("Authentication").getOrElse("")

      if (jwtUtils.isValidToken(jwt)) {
        jwtUtils.decodeUserId(jwt)
          .fold(Future.successful(Unauthorized("Invalid credentials!"))) { userId =>

  //          done this way to increase speed
            val userFuture: Future[Option[User]] = userRepository.findOne(userId)
            val username = jwtUtils.decodeUsername(jwt).get
            val user = Await.result(userFuture, Duration.Inf)

            if (user.isDefined && user.get.name == username) {
              Future.successful(Ok("Authorization successful!").withHeaders("user" -> user.get.name))
            } else {
              Future.successful(Unauthorized("Invalid credentials!"))
            }
        }
      } else {
        Future.successful(Unauthorized("Invalid credentials!"))
      }
    }
  }

  def async[A](action: Action[A]) = Action.async(action.parser) { request =>
    val jwt = request.headers.get("Authentication").getOrElse("")

    jwtUtils.isValidToken(jwt) match {
      case true =>
        jwtUtils.decodeUserId(jwt)
          .fold(Future.successful(Unauthorized("Invalid credentials!"))) { userId =>

            val username = jwtUtils.decodeUsername(jwt).get
            val userFuture: Future[Option[User]] = userRepository.findOne(userId)
            val user = Await.result(userFuture, Duration.Inf)

            if (user.isDefined && user.get.name == username) {
              action(request)
            } else {
              Future.successful(Unauthorized("Invalid credentials!"))
            }
          }

      case false =>
        Future.successful(Unauthorized)
    }
  }
}
