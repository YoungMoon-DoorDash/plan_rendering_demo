package com.doordash.plan_rendering_demo.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.sql.Timestamp

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Timestamp::class)
object TimestampSerializer : KSerializer<Timestamp> {
    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Timestamp {
        return Timestamp.valueOf(decoder.decodeString())
    }
}
