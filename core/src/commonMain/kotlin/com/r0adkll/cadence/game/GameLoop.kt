@file:OptIn(ExperimentalCoroutinesApi::class)

package com.r0adkll.cadence.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import com.r0adkll.cadence.utils.GameThread
import com.r0adkll.cadence.utils.Performance
import com.r0adkll.cadence.utils.PhysicsThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

interface GameLoop {

  @GameThread
  fun update(timeNanos: Long, deltaNs: Long, delta: Double)

  @PhysicsThread
  fun updatePhysics(timeNanos: Long, deltaNs: Long, delta: Double)
}

@Composable
fun GameLoop(loop: GameLoop) {
  // Create dedicated threads for each loop
  val physicsThread = remember { Dispatchers.Default.limitedParallelism(1) }
  val gameThread = remember { Dispatchers.Default.limitedParallelism(1) }

  // Physics Thread
  LaunchedEffect(Unit) {
    withContext(physicsThread) {
      gameLoop { timeNanos, deltaNs, delta ->
        Performance.profilePhysics(deltaNs)
        loop.updatePhysics(timeNanos, deltaNs, delta)
      }
    }
  }

  // Game Thread
  LaunchedEffect(Unit) {
    withContext(gameThread) {
      gameLoop { timeNanos, deltaNs, delta ->
        Performance.profileUpdates(deltaNs)
        loop.update(timeNanos, deltaNs, delta)
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
