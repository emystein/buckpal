package io.reflectoring.buckpal.account.application.port.`in`

import io.reflectoring.buckpal.account.domain.Money

class NotPositiveAmountException(val amount: Money) : RuntimeException(amount.toString())