package com.r0adkll.cadence.utils

object Debug {
  /**
   * Change this flag to enable debug mode
   */
  const val enabled: Boolean = false
}

fun log(msg: () -> String) {
  if (Debug.enabled) {
    println(msg())
  }
}
