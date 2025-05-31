package com.r0adkll.cadence.app.system

import androidx.compose.ui.unit.IntSize
import com.r0adkll.cadence.app.components.WindowSize
import com.r0adkll.cadence.game.components.Transform
import com.r0adkll.cadence.game.ecs.Entity
import com.r0adkll.cadence.game.ecs.System

class GraveyardSystem(
  val worldEntity: Entity,
) : System() {

  var deadEntities = mutableListOf<Entity>()

  fun update(delta: Double) {
    val windowSize = world.getComponent<WindowSize>(worldEntity)!!
    if (windowSize.size == IntSize.Zero) return

    entities.forEach { entity ->
      val transform = world.getComponent<Transform>(entity)!!

      if (transform.position.y > windowSize.size.height) {
        //world.destroyEntity(entity)
        deadEntities += entity
      }
    }

    if (deadEntities.isNotEmpty()) {
      deadEntities.forEach {
        world.destroyEntity(it)
      }
      deadEntities.clear()
    }
  }
}
