package io.reflectoring.buckpal

import io.reflectoring.buckpal.account.application.service.MoneyTransferProperties
import io.reflectoring.buckpal.account.domain.Money.Companion.of
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(BuckPalConfigurationProperties::class)
class BuckPalConfiguration {
    /**
     * Adds a use-case-specific [MoneyTransferProperties] object to the application context. The properties
     * are read from the Spring-Boot-specific [BuckPalConfigurationProperties] object.
     */
    @Bean
    fun moneyTransferProperties(buckPalConfigurationProperties: BuckPalConfigurationProperties): MoneyTransferProperties {
        return MoneyTransferProperties(of(buckPalConfigurationProperties.transferThreshold))
    }
}