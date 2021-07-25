package io.reflectoring.buckpal.account.domain

import java.math.BigInteger

class Money(val amount: BigInteger) {
    val isPositiveOrZero: Boolean
        get() = amount.compareTo(BigInteger.ZERO) >= 0
    val isNegative: Boolean
        get() = amount.compareTo(BigInteger.ZERO) < 0
    val isPositive: Boolean
        get() = amount.compareTo(BigInteger.ZERO) > 0

    fun isGreaterThanOrEqualTo(money: Money): Boolean {
        return amount.compareTo(money.amount) >= 0
    }

    fun isGreaterThan(money: Money): Boolean {
        return amount.compareTo(money.amount) >= 1
    }

    operator fun minus(money: Money): Money {
        return Money(amount.subtract(money.amount))
    }

    operator fun plus(money: Money): Money {
        return Money(amount.add(money.amount))
    }

    fun negate(): Money {
        return Money(amount.negate())
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is Money) return false
        val `this$amount`: Any = amount
        val `other$amount`: Any = o.amount
        return if (if (`this$amount` == null) `other$amount` != null else `this$amount` != `other$amount`) false else true
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$amount`: Any = amount
        result = result * PRIME + (`$amount`?.hashCode() ?: 43)
        return result
    }

    override fun toString(): String {
        return "Money(amount=" + amount + ")"
    }

    companion object {
        val ZERO = of(0L)
        @JvmStatic
		fun of(value: Long): Money {
            return Money(BigInteger.valueOf(value))
        }

        fun add(a: Money, b: Money?): Money {
            return Money(a.amount.add(b!!.amount))
        }

        @JvmStatic
		fun subtract(a: Money, b: Money): Money {
            return Money(a.amount.subtract(b.amount))
        }
    }
}