package org.valkyrienskies.core.util.assertions.stages.constraints

internal class RequireNoDuplicates<S>(vararg stages: S) :
    StageConstraint<S> {
    private val stages: List<S> = stages.asList()

    override fun check(stagesSinceReset: List<S>, isReset: Boolean): String? {
        if (stages.isEmpty() && setOf(stagesSinceReset).size != stagesSinceReset.size) {
            return "Expected no duplicates"
        }

        if (stages.any { stage -> stagesSinceReset.count { it == stage } > 1 }) {
            return "Expected no duplicates of the following stages: $stages"
        }

        return null
    }
}
