// Copyright (C) 2025 r0adkll
// SPDX-License-Identifier: Apache-2.0
package com.r0adkll.cadence.game.ecs

import com.r0adkll.cadence.utils.log
import kotlin.jvm.JvmInline

@JvmInline
value class Entity(val id: Int)

/**
 * This class is responsible for managing our [Entity] ids
 */
class EntityManager(
  maxEntities: Int = DEFAULT_MAX,
) {
  private val available = ArrayDeque<Entity>()
  private val signatures = mutableMapOf<Entity, Signature>()

  private var living: Int = 0

  init {
    repeat(maxEntities) {
      available.add(Entity(it))
    }
  }

  fun create(): Entity {
    val id = available.removeFirst()
    ++living
    log { "Creating Entity[$id] Living[$living]" }
    return id
  }

  fun destroy(entity: Entity) {
    available.addLast(entity)
    --living
    log { "Destroying Entity[${entity.id}] Living[$living]" }
  }

  fun setSignature(entity: Entity, signature: Signature) {
    log { "Entity[${entity.id}] Signature Updated [$signature]]" }
    signatures[entity] = signature
  }

  fun getSignature(entity: Entity): Signature? = signatures[entity]

  companion object {
    const val DEFAULT_MAX = 5000
  }
}
