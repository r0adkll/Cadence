/*
 * Copyright (c) 2018, Edwin Jakobs, RNDR
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package com.r0adkll.cadence.math

import androidx.compose.ui.geometry.Offset
import kotlin.math.*

interface LinearType<T : LinearType<T>> {
    operator fun plus(right: T): T
    operator fun minus(right: T): T
    operator fun times(scale: Double): T
    operator fun div(scale: Double): T
}

/** Double-precision 2D vector. */
data class Vector2(val x: Double, val y: Double) : LinearType<Vector2> {

    constructor(x: Double) : this(x, x)

    /** The squared Euclidean length of the vector. */
    val squaredLength: Double
        get() = x * x + y * y

    /**
     * Calculates a cross product between this [Vector2] and [right].
     *
     * Technically you cannot find the
     * [cross product of two 2D vectors](https://stackoverflow.com/a/243984)
     * but it is still possible with clever use of mathematics.
     */
    infix fun cross(right: Vector2) = x * right.y - y * right.x

    /** Calculates a dot product between this [Vector2] and [right]. */
    infix fun dot(right: Vector2): Double = x * right.x + y * right.y

    /**
     * Creates a new [Vector2] with the given rotation and origin.
     *
     * @param degrees The rotation in degrees.
     * @param origin The point around which the vector is rotated, default is [Vector2.ZERO].
     */
    fun rotate(degrees: Double, origin: Vector2 = ZERO): Vector2 {
        val p = this - origin
        val a = degrees.asRadians

        val w = Vector2(
                p.x * cos(a) - p.y * sin(a),
                p.y * cos(a) + p.x * sin(a)
        )

        return w + origin
    }

    operator fun get(i: Int): Double {
        return when (i) {
            0 -> x
            1 -> y
            else -> throw RuntimeException("unsupported index")
        }
    }

    operator fun unaryMinus() = Vector2(-x, -y)

    override operator fun plus(right: Vector2) = Vector2(x + right.x, y + right.y)
    operator fun plus(d: Double) = Vector2(x + d, y + d)

    override operator fun minus(right: Vector2) = Vector2(x - right.x, y - right.y)
    operator fun minus(d: Double) = Vector2(x - d, y - d)

    override operator fun times(scale: Double) = Vector2(x * scale, y * scale)
    operator fun times(v: Vector2) = Vector2(x * v.x, y * v.y)

    override operator fun div(scale: Double) = Vector2(x / scale, y / scale)
    operator fun div(d: Vector2) = Vector2(x / d.x, y / d.y)

    /** Calculates the squared Euclidean distance to [other]. */
    fun squaredDistanceTo(other: Vector2): Double {
        val dx = other.x - x
        val dy = other.y - y
        return dx * dx + dy * dy
    }

    companion object {
        val ZERO = Vector2(0.0, 0.0)

        /** A [Vector2] representation for infinite values. */
        val INFINITY = Vector2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
    }

    /** Casts to [DoubleArray]. */
    fun toDoubleArray() = doubleArrayOf(x, y)
}

operator fun Double.times(v: Vector2) = v * this

fun Vector2.asOffset(): Offset = Offset(x.toFloat(), y.toFloat())