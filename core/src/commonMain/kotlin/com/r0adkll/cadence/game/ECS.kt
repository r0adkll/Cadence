package com.r0adkll.cadence.game

import androidx.compose.runtime.mutableStateSetOf
import com.r0adkll.cadence.utils.Debug
import kotlin.Array
import kotlin.jvm.JvmInline
import kotlin.math.pow
import kotlin.reflect.KClass

fun log(msg: () -> String) {
  if (Debug.enabled) {
    println(msg())
  }
}

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

abstract class System {
  lateinit var world: World

  val entities = mutableStateSetOf<Entity>()
}

class SystemManager {
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
}

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
