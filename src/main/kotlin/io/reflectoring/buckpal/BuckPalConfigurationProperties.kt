package io.reflectoring.buckpal

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "buckpal")
class BuckPalConfigurationProperties {
    var transferThreshold = Long.MAX_VALUE
    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is BuckPalConfigurationProperties) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        return if (transferThreshold != other.transferThreshold) false else true
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