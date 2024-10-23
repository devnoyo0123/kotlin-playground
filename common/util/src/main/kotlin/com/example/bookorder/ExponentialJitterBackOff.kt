import org.springframework.util.backoff.BackOff
import org.springframework.util.backoff.BackOffExecution
import kotlin.random.Random

class ExponentialJitterBackOff(
    private val initialInterval: Long = 100,
    private val maxInterval: Long = 30000,
    private val multiplier: Double = 2.0,
    private val maxElapsedTime: Long = Long.MAX_VALUE,
    private val jitterFactor: Double = 0.5,
    private val maxWaitTime: Long = 60000 // 최대 1분 대기
) : BackOff {
    init {
        require(initialInterval > 0) { "Initial interval must be positive" }
        require(maxInterval > 0) { "Max interval must be positive" }
        require(multiplier > 1.0) { "Multiplier must be greater than 1" }
        require(maxElapsedTime > 0) { "Max elapsed time must be positive" }
        require(jitterFactor in 0.0..1.0) { "Jitter factor must be between 0 and 1" }
        require(maxWaitTime >= initialInterval) { "Max wait time must be greater than or equal to initial interval" }
        require(maxWaitTime >= maxInterval) { "Max wait time must be greater than or equal to max interval" }
    }

    override fun start(): BackOffExecution {
        return ExponentialJitterBackOffExecution()
    }

    private inner class ExponentialJitterBackOffExecution : BackOffExecution {
        private var currentInterval = initialInterval
        private var elapsedTime: Long = 0
        private var totalWaitTime: Long = 0
        private val random = Random.Default

        override fun nextBackOff(): Long {
            // 최대 대기 시간 체크
            if (totalWaitTime >= maxWaitTime || elapsedTime >= maxElapsedTime) {
                return BackOffExecution.STOP
            }

            val delta = (currentInterval * jitterFactor).toLong()
            val jitteredInterval = if (delta > 0) {
                currentInterval - delta + random.nextLong(2 * delta + 1)
            } else {
                currentInterval
            }

            // maxInterval과 남은 대기 가능 시간 중 작은 값으로 제한
            val remainingWaitTime = maxWaitTime - totalWaitTime
            val result = jitteredInterval.coerceAtMost(maxInterval).coerceAtMost(remainingWaitTime)

            elapsedTime += result
            totalWaitTime += result

            // Calculate next interval without jitter for next iteration
            currentInterval = (currentInterval * multiplier).toLong().coerceAtMost(maxInterval)

            return result
        }
    }

    companion object {
        const val DEFAULT_INITIAL_INTERVAL = 100L
        const val DEFAULT_MAX_INTERVAL = 30000L // 30초
        const val DEFAULT_MULTIPLIER = 2.0
        const val DEFAULT_JITTER_FACTOR = 0.5
        const val DEFAULT_MAX_WAIT_TIME = 60000L // 1분
    }
}