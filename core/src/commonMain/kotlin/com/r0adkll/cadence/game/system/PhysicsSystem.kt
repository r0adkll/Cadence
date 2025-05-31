package com.r0adkll.cadence.game.system

import com.r0adkll.cadence.game.components.Gravity
import com.r0adkll.cadence.game.components.RigidBody
import com.r0adkll.cadence.game.components.Transform
import com.r0adkll.cadence.game.ecs.System

class PhysicsSystem : System() {

  override fun update(timeNanos: Long, deltaNs: Long, delta: Double) {
    entities.forEach { entity ->
      val rigidBody = world.getComponent<RigidBody>(entity)!!
      val transform = world.getComponent<Transform>(entity)!!
      val gravity = world.getComponent<Gravity>(entity)!!

      rigidBody.velocity += (rigidBody.acceleration * delta.toFloat())

      transform.position += (rigidBody.velocity * delta.toFloat())

      transform.rotation += (rigidBody.angularVelocity * delta.toFloat())

      rigidBody.velocity += (gravity.force * delta.toFloat())
    }
  }
}
