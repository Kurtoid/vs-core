package org.valkyrienskies.core.impl.api

import org.valkyrienskies.core.api.ships.LoadedServerShip
import kotlin.reflect.KProperty

// These will throw an error if ship is null
open class ShipValueDelegate<T>(private val clazz: Class<T>, private val persistent: Boolean) {
    open operator fun getValue(thisRef: ServerShipProvider, property: KProperty<*>): T? =
        thisRef.ship?.getAttachment(clazz)

    open operator fun setValue(thisRef: ServerShipProvider, property: KProperty<*>, value: T?) =
        if (persistent) thisRef.ship!!.saveAttachment(clazz, value) else {
            assert(thisRef.ship is LoadedServerShip) // Is not allowed to try to store non-persistent on unloaded ship
            (thisRef.ship!! as LoadedServerShip).setAttachment(clazz, value)
        }
}

class DefaultedShipValueDelegate<T>(clazz: Class<T>, persistent: Boolean, private val default: T) :
    ShipValueDelegate<T>(clazz, persistent) {

    override operator fun getValue(thisRef: ServerShipProvider, property: KProperty<*>): T =
        super.getValue(thisRef, property) ?: default
}

@Deprecated("sus")
inline fun <reified T> shipValue(default: T) =
    org.valkyrienskies.core.impl.api.DefaultedShipValueDelegate(T::class.java, false, default)

@Deprecated("sus")
inline fun <reified T> shipValue() =
    org.valkyrienskies.core.impl.api.ShipValueDelegate(T::class.java, false)

@Deprecated("sus")
inline fun <reified T> persistentShipValue(default: T) =
    org.valkyrienskies.core.impl.api.DefaultedShipValueDelegate(T::class.java, true, default)

@Deprecated("sus")
inline fun <reified T> persistentShipValue() =
    org.valkyrienskies.core.impl.api.ShipValueDelegate(T::class.java, true)
