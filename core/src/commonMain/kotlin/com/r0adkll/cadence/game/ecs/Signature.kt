package com.r0adkll.cadence.game.ecs

import kotlin.math.pow

typealias Signature = Int

/**
 * Modifies a signature by setting or unsetting a specific value signature.
 *
 * @param original The original 32-bit integer signature.
 * @param value The 32-bit integer representing the bit to flip (e.g., 1 shl 0 for the first bit, 1 shl 1 for the second, etc.).
 * @param enabled True to turn the bit on, false to turn it off.
 * @return The resulting signature with the specified bit modified.
 */
fun setSignature(original: Signature, value: Signature, enabled: Boolean = true): Signature {
  return if (enabled) {
    original or value
  } else {
    original and value.inv()
  }
}

fun Signature.applySignature(value: Signature, enabled: Boolean = true): Signature {
  return setSignature(this, value, enabled)
}

fun createSignature(vararg values: Signature): Signature {
  return values.fold(0) { acc, value ->
    acc.applySignature(value)
  }
}

/**
 * Factory used to generate an ordered list of bitwise int signatures that
 * can be signed in to a whole signature for use in our ECS
 */
class BitwiseSignatureFactory {
  private val available = ArrayDeque<Signature>(32)

  init {
    repeat(32) { bit ->
      available.add(2.0.pow(bit).toInt())
    }
  }

  fun next(): Signature = available.removeFirst()
}
