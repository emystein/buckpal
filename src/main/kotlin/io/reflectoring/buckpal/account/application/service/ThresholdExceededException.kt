package io.reflectoring.buckpal.account.application.service

import io.reflectoring.buckpal.account.domain.Money

class ThresholdExceededException(threshold: Money?, actual: Money?) : RuntimeException(
    String.format(
        "Maximum threshold for transferring money exceeded: tried to transfer %s but threshold is %s!",
        actual,
        threshold
    )
)