package tf.bug.parabox

import indigo.*
import indigo.scenes.*
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("IndigoGame")
object Game extends IndigoGame[Unit, Unit, GameState, Unit] {

  override def scenes(bootData: Unit): NonEmptyList[Scene[Unit, GameState, Unit]] = NonEmptyList(InPuzzle)

  override def initialScene(bootData: Unit): Option[SceneName] = Some(InPuzzle.name)

  override def eventFilters: EventFilters = EventFilters.AllowAll
  
  override def boot(flags: Map[String, String]): Outcome[BootResult[Unit, GameState]] =
    Outcome(
      BootResult.noData(
        GameConfig(1600, 900)
      ).withAssets(Set(
        AssetType.Image(AssetName("blank_pixel"), AssetPath("./assets/blank_pixel.png")),
      ))
    )

  override def setup(bootData: Unit, assetCollection: AssetCollection, dice: Dice): Outcome[Startup[Unit]] = Outcome(Startup.Success(()))

  override def initialModel(startupData: Unit): Outcome[GameState] = Outcome(GameState.default)

  override def initialViewModel(startupData: Unit, model: GameState): Outcome[Unit] = Outcome(())
  
  override def updateModel(context: Context[Unit], model: GameState): GlobalEvent => Outcome[GameState] = _ => Outcome(model)

  override def updateViewModel(context: Context[Unit], model: GameState, viewModel: Unit): GlobalEvent => Outcome[Unit] = _ => Outcome(())
  
  override def present(context: Context[Unit], model: GameState, viewModel: Unit): Outcome[SceneUpdateFragment] = Outcome(SceneUpdateFragment.empty)

}
