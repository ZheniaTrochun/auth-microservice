package security

import authentikat.jwt.{JsonWebToken, JwtClaimsSet, JwtHeader}
import com.google.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class JwtUtils @Inject()(configuration: Configuration) {
  private lazy val algo = configuration.underlying.getString("security.algo")
  private lazy val secret = configuration.underlying.getString("security.secret")

  def createToken(userId: Int, username: String): String = {
    val header = JwtHeader(algo)
    val claimsSet = JwtClaimsSet(Map("userId" -> userId, "username" -> username))

    JsonWebToken(header, claimsSet, secret)
  }

  def isValidToken(jwt: String): Boolean = JsonWebToken.validate(jwt, secret)

  def decodeUserId(jwt: String): Option[Int] =
    jwt match {
      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asSimpleMap.get("userId").toInt)

      case _ => None
    }

  def decodeUsername(jwt: String): Option[String] =
    jwt match {
      case JsonWebToken(header, claimsSet, signature) => Option(claimsSet.asSimpleMap.get("username"))

      case _ => None
    }

  def decodeUserData(jwt: String): Option[(Int, String)] =
    jwt match {
      case JsonWebToken(header, claimsSet, signature) =>
        Option(claimsSet.asSimpleMap.get("userId").toInt -> claimsSet.asSimpleMap.get("username"))

      case _ => None
    }
}
