package com.r0adkll.cadence.game.ecs

import com.r0adkll.cadence.game.components.Window
import com.r0adkll.cadence.utils.log

/**
 * Coordinator class to manage the entire Entity Component System (ECS)
 */
class World {
  val entityManager = EntityManager()
  val componentManager = ComponentManager()
  val systemManager = SystemManager()

  /**
   * Create a root entity that represents the entire "world". This essentially is a global
   * entity that can be used to store components/information about the world (like window size) that
   * other entities/components can easily access
   */
  val self = createEntity {
    addComponent(Window())
  }

  /**
   * Get the global [Window] componet of this game. This [Component] is by default
   * attached to the global [self] entity for this world.
   */
  fun getWindow(): Window {
    return getComponent<Window>(self)!!
  }

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

  inline fun <reified C : Component> addComponent(entity: Entity, component: C) {
    // Make sure our components are registered with the system before being added
    // to an entity. The ensures that its array and signature are available for use.
    val componentSignature = componentManager.register<C>()

    // Register the provided component to the entity
    componentManager.add(entity, component)

    // Update the entities signature, and notify any systems
    val entitySignature = entityManager.getSignature(entity) ?: 0
    val newSignature = entitySignature.applySignature(componentSignature, true)
    log { "Adding Component[${C::class.simpleName}, sig: $componentSignature] to Entity[$entity, sig: $entitySignature]" }
    entityManager.setSignature(entity, newSignature)

    systemManager.notifyEntityChange(entity, newSignature)
  }

  inline fun <reified C : Component> removeComponent(entity: Entity) {
    componentManager.remove<C>(entity)

    val entitySignature = entityManager.getSignature(entity)!!
    val componentSignature = componentManager.getSignature<C>()
    val newSignature = entitySignature.applySignature(componentSignature, false)
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
    // Ensure that this component is actually registered and assigned a signatures
    val componentSignature = world.componentManager.register<C>()
    signature = signature.applySignature(componentSignature)
  }
}
