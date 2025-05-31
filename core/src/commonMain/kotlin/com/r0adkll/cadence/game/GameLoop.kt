@file:OptIn(ExperimentalCoroutinesApi::class)

package com.r0adkll.cadence.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import com.r0adkll.cadence.utils.GameThread
import com.r0adkll.cadence.utils.Performance
import com.r0adkll.cadence.utils.PhysicsThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

interface Game {
  @GameThread
  fun update(timeNanos: Long, deltaNs: Long, delta: Double)

  @PhysicsThread
  fun updatePhysics(timeNanos: Long, deltaNs: Long, delta: Double)

  @Composable
  fun Content(modifier: Modifier)
}

@Composable
fun GameLoop(game: Game) {
  // Create dedicated threads for each loop
  val physicsThread = remember { Dispatchers.Default.limitedParallelism(1) }
  val gameThread = remember { Dispatchers.Default.limitedParallelism(1) }

  // Physics Thread
  LaunchedEffect(Unit) {
    withContext(physicsThread) {
      gameLoop { timeNanos, deltaNs, delta ->
        Performance.profilePhysics(deltaNs)
        game.updatePhysics(timeNanos, deltaNs, delta)
      }
    }
  }

  // Game Thread
  LaunchedEffect(Unit) {
    withContext(gameThread) {
      gameLoop { timeNanos, deltaNs, delta ->
        Performance.profileUpdates(deltaNs)
        game.update(timeNanos, deltaNs, delta)
      }
    }
  }
}

private suspend fun gameLoop(
  onFrame: (timeNanos: Long, deltaNs: Long, delta: Double) -> Unit,
) {
  var previousTime = -1L
  while (coroutineContext.isActive) {
    withFrameNanos { time ->
      if (previousTime == -1L) {
        previousTime = time
        return@withFrameNanos
      }

      val delta = time - previousTime
      val deltaDouble = (delta.toDouble() / 1E9)
      previousTime = time

      onFrame(time, delta, deltaDouble)
    }
  }
}
