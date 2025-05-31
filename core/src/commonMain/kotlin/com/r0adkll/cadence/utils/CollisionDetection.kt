// Copyright (C) 2025 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.utils

import androidx.compose.ui.geometry.Rect
import com.r0adkll.cadence.math.Vector2
import kotlin.math.abs

fun hasCollision(position: Vector2, radius: Double, bounds: Rect): Boolean {
  val halfWidth = bounds.width / 2f
  val halfHeight = bounds.height / 2f

  val circleDistX = abs(position.x - bounds.center.x)
  val circleDistY = abs(position.y - bounds.center.y)

  if (circleDistX > (halfWidth + radius)) return false
  if (circleDistY > (halfHeight + radius)) return false

  if (circleDistX <= halfWidth && circleDistY <= halfHeight) return true

  val dx = circleDistX - halfWidth
  val dy = circleDistY - halfHeight
  val cornerDistSq = dx * dx + dy * dy

  return cornerDistSq <= radius * radius
}
