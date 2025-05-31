package com.r0adkll.cadence.app.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import com.r0adkll.cadence.game.ecs.World
import com.r0adkll.cadence.game.components.Renderable
import com.r0adkll.cadence.game.components.Transform
import com.r0adkll.cadence.game.ecs.Entity

class Cube(
  val color: Color,
) : Renderable {

  @Composable
  override fun Content(entity: Entity, world: World) {
    val transform = world.getComponent<Transform>(entity)!!

    Canvas(
      modifier = Modifier
        .size(24.dp)
        .rotate(transform.rotation)
        .offset {
          IntOffset(
            x = transform.position.x.fastRoundToInt(),
            y = transform.position.y.fastRoundToInt(),
          )
        }
    ) {
      drawRect(color)
    }
  }
}
