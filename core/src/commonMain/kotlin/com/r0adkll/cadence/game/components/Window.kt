package com.r0adkll.cadence.game.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import com.r0adkll.cadence.game.ecs.Component

class Window : Component {
  var size by mutableStateOf(IntSize.Zero)
}
