package com.r0adkll.cadence.game.ecs

import com.r0adkll.cadence.utils.log
import kotlin.reflect.KClass


/**
 * Base component type for better typing in this system
 */
interface Component

class ComponentArray<T : Component>(
  private val components: Array<T?>,
) {
  private val entityToIndex = mutableMapOf<Entity, Int>()
  private val indexToEntity = mutableMapOf<Int, Entity>()

  var size: Int = 0
    private set

  fun insert(entity: Entity, component: T) {
    val newIndex = size
    entityToIndex[entity] = newIndex
    indexToEntity[newIndex] = entity
    components[newIndex] = component
    ++size
  }

  fun remove(entity: Entity) {
    if (entityToIndex[entity] == null) return

    val indexOfRemovedEntity = entityToIndex[entity]!!
    val indexOfLastElement = size - 1
    components[indexOfRemovedEntity] = components[indexOfLastElement]

    val entityOfLastElement = indexToEntity[indexOfLastElement]!!
    entityToIndex[entityOfLastElement] = indexOfRemovedEntity
    indexToEntity[indexOfRemovedEntity] = entityOfLastElement

    entityToIndex.remove(entity)
    indexToEntity.remove(indexOfLastElement)

    --size
  }

  operator fun get(entity: Entity): T = components[entityToIndex[entity]!!]!!
}

class ComponentManager {
  val signatureFactory = BitwiseSignatureFactory()
  val componentSignatures = mutableMapOf<KClass<*>, Signature>()
  val componentArrays = mutableMapOf<KClass<*>, ComponentArray<*>>()

  inline fun <reified T: Component> register() {
    val newArray = ComponentArray<T>(arrayOfNulls(EntityManager.DEFAULT_MAX))
    componentArrays[T::class] = newArray
    componentSignatures[T::class] = signatureFactory.next()
    log { "Component[${T::class.simpleName}] Signature[${componentSignatures[T::class]}]" }
  }

  @Suppress("UNCHECKED_CAST")
  inline fun <reified T : Component> add(entity: Entity, component: T) {
    val componentArray = componentArrays[T::class] as ComponentArray<T>
    componentArray.insert(entity, component)
  }

  inline fun <reified T : Component> remove(entity: Entity) {
    val componentArray = componentArrays[T::class]
    componentArray?.remove(entity)
  }

  inline fun <reified T : Component> get(entity: Entity): T? {
    val componentArray = componentArrays[T::class]
    return componentArray?.get(entity) as T
  }

  inline fun <reified T : Component> getSignature(): Signature {
    return componentSignatures[T::class]!!
  }

  /**
   * Remove ALL registered components for a given entity
   */
  fun destroy(entity: Entity) {
    componentArrays.values.forEach {
      it.remove(entity)
    }
  }

  @Suppress("UNCHECKED_CAST")
  inline fun <reified T : Component> getOrCreateComponentArray(): ComponentArray<T> {
    val componentArray = componentArrays[T::class]
    if (componentArray == null) {
      val newArray = ComponentArray<T>(arrayOfNulls(EntityManager.DEFAULT_MAX))
      componentArrays[T::class] = newArray
      componentSignatures[T::class] = signatureFactory.next()
      return newArray
    } else {
      return componentArray as ComponentArray<T>
    }
  }
}
