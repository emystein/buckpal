package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.domain.Money

/**
 * Configuration properties for money transfer use cases.
 */
class MoneyTransferProperties {
    var maximumTransferThreshold: Money = Money.of(1000000L)

    constructor(maximumTransferThreshold: Money) {
        this.maximumTransferThreshold = maximumTransferThreshold
    }

    constructor()

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is MoneyTransferProperties) return false
        if (!other.canEqual(this as Any)) return false
        return maximumTransferThreshold == other.maximumTransferThreshold
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is MoneyTransferProperties
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        result = result * PRIME + maximumTransferThreshold.hashCode()
        return result
    }

    override fun toString(): String {
        return "MoneyTransferProperties(maximumTransferThreshold=" + maximumTransferThreshold + ")"
    }
}