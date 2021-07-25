package io.reflectoring.buckpal.common

import javax.validation.*

abstract class SelfValidating<T> {
    private val validator: Validator

    /**
     * Evaluates all Bean Validations on the attributes of this
     * instance.
     */
    @Suppress("UNCHECKED_CAST")
    protected fun validateSelf() {
        val violations = validator.validate(this as T)
        if (!violations.isEmpty()) {
            throw ConstraintViolationException(violations)
        }
    }

    init {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }
}