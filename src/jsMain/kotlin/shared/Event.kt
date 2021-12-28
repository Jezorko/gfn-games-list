package shared

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event

fun Event.targetValue() = when (val currentTarget = target) {
    null -> null
    is HTMLInputElement -> currentTarget.value
    is HTMLSelectElement -> currentTarget.value
    is HTMLTextAreaElement -> currentTarget.value
    else -> throw IllegalStateException("target ${currentTarget::class.simpleName} not supported")
}