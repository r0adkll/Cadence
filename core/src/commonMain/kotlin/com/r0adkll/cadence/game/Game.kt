package com.r0adkll.cadence.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Game : GameLoop {

  @Composable
  fun Content(modifier: Modifier)
}
