// Copyright (C) 2025 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.r0adkll.cadence.app.components.Cube
import com.r0adkll.cadence.app.emitter.EntityEmitter
import com.r0adkll.cadence.app.system.GraveyardSystem
import com.r0adkll.cadence.game.components.Gravity
import com.r0adkll.cadence.game.components.Renderable
import com.r0adkll.cadence.game.components.RigidBody
import com.r0adkll.cadence.game.components.Transform
import com.r0adkll.cadence.game.rememberGameWorld
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

fun main() = application {
  val gameWorld = rememberGameWorld {
    world.registerSystem(GraveyardSystem()) {
      require<Transform>()
    }

    val cubeEmitter = EntityEmitter(
      emit = {
        val position = Offset(
          x = Random.nextFloat() * world.getWindow().size.width,
          y = -100f,
        )
        world.createEntity {
          addComponent(Transform(initialPosition = position))
          addComponent(RigidBody(velocity = Offset(0f, 50f)))
          addComponent(Gravity(force = Offset(0f, 30f)))
          addComponent(Cube(AvailableColors.random()) as Renderable)
        }
      },
      interval = 50.milliseconds,
    )

    onUpdate { _, deltaNs, _ ->
      cubeEmitter.update(deltaNs)
    }
  }

  val windowState = rememberWindowState(
    width = 480.dp,
    height = 720.dp,
    position = WindowPosition.Aligned(Alignment.Center),
  )
  Window(
    title = "Example Game",
    onCloseRequest = ::exitApplication,
    state = windowState,
  ) {
    gameWorld.Content(
      modifier = Modifier.fillMaxSize(),
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
