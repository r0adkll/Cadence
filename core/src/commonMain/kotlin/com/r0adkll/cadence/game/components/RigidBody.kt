// Copyright (C) 2025 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.game.components

import androidx.compose.ui.geometry.Offset
import com.r0adkll.cadence.game.ecs.Component

class RigidBody(
  var velocity: Offset = Offset.Zero,
  var angularVelocity: Float = 0f,
  var acceleration: Offset = Offset.Zero,
) : Component
