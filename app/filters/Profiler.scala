package filters

import com.google.inject.{Inject, Singleton}
import play.api.Logger
import play.api.mvc.{EssentialAction, EssentialFilter, Filters}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext

@Singleton
class Profiler @Inject()(executionContext: ExecutionContext) extends EssentialFilter {
  override def apply(next: EssentialAction): EssentialAction = EssentialAction { request =>
    val start = System.currentTimeMillis()

    next(request) map { result =>
      val end = System.currentTimeMillis()
      Logger.info(s"Request to URL ${request.uri} - response in ${end - start} minutes")
      result
    }
  }
}
