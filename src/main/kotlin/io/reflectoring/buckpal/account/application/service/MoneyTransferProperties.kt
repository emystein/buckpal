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

    constructor() {}

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is MoneyTransferProperties) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        val `this$maximumTransferThreshold`: Any = maximumTransferThreshold
        val `other$maximumTransferThreshold`: Any = other.maximumTransferThreshold
        return if (if (`this$maximumTransferThreshold` == null) `other$maximumTransferThreshold` != null else `this$maximumTransferThreshold` != `other$maximumTransferThreshold`) false else true
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is MoneyTransferProperties
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$maximumTransferThreshold`: Any = maximumTransferThreshold
        result = result * PRIME + (`$maximumTransferThreshold`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "MoneyTransferProperties(maximumTransferThreshold=" + maximumTransferThreshold + ")"
    }
}