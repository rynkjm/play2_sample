import play.api.Logger
import play.api.GlobalSettings
import play.api.Application

object Global extends GlobalSettings {
  
  /**
   * 起動時
   */
  override def onStart(app: Application) {
    Logger.info("Application has started")
  }
  
  /**
   * 終了時
   */
  override def onStop(app: Application) {
    Logger.info("Application shutdown ...")
  }
}