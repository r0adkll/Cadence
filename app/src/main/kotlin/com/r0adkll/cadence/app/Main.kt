// Copyright (C) 2023 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.r0adkll.cadence.game.GameLoop

@Suppress("CAST_NEVER_SUCCEEDS", "UNCHECKED_CAST", "USELESS_CAST", "KotlinRedundantDiagnosticSuppress")
fun main() = application {
  val gameWorld = remember { ExampleGameWorld() }

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
    GameLoop(gameWorld)

    gameWorld.Content(
      modifier = Modifier.fillMaxSize()
    )
  }
}
