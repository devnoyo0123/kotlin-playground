package com.example.bookorder.controller.order.dto.validator

import com.example.bookorder.controller.order.CreateOrder
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ItemsListValidator::class])
annotation class ValidItemsList(
    val message: String = "목록은 비어 있을 수 없습니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ItemsListValidator : ConstraintValidator<ValidItemsList, List<CreateOrder.CreateOrderItemRequest>> {
    override fun isValid(value: List<CreateOrder.CreateOrderItemRequest>?, context: ConstraintValidatorContext?): Boolean {
        val result =  !value.isNullOrEmpty()
        return result;
    }
}