package com.r0adkll.cadence.game.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.r0adkll.cadence.game.System
import com.r0adkll.cadence.game.components.Renderable

class ComposableRenderSystem : System() {

  @Composable
  fun Content() {
    entities.forEach { entity ->
      val renderable = world.getComponent<Renderable>(entity)!!

      key(entity) {
        renderable.Content(entity, world)
      }
    }
  }
}
