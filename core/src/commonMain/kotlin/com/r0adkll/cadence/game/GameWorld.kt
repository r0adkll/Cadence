// Copyright (C) 2025 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.game

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import com.r0adkll.cadence.game.components.Gravity
import com.r0adkll.cadence.game.components.Renderable
import com.r0adkll.cadence.game.components.RigidBody
import com.r0adkll.cadence.game.components.Transform
import com.r0adkll.cadence.game.components.Window
import com.r0adkll.cadence.game.ecs.World
import com.r0adkll.cadence.game.system.ComposableRenderSystem
import com.r0adkll.cadence.game.system.PhysicsSystem

class GameWorld internal constructor(
  private val world: World,
  private val additionalUpdate: UpdateFunction,
  private val additionalPhysicsUpdate: UpdateFunction,
) : Game {

  // Register and sign the render system
  private val renderSystem: ComposableRenderSystem = world.registerSystem(ComposableRenderSystem()) {
    require<Renderable>()
  }

  init {
    // Register and sign the physics system
    world.registerSystem(PhysicsSystem()) {
      require<Transform>()
      require<RigidBody>()
      require<Gravity>()
    }
  }

  override fun update(timeNanos: Long, deltaNs: Long, delta: Double) {
    world.systemManager.update(timeNanos, deltaNs, delta)
    additionalUpdate(world, timeNanos, deltaNs, delta)
  }

  override fun updatePhysics(timeNanos: Long, deltaNs: Long, delta: Double) {
    world.systemManager.updatePhysics(timeNanos, deltaNs, delta)
    additionalPhysicsUpdate(world, timeNanos, deltaNs, delta)
  }

  @Composable
  override fun Content(modifier: Modifier) {
    GameLoop(this)

    Box(
      modifier = modifier
        .onSizeChanged { newSize ->
          // Update our world with the current window size
          world.getComponent<Window>(world.self)?.size = newSize
        },
    ) {
      renderSystem.Content()
    }
  }
}
