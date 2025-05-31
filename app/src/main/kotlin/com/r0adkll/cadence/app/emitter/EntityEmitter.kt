package com.r0adkll.cadence.app.emitter

import com.r0adkll.cadence.game.ecs.Entity


class EntityEmitter(
  val emit: () -> Entity,
  val intervalNanos: Long,
  val limit: Int = Int.MAX_VALUE
) {

  private var emitCount = 0
  private var cumulativeTimeNanos = 0L

  fun update(deltaNanos: Long) {
    cumulativeTimeNanos += deltaNanos
    if (cumulativeTimeNanos > intervalNanos && emitCount < limit) {
      emit()
      emitCount++
      cumulativeTimeNanos = 0L
    }
  }
}
