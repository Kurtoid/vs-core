package org.valkyrienskies.core.util.assertions.stages.predicates

fun interface StagePredicate<S> {
    fun test(stage: S): Boolean

    companion object {
        fun <S> single(stage: S): StagePredicate<S> = SingleStage(stage)
        fun <S> oneOf(vararg stages: S): StagePredicate<S> = OneOfStages(*stages)
    }
}
