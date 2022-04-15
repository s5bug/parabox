package tf.bug.parabox

import indigo.*
import indigo.scenes.*

object MainMenu extends Scene[Unit, Unit, Unit] {
  
  override type SceneModel = Unit
  override type SceneViewModel = Unit

  override def name: SceneName = SceneName("Main Menu")

  override def modelLens: Lens[Unit, Unit] = Lens.identity

  override def viewModelLens: Lens[Unit, Unit] = Lens.identity

  override def eventFilters: EventFilters = EventFilters.AllowAll

  override def subSystems: Set[SubSystem] = Set()

  override def updateModel(context: FrameContext[Unit], model: Unit): GlobalEvent => Outcome[Unit] = _ => Outcome(model)

  override def updateViewModel(context: FrameContext[Unit], model: Unit, viewModel: Unit): GlobalEvent => Outcome[Unit] = _ => Outcome(viewModel)

  override def present(context: FrameContext[Unit], model: Unit, viewModel: Unit): Outcome[SceneUpdateFragment] = Outcome(SceneUpdateFragment.empty)
  
}
