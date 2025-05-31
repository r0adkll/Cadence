package com.r0adkll.cadence.game.components

import androidx.compose.runtime.Composable
import com.r0adkll.cadence.game.Component
import com.r0adkll.cadence.game.Entity
import com.r0adkll.cadence.game.World

interface Renderable : Component {

  @Composable
  fun Content(entity: Entity, world: World)
}
