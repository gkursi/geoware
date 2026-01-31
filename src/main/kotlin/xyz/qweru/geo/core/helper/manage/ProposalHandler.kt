package xyz.qweru.geo.core.helper.manage

import xyz.qweru.geo.extend.kotlin.math.not

abstract class ProposalHandler<T : Proposal> {

    protected var current: T? = null
    protected var currentPriority = Int.MIN_VALUE

    open fun propose(proposal: T, priority: Int): Boolean {
        if (priority <= currentPriority) return false
        current = proposal
        return true
    }

    protected open fun resetProposal() {
        if (current == null || !current?.isComplete()) {
            return
        }

        currentPriority = Int.MIN_VALUE
        current = null
    }

}