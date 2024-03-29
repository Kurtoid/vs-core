package org.valkyrienskies.core.impl.util.assertions.stages.predicates

fun interface StagePredicate<S> {
    fun test(stage: S): Boolean

    companion object {
        fun <S> single(stage: S): StagePredicate<S> = SingleStage(stage)
        fun <S> anyOf(vararg stages: S): StagePredicate<S> = AnyOfStages(*stages)
    }
}
