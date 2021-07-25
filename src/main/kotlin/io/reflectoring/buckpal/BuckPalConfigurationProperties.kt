package io.reflectoring.buckpal

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "buckpal")
class BuckPalConfigurationProperties {
    var transferThreshold = Long.MAX_VALUE

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is BuckPalConfigurationProperties) return false
        if (!other.canEqual(this as Any)) return false
        return transferThreshold == other.transferThreshold
    }

    protected fun canEqual(other: Any?): Boolean {
        return other is BuckPalConfigurationProperties
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$transferThreshold` = transferThreshold
        result = result * PRIME + (`$transferThreshold` ushr 32 xor `$transferThreshold`).toInt()
        return result
    }

    override fun toString(): String {
        return "BuckPalConfigurationProperties(transferThreshold=" + transferThreshold + ")"
    }
}