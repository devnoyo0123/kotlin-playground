package com.example.bookorder.create.exception

import com.example.bookorder.book.exception.InsufficientStockException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Component

@Aspect
@Component
class CreateOrderUseCaseExceptionWrapperAspect {

    @Around("@annotation(CreateOrderUseCaseExceptionWrapper)")
    fun wrapCreateOrderExceptions(joinPoint: ProceedingJoinPoint): Any? {

        return try {
            joinPoint.proceed()
        } catch (e: Exception) {
            when (e) {
                is ObjectOptimisticLockingFailureException -> throw OrderMaximumRetryException(OrderMaximumRetryException.MESSAGE ,e)
                is InsufficientStockException -> throw InsufficientStockException(InsufficientStockException.MESSAGE, e)
                is DataIntegrityViolationException -> throw DuplicateOrderException(DuplicateOrderException.MESSAGE, e)
                else -> throw e}
        }
    }
}