package com.r0adkll.cadence.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import com.r0adkll.cadence.app.components.Cube
import com.r0adkll.cadence.app.components.WindowSize
import com.r0adkll.cadence.app.emitter.EntityEmitter
import com.r0adkll.cadence.app.system.GraveyardSystem
import com.r0adkll.cadence.game.GameWorld
import com.r0adkll.cadence.game.components.Gravity
import com.r0adkll.cadence.game.components.Renderable
import com.r0adkll.cadence.game.components.RigidBody
import com.r0adkll.cadence.game.components.Transform
import com.r0adkll.cadence.game.ecs.Entity
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

class ExampleGameWorld : GameWorld() {

  private val windowSize = WindowSize()
  private val graveyardSystem: GraveyardSystem

  private val cubeEmitter = EntityEmitter(
    emit = {
      val position = Offset(
        x = Random.nextFloat() * windowSize.size.width,
        y = -100f,
      )
      println("==> Emitting @ $position")
      createCubeEntity(
        initialPosition = position,
        initialVelocity = Offset(0f, 50f),
      )
    },
    intervalNanos = 50.milliseconds.inWholeNanoseconds,
//    limit = 10
  )

  init {
    world.register<WindowSize>()

    val worldEntity = world.createEntity {
      addComponent(windowSize)
    }

    graveyardSystem = world.registerSystem(GraveyardSystem(worldEntity)) {
      require<Transform>()
    }
  }

  private fun createCubeEntity(
    initialPosition: Offset,
    initialVelocity: Offset = Offset.Zero,
  ): Entity {
    return world.createEntity {
      addComponent(Transform(initialPosition = initialPosition))
      addComponent(RigidBody(velocity = initialVelocity))
      addComponent(Gravity(force = Offset(0f, 30f)))
      addComponent(Cube(AvailableColors.random()) as Renderable)
    }
  }

  override fun update(timeNanos: Long, deltaNs: Long, delta: Double) {
    cubeEmitter.update(deltaNanos = deltaNs)
  }

  override fun updatePhysics(timeNanos: Long, deltaNs: Long, delta: Double) {
    super.updatePhysics(timeNanos, deltaNs, delta)
    graveyardSystem.update(delta)
  }

  @Composable
  override fun Content(modifier: Modifier) {
    super.Content(
      modifier.onSizeChanged { newSize ->
        windowSize.size = newSize
      }
    )
  }
}

private val AvailableColors = listOf(
  Color.Red,
  Color.Yellow,
  Color.Blue,
  Color.Cyan,
  Color.Gray,
  Color.Black,
  Color.Magenta,
)
