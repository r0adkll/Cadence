// Copyright (C) 2025 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.app.emitter

import kotlin.time.Duration

class EntityEmitter(
  val emit: () -> Unit,
  interval: Duration,
  val limit: Long? = null,
) {
  private val intervalNanos = interval.inWholeNanoseconds

  private var emitCount = 0L
  private var cumulativeTimeNanos = 0L

  fun update(deltaNs: Long) {
    if (limit != null && emitCount >= limit) return

    cumulativeTimeNanos += deltaNs

    if (cumulativeTimeNanos > intervalNanos) {
      emit()
      emitCount++
      cumulativeTimeNanos = 0L
    }
  }
}
