package com.r0adkll.cadence.game.ecs

import com.r0adkll.cadence.utils.log

class World {
  val entityManager = EntityManager()
  val componentManager = ComponentManager()
  val systemManager = SystemManager()

  fun createEntity(block: EntityRegistrationScope.() -> Unit = {}): Entity {
    return entityManager.create().apply {
      EntityRegistrationScope(this, this@World).block()
    }
  }

  fun destroyEntity(entity: Entity) {
    entityManager.destroy(entity)
    componentManager.destroy(entity)
    systemManager.destroy(entity)
  }

  inline fun <reified C : Component> register() {
    componentManager.register<C>()
  }

  inline fun <reified C : Component> addComponent(entity: Entity, component: C) {
    componentManager.add(entity, component)

    val entitySignature = entityManager.getSignature(entity) ?: 0
    val componentSignature = componentManager.getSignature<C>()
    val newSignature = setSignature(entitySignature, componentSignature, true)
    log { "Adding Component[${C::class.simpleName}, sig: $componentSignature] to Entity[$entity, sig: $entitySignature]" }
    entityManager.setSignature(entity, newSignature)

    systemManager.notifyEntityChange(entity, newSignature)
  }

  inline fun <reified C : Component> removeComponent(entity: Entity) {
    componentManager.remove<C>(entity)

    val entitySignature = entityManager.getSignature(entity)!!
    val newSignature = setSignature(entitySignature, componentManager.getSignature<C>(), false)
    entityManager.setSignature(entity, newSignature)

    systemManager.notifyEntityChange(entity, newSignature)
  }

  inline fun <reified S : System> registerSystem(
    system: S,
    block: SystemRegistrationScope.() -> Unit = {},
  ): S{
    system.world = this
    systemManager.register(system)
    setSystemSignature<S>(
      SystemRegistrationScope(this)
        .apply(block)
        .signature
    )
    return system
  }

  inline fun <reified S : System> setSystemSignature(signature: Signature) {
    log { "System[${S::class.simpleName}] Signature[$signature]" }
    systemManager.setSignature<S>(signature)
  }

  inline fun <reified C : Component> getComponent(entity: Entity): C? {
    return componentManager.get(entity)
  }
}

class EntityRegistrationScope(
  val entity: Entity,
  val world: World,
) {

  inline fun <reified C: Component> addComponent(component: C) {
    world.addComponent(entity, component)
  }
}

class SystemRegistrationScope(
  val world: World,
) {
  var signature = 0

  inline fun <reified C : Component> require() {
    signature = setSignature(signature, world.componentManager.getSignature<C>(), true)
  }
}
