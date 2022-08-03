package org.valkyrienskies.core.util.events

import org.valkyrienskies.core.networking.RegisteredHandler
import java.util.function.Consumer
import java.util.function.Predicate

interface EventEmitter<T> {

    fun on(cb: EventConsumer<T>): RegisteredHandler

    fun on(cb: Consumer<T>): RegisteredHandler {
        return on { value, _ -> cb.accept(value) }
    }

    fun once(predicate: Predicate<T>, cb: Consumer<T>): RegisteredHandler {
        return on { value, handler ->
            if (predicate.test(value)) {
                cb.accept(value)
                handler.unregister()
            }
        }
    }
}
