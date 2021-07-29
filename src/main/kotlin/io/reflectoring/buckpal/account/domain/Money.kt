package io.reflectoring.buckpal.account.domain

import java.math.BigInteger

data class Money(val amount: BigInteger) {
    val isZero: Boolean = amount == BigInteger.ZERO

    val isPositiveOrZero: Boolean
        get() = amount.compareTo(BigInteger.ZERO) >= 0
    val isNegative: Boolean
        get() = amount.compareTo(BigInteger.ZERO) < 0
    val isPositive: Boolean
        get() = amount.compareTo(BigInteger.ZERO) > 0

    val isNotPositive: Boolean = isZero || isNegative

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

    companion object {
        val ZERO = of(0L)

		fun of(value: Long): Money {
            return Money(BigInteger.valueOf(value))
        }

        fun add(a: Money, b: Money?): Money {
            return Money(a.amount.add(b!!.amount))
        }

		fun subtract(a: Money, b: Money): Money {
            return Money(a.amount.subtract(b.amount))
        }
    }
}