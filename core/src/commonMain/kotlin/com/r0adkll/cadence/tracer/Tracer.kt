package com.r0adkll.cadence.tracer

interface Tracer {
  fun beginSection(label: String)
  fun endSection()

  companion object NoOp : Tracer {
    override fun beginSection(label: String) = Unit
    override fun endSection() = Unit
  }
}

inline fun <R> Tracer.trace(label: String, block: () -> R): R {
  beginSection(label)
  return try {
    block()
  } finally {
    endSection()
  }
}
