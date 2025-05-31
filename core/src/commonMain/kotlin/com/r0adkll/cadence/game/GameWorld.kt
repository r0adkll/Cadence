package com.r0adkll.cadence.game

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.r0adkll.cadence.game.components.Gravity
import com.r0adkll.cadence.game.components.Renderable
import com.r0adkll.cadence.game.components.RigidBody
import com.r0adkll.cadence.game.components.Transform
import com.r0adkll.cadence.game.system.ComposableRenderSystem
import com.r0adkll.cadence.game.system.PhysicsSystem

abstract class GameWorld : Game {
  val world = World()

  private val physicsSystem: PhysicsSystem
  private val renderSystem: ComposableRenderSystem

  init {
    with (world) {
      register<Transform>()
      register<RigidBody>()
      register<Gravity>()
      register<Renderable>()
    }

    // Register and sign the physics system
    physicsSystem = world.registerSystem(PhysicsSystem()) {
      require<Transform>()
      require<RigidBody>()
      require<Gravity>()
    }

    // Register and sign the render system
    renderSystem = world.registerSystem(ComposableRenderSystem()) {
      require<Renderable>()
    }
  }

  override fun updatePhysics(timeNanos: Long, deltaNs: Long, delta: Double) {
    physicsSystem.update(delta)
  }

  @Composable
  override fun Content(modifier: Modifier) {
    Box(
      modifier = modifier
    ) {
      renderSystem.Content()
    }
  }
}
