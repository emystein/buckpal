package io.reflectoring.buckpal.common

import org.springframework.stereotype.Component
import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.validation.ConstraintViolation
import javax.validation.ValidatorFactory

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@kotlin.annotation.MustBeDocumented
@Component
annotation class PersistenceAdapter(
    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     * @return the suggested component name, if any (or empty String otherwise)
     */
    val value: String = ""
)