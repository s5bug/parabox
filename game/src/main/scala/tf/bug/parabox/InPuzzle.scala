package tf.bug.parabox

import indigo.*
import indigo.scenes.*
import scala.annotation.tailrec

object InPuzzle extends Scene[Unit, GameState, Unit] {

  val handledColors: List[BoxColor] = List(
    BoxColor.Pink,
    BoxColor.Blue,
    BoxColor.Orange
  )

  case class BoxImageEffects(
    wallEffects: Material.ImageEffects,
    emptyEffects: Material.ImageEffects,
  )

  val colorEffects: Map[BoxColor, BoxImageEffects] = handledColors.map { color =>
    val (wr, wg, wb, wa) = color.wallRgba
    val (er, eg, eb, ea) = color.emptyRgba
    color -> BoxImageEffects(
      wallEffects = Material.ImageEffects(AssetName("blank_pixel")).withTint(RGBA(wr, wg, wb, wa)),
      emptyEffects = Material.ImageEffects(AssetName("blank_pixel")).withTint(RGBA(er, eg, eb, ea))
    )
  }.toMap

  case class BoxCloneBlanks(
    wallClone: CloneBlank,
    emptyClone: CloneBlank
  )

  val cloneBlanks: Map[BoxColor, BoxCloneBlanks] = colorEffects.map {
    case (color, BoxImageEffects(wallEffects, emptyEffects)) =>
      color -> BoxCloneBlanks(
        CloneBlank(CloneId(s"$color-wall"), Graphic(1, 1, wallEffects)),
        CloneBlank(CloneId(s"$color-empty"), Graphic(1, 1, emptyEffects))
      )
  }

  override type SceneModel = GameState
  override type SceneViewModel = Unit

  override def name: SceneName = SceneName("In Puzzle")

  override def modelLens: Lens[GameState, GameState] = Lens.keepLatest

  override def viewModelLens: Lens[Unit, Unit] = Lens.keepLatest

  override def eventFilters: EventFilters = EventFilters.AllowAll

  override def subSystems: Set[SubSystem[GameState]] = Set()
  
  override def updateModel(context: SceneContext[Unit], model: GameState): GlobalEvent => Outcome[GameState] = {
    case KeyboardEvent.KeyDown(Key.ARROW_UP) => Outcome(model.move(Direction.Up))
    case KeyboardEvent.KeyDown(Key.ARROW_DOWN) => Outcome(model.move(Direction.Down))
    case KeyboardEvent.KeyDown(Key.ARROW_LEFT) => Outcome(model.move(Direction.Left))
    case KeyboardEvent.KeyDown(Key.ARROW_RIGHT) => Outcome(model.move(Direction.Right))
    case _ => Outcome(model)
  }

  override def updateViewModel(context: SceneContext[Unit], model: GameState, viewModel: Unit): GlobalEvent => Outcome[Unit] =
    _ => Outcome(viewModel)

  override def present(context: SceneContext[Unit], model: GameState, viewModel: Unit): Outcome[SceneUpdateFragment] = {
    val playerBox = model.boxes(0)
    val parentBox = playerBox.parent
    parentBox match {
      case Some(pbi) =>
        val screenWidth = 1600
        val screenHeight = 900

        val containerWH = (screenHeight * 4) / 5
        val containerYOff = (screenHeight - containerWH) / 2
        val containerXOff = (screenWidth - containerWH) / 2

        val addBlanks = SceneUpdateFragment.empty.addCloneBlanks(cloneBlanks.flatMap {
          case (color, BoxCloneBlanks(wall, empty)) => List(wall, empty)
        }.toList*)

        val (biggestId, boundingBox) = findEnclosing(model.boxes, Rectangle(containerXOff, containerYOff, containerWH, containerWH), screenWidth, screenHeight, pbi)
        val renderBoxes = renderBox(model.boxes, boundingBox, biggestId)

        val cloneBatches = renderBoxes.map {
          case (cid, cbd) => CloneBatch(cid, cbd*)
        }.toList

        Outcome(addBlanks |+| SceneUpdateFragment(cloneBatches*))
      case None => ???
    }
  }

  @tailrec def findEnclosing(boxes: Map[Int, BoxF[Int]], bounds: Rectangle, screenWidth: Int, screenHeight: Int, id: Int): (Int, Rectangle) = {
    if(bounds.width > screenWidth && bounds.height > screenHeight) {
      (id, bounds)
    } else {
      val box = boxes(id)
      box.parent match {
        case Some(parentId) =>
          val parentBox = boxes(parentId)
          val (hereX, hereY) = parentBox.positions(id)
          val boxHeight = parentBox.tiles.length
          val boxWidth = parentBox.tiles(hereY).length

          val newHeight = bounds.height * boxHeight
          val newWidth = bounds.width * boxWidth

          val xInset = bounds.width * hereX
          val yInset = bounds.width * hereY

          findEnclosing(boxes, Rectangle(bounds.x - xInset, bounds.y - yInset, newWidth, newHeight), screenWidth, screenHeight, parentId)
        case None =>
          (id, bounds)
      }
    }
  }

  def renderBox(boxes: Map[Int, BoxF[Int]], bounds: Rectangle, boxId: Int, acc: Map[CloneId, List[CloneBatchData]] = Map.empty): Map[CloneId, List[CloneBatchData]] = {
    val box = boxes(boxId)
    box.tiles.zipWithIndex.foldLeft(acc) { case (map, (row, y)) =>
      val pxy = bounds.y + ((y * bounds.height) / box.tiles.length)
      val npxy = bounds.y + (((y + 1) * bounds.height) / box.tiles.length)
      row.zipWithIndex.foldLeft(map) { case (map, (cell, x)) =>
        val pxx = bounds.x + ((x * bounds.width) / row.length)
        val npxx = bounds.x + (((x + 1) * bounds.width) / row.length);

        val bb = Rectangle(pxx, pxy, npxx - pxx, npxy - pxy)

        if(bb.width < 1 || bb.height < 1) {
          map
        } else {
          box.boxes.get((x, y)) match {
            case None =>
              val cbd = CloneBatchData(pxx, pxy, Radians.zero, npxx - pxx, npxy - pxy)
              val cid = cell match {
                case Tile.Wall => cloneBlanks(box.color).wallClone.id
                case _ => cloneBlanks(box.color).emptyClone.id
              }
              map.updated(cid, cbd :: map.getOrElse(cid, Nil))
            case Some(cb) =>
              renderBox(boxes, bb, cb, map)
          }
        }
      }
    }
  }

}
