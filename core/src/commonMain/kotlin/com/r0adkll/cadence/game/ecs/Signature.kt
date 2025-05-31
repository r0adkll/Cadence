// Copyright (C) 2025 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.game.ecs

import kotlin.math.pow

/**
 * Signatures are just bitwise encoded 32-bit integers.
 *
 * For example, if a [System] requires a Tranform, RigidBody, and Gravity component we want to encode those
 * component signatures into the system signature. For example, take
 *
 * ```
 * Transform = 0x00001 = 1
 * RigidBoxy = 0x00010 = 2
 * Gravity   = 0x00100 = 4
 * ----------|---------|---
 * System    | 0x00111 | 7
 * ```
 *
 * Then when you create an entity and add the Transform, RigidBody, and Gravity to it will also have a signature
 * of `0x00111` and we can cheaply determine if it needs to be added/removed from certain systems.
 *
 */
typealias Signature = Int

/**
 * Set the provided [Signature] [value] to this signature based on the provided [enabled]
 * flag.
 */
fun Signature.applySignature(value: Signature, enabled: Boolean = true): Signature {
  return setSignature(this, value, enabled)
}

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

/**
 * Factory used to generate an ordered list of bitwise int signatures that
 * can be signed in to a whole signature for use in our ECS.
 *
 * When encoding signatures into an integer we need to use base2 exponential numbers
 * so that each unique signature is defined by a unique bit in an integer.
 *
 * Take the example:
 *
 * ```
 * 0x00001 = 1
 * 0x00010 = 2
 * 0x00100 = 4
 * 0x01000 = 8
 * 0x10000 = 16
 * ```
 *
 * Here you can observe that each bit scales at a power of 2, so to generate a ready usable list of
 * signatures to give components we generate a queue of available signatures as such.
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
