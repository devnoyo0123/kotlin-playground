package com.example.bookorder.distributed_lock

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.slf4j.LoggerFactory
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method

@Aspect
@Component
class DistributedLockAspect(
    private val distributedLockPort: DistributedLockPort,
    private val distributedLockTransaction: DistributedLockTransaction,
) {
    private val log = LoggerFactory.getLogger(DistributedLockAspect::class.java)

    private val parser: ExpressionParser = SpelExpressionParser()

    @Around("@annotation(com.example.bookorder.distributed_lock.DistributedLock)")
    @Throws(Throwable::class)
    fun distributedLock(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val distributedLock = method.getAnnotation(DistributedLock::class.java)

        val lockKeys = getLockKeys(distributedLock.keys, method, joinPoint.args)
        val waitTime = distributedLock.waitTime
        val leaseTime = distributedLock.leaseTime

        log.info("Attempting to acquire distributed lock for method: ${method.name}, keys: $lockKeys")

        var lock: RLock? = null
        try {
            lock = distributedLockPort.multiLock(lockKeys, waitTime, leaseTime, distributedLock.timeUnit)
            if (lock != null) {
                log.info("Successfully acquired distributed lock for method: ${method.name}, keys: $lockKeys")
                log.debug("Before method execution - Thread: ${Thread.currentThread().name}")
                val result = distributedLockTransaction.proceed(joinPoint)
                log.debug("After method execution - Thread: ${Thread.currentThread().name}")
                log.info("Method ${method.name} executed successfully under distributed lock")
                return result
            } else {
                log.error("Failed to acquire distributed lock for method: ${method.name}, keys: $lockKeys")
                throw IllegalStateException("Failed to acquire distributed lock for keys: $lockKeys")
            }
        } catch (e: Exception) {
            log.error("Error occurred while executing method ${method.name} under distributed lock", e)
            throw e
        } finally {
            lock?.let {
                distributedLockPort.multiUnlock(it)
                log.info("Released distributed lock for method: ${method.name}, keys: $lockKeys")
            }
        }
    }

    private fun getLockKeys(keys: Array<String>, method: Method, args: Array<Any>): List<String> {
        val evaluationContext = StandardEvaluationContext()
        val parameterNames = method.parameters.map { it.name }.toTypedArray()

        parameterNames.forEachIndexed { index, paramName ->
            evaluationContext.setVariable(paramName, args[index])
        }

        return keys.map { key ->
            // SpEL 표현식 평가
            val expression = parser.parseExpression(key)
            expression.getValue(evaluationContext, String::class.java) ?: ""
        }.also {
            log.debug("Generated lock keys: {} for method: {}", it, method.name)
        }
    }
}