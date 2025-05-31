package com.r0adkll.cadence.game.ecs

import androidx.compose.runtime.mutableStateSetOf
import com.r0adkll.cadence.game.GameLoop
import com.r0adkll.cadence.utils.log
import kotlin.reflect.KClass

abstract class System : GameLoop {
  lateinit var world: World

  val entities = mutableStateSetOf<Entity>()

  override fun update(timeNanos: Long, deltaNs: Long, delta: Double) {
    // Do nothing by default
  }

  override fun updatePhysics(timeNanos: Long, deltaNs: Long, delta: Double) {
    // Do nothing by default
  }
}

class SystemManager : GameLoop {
  val systems = mutableMapOf<KClass<*>, System>()
  val signatures = mutableMapOf<KClass<*>, Signature>()

  inline fun <reified S : System> register(system: S): S {
    systems[S::class] = system
    return system
  }

  inline fun <reified S : System> setSignature(signature: Signature) {
    signatures[S::class] = signature
  }

  fun destroy(entity: Entity) {
    systems.values.forEach { system ->
      system.entities.remove(entity)
    }
  }

  fun notifyEntityChange(entity: Entity, signature: Signature) {
    systems.forEach { (type, system) ->
      val systemSignature = signatures[type]
        ?: throw IllegalStateException("The system for $type doesn't have a signature set")

      if ((signature and systemSignature) == systemSignature) {
        log { "Entity[$entity] added to System[$system]" }
        system.entities.add(entity)
      } else {
        log { "Entity[$entity] removed from System[$system]" }
        system.entities.remove(entity)
      }
    }
  }

  override fun update(timeNanos: Long, deltaNs: Long, delta: Double) {
    systems.values.forEach { it.update(timeNanos, deltaNs, delta) }
  }

  override fun updatePhysics(timeNanos: Long, deltaNs: Long, delta: Double) {
    systems.values.forEach { it.updatePhysics(timeNanos, deltaNs, delta) }
  }
}
