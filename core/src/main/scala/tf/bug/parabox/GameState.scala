package tf.bug.parabox

import scala.collection.immutable.ArraySeq
import tf.bug.parabox.Tile.{BoxGoal, Empty, PlayerGoal, Wall}

case class GameState(
  boxes: Map[Int, BoxF[Int]],
) {

  def move(direction: Direction): GameState = {
    val playerBox = boxes(0)
    playerBox.parent match {
      case Some(parentId) =>
        val parentBox = boxes(parentId)
        val (playerX, playerY) = parentBox.positions(0)
        force(parentId, None, 0, playerX, playerY, direction).getOrElse(this)
      case None => ???
    }
  }

  def force(context: Int, pusher: Option[Int], pushee: Int, pusheeX: Int, pusheeY: Int, direction: Direction): Option[GameState] = {
    val (nextX, nextY) = direction.next(pusheeX, pusheeY)
    val contextBox = boxes(context)
    if(nextY < 0 || nextY >= contextBox.tiles.length ||
      nextX < 0 || nextX >= contextBox.tiles(nextY).length) {
      ???
    } else {
      val pushDepedents = contextBox.tiles(nextY)(nextX) match {
        case Tile.Wall => None
        case _ =>
          contextBox.boxes.get((nextX, nextY)) match {
            case Some(inhabitantId) =>
              force(context, Some(pushee), inhabitantId, nextX, nextY, direction)
            case None =>
              Some(this)
          }
      }
      pushDepedents.map { newGameState =>
        val newContextBox = newGameState.boxes(context)
        GameState(
          boxes = newGameState.boxes.updated(
            context,
            newContextBox.copy(
              boxes = newContextBox.boxes
                .removed((pusheeX, pusheeY))
                .updated((nextX, nextY), pushee)
            )
          )
        )
      }
    }
  }

}

object GameState {

  def default: GameState = {
    lazy val couldesac: BoxF[Int] = BoxF(
      parent = Some(1),
      color = BoxColor.Orange,
      tiles = Vector(
        Vector(Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Wall, Tile.Wall, Tile.Empty, Tile.Wall, Tile.Wall, Tile.Wall)
      ),
      boxes = Map(
        (3, 3) -> 2
      )
    )
    lazy val container: BoxF[Int] = BoxF(
      parent = None,
      color = BoxColor.Blue,
      tiles = Vector(
        Vector(Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Empty, Tile.Wall),
        Vector(Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall, Tile.Wall)
      ),
      boxes = Map(
        (3, 1) -> 0,
        (3, 3) -> 1,
        (4, 3) -> 2,
      )
    )
    lazy val player: BoxF[Int] = BoxF(
      parent = Some(1),
      color = BoxColor.Pink,
      tiles = Vector(Vector(Tile.Wall)),
      boxes = Map()
    )
    GameState(
      boxes = Map(
        0 -> player,
        1 -> container,
        2 -> couldesac,
      ),
    )
  }

}

enum Direction extends java.lang.Enum[Direction] {
  case Up
  case Down
  case Left
  case Right

  def next(x: Int, y: Int): (Int, Int) =
    this match {
      case Up => (x, y - 1)
      case Down => (x, y + 1)
      case Left => (x - 1, y)
      case Right => (x + 1, y)
    }
}

case class BoxF[A](
  parent: Option[A],
  color: BoxColor,
  tiles: Vector[Vector[Tile]],
  boxes: Map[(Int, Int), A],
) {

  val positions: Map[A, (Int, Int)] = boxes.map(_.swap)

}

sealed trait BoxColor {
  def wallRgba: (Double, Double, Double, Double)
  def emptyRgba: (Double, Double, Double, Double)
}
object BoxColor {
  case object Pink extends BoxColor {
    override def wallRgba: (Double, Double, Double, Double) = (1.0, 0.0, 1.0, 1.0)
    override def emptyRgba: (Double, Double, Double, Double) = (0.5, 0.0, 0.5, 1.0)
  }
  case object Blue extends BoxColor {
    override def wallRgba: (Double, Double, Double, Double) = (0.0, 0.0, 1.0, 1.0)
    override def emptyRgba: (Double, Double, Double, Double) = (0.0, 0.0, 0.5, 1.0)
  }
  case object Orange extends BoxColor {
    override def wallRgba: (Double, Double, Double, Double) = (1.0, 0.5, 0.0, 1.0)
    override def emptyRgba: (Double, Double, Double, Double) = (0.5, 0.25, 0.0, 1.0)
  }
}

enum Tile extends java.lang.Enum[Tile] {
  case Empty
  case BoxGoal
  case PlayerGoal
  case Wall
}
