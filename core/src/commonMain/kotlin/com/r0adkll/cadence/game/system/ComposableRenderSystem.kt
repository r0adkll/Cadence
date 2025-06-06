// Copyright (C) 2025 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.game.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.r0adkll.cadence.game.components.Renderable
import com.r0adkll.cadence.game.ecs.System

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
