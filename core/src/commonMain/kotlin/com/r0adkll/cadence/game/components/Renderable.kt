// Copyright (C) 2025 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.game.components

import androidx.compose.runtime.Composable
import com.r0adkll.cadence.game.ecs.Component
import com.r0adkll.cadence.game.ecs.Entity
import com.r0adkll.cadence.game.ecs.World

interface Renderable : Component {

  @Composable
  fun Content(entity: Entity, world: World)
}
