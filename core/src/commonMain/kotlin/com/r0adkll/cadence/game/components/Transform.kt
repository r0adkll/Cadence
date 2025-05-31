package com.r0adkll.cadence.game.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import com.r0adkll.cadence.game.ecs.Component

class Transform(
  initialPosition: Offset = Offset.Zero,
  initialRotation: Float = 0f,
  initialScale: Offset = Offset(1f, 1f),
) : Component {
  var position by mutableStateOf(initialPosition)
  var rotation by mutableFloatStateOf(initialRotation)
  var scale by mutableStateOf(initialScale)
}
