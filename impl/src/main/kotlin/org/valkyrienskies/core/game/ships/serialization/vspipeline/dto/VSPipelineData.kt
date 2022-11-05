package org.valkyrienskies.core.game.ships.serialization.vspipeline.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonSubTypes(
    JsonSubTypes.Type(VSPipelineDataV1::class),
    JsonSubTypes.Type(VSPipelineDataV2::class),
    JsonSubTypes.Type(VSPipelineDataV3::class)
)
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
internal sealed interface VSPipelineData