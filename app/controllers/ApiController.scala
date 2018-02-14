package controllers

import com.google.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import security.SecuredAuthenticator

import scala.language.postfixOps

@Singleton
class ApiController @Inject()(config: Configuration, securedAuth: SecuredAuthenticator, cc: ControllerComponents, ws: WSClient) extends AbstractController(cc) {

  def securedWithRedirect(path: String) = securedAuth.JwtAuthentication { req =>
    Redirect(config.underlying.getString(path))
      .withHeaders("user" -> req.headers.get("user").get,
        "Sertificate" -> config.underlying.getString("api.sertificate"))
  }

  def free(path: String) = Action { req =>
    Redirect(config.underlying.getString(path))
      .withHeaders("Sertificate" -> config.underlying.getString("api.sertificate"))
  }
}
