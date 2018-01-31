package controllers

import javax.inject._

import models.{UserRegisterRequest, UserSignInRequest}
import play.api.libs.json.Json
import play.api.mvc._
import security.{JwtUtils, SecuredAuthenticator}
import services.UserService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, secured: SecuredAuthenticator,
                               userService: UserService, jwtUtils: JwtUtils)
  extends AbstractController(cc) {

  implicit val signUp = Json.reads[UserRegisterRequest]
  implicit val signIn = Json.reads[UserSignInRequest]

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = secured.JwtAuthentication {
    Ok("Your new application is ready.")
  }

  def register = Action.async { req =>
    val js = req.body.asJson.get

    val dto = Json.fromJson[UserRegisterRequest](js)

    userService.register(dto.get) flatMap { res =>
        Future.successful(Ok(res toString))
    }
  }

  def login = Action.async { req =>
    val userDto = Json.fromJson[UserSignInRequest](req.body.asJson.get).get
    userService.signIn(userDto) flatMap { res =>
      Future.successful(Ok(res toString).withHeaders("Authentication" -> jwtUtils.createToken(res, userDto.name)))
    }
  }
}
