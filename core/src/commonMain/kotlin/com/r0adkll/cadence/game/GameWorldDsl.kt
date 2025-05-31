package com.r0adkll.cadence.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.r0adkll.cadence.game.ecs.World

@DslMarker
annotation class GameWorldDsl

typealias UpdateFunction = World.(timeNanos: Long, deltaNs: Long, delta: Double) -> Unit

@GameWorldDsl
class GameWorldBuilder(
  val world: World,
) {
  private var update: UpdateFunction = { _, _, _ -> }
  private var updatePhysics: UpdateFunction = { _, _, _ -> }

  @GameWorldDsl
  fun onUpdate(block: UpdateFunction) {
    update = block
  }

  @GameWorldDsl
  fun onUpdatePhysics(block: UpdateFunction) {
    updatePhysics = block
  }

  internal fun build(): GameWorld {
    return GameWorld(world, update, updatePhysics)
  }
}

@GameWorldDsl
fun GameWorld(block: GameWorldBuilder.() -> Unit): GameWorld {
  val world = World()
  val builder = GameWorldBuilder(world)
  builder.block()
  return builder.build()
}

@GameWorldDsl
@Composable
fun rememberGameWorld(block: GameWorldBuilder.() -> Unit = {}): GameWorld {
  return remember {
    GameWorld(block = block)
  }
}
