package com.r0adkll.cadence.game.components

import androidx.compose.ui.geometry.Offset
import com.r0adkll.cadence.game.ecs.Component

class Gravity(
  var force: Offset = Offset.Zero
) : Component
