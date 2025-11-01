package xyz.qweru.geo.core.helper.manage

abstract class ProposalHandler<T : Proposal> {

    protected var current: T? = null
    protected var currentPriority = Int.MIN_VALUE

    open fun propose(proposal: T, priority: Int): Boolean {
        if (priority <= currentPriority) return false
        current = proposal
        return true
    }

    protected open fun handleProposal() {
        if (current?.isComplete() ?: false) {
            current = null
            currentPriority = Int.MIN_VALUE
        }
    }

}