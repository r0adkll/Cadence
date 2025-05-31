@file:OptIn(ExperimentalTime::class)

package com.r0adkll.cadence.utils

import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

inline fun profile(block: () -> Unit): Long = measureTime(block).inWholeNanoseconds

object Performance {
  private val updateProfiles = ArrayDeque<Long>(60)
  private val physicProfiles = ArrayDeque<Long>(60)

  fun profileUpdates(delta: Long) {
    updateProfiles.addFirst(delta)
    if (updateProfiles.size > 60) updateProfiles.removeLast()
  }

  fun profilePhysics(delta: Long) {
    physicProfiles.addFirst(delta)
    if (physicProfiles.size > 60) physicProfiles.removeLast()
  }

  val avgUpdateTime: Long
    get() = 1.seconds.inWholeNanoseconds / (updateProfiles.sum() / updateProfiles.size)

  val avgPhysicsTime: Long
    get() = 1.seconds.inWholeNanoseconds / (physicProfiles.sum() / physicProfiles.size)
}