package shared

import jezorko.github.gfngameslist.localization.Messages
import jezorko.github.gfngameslist.shared.ErrorResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val module = SerializersModule {
    polymorphic(Messages::class)
    polymorphic(ErrorResponse::class)
}

val json = Json {
    serializersModule = module
}