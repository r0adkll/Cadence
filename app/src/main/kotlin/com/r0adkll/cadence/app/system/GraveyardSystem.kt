package com.r0adkll.cadence.app.system

import androidx.compose.ui.unit.IntSize
import com.r0adkll.cadence.game.components.Transform
import com.r0adkll.cadence.game.components.Window
import com.r0adkll.cadence.game.ecs.Entity
import com.r0adkll.cadence.game.ecs.System

class GraveyardSystem() : System() {

  private var deadEntities = mutableListOf<Entity>()

  override fun update(timeNanos: Long, deltaNs: Long, delta: Double) {
    val windowSize = world.getComponent<Window>(world.self)!!
    if (windowSize.size == IntSize.Zero) return

    entities.forEach { entity ->
      val transform = world.getComponent<Transform>(entity)!!

      if (transform.position.y > windowSize.size.height) {
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
