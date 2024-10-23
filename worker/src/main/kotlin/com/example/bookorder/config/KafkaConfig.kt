package com.example.bookorder.config

import ExponentialJitterBackOff
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.*
import org.springframework.util.backoff.BackOff
import java.util.concurrent.atomic.AtomicReference


@Configuration
@EnableKafka
class KafkaConfig(
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, String> {
        val props: Map<String, Any> = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false, // 수동 커밋
        )
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(kafkaTemplate: KafkaTemplate<String, String>,    errorHandler: CommonErrorHandler
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = consumerFactory()
            setConcurrency(1) // 단, 이는 토픽의 파티션 수에 따라 제한됩니다. 파티션 수보다 많은 concurrency는 의미가 없습니다.
            containerProperties.pollTimeout = 3000
            setCommonErrorHandler(errorHandler) // 등록된 errorHandler 빈을 사용

        }
    }

    private fun generateBackOff(): BackOff {
        return ExponentialJitterBackOff(
            initialInterval = 200,
            maxInterval = 10000,
            multiplier = 1.5,
            jitterFactor = 0.3,
            maxWaitTime = 60000 // 60초
        )
    }

    @Bean
    @Primary
    fun errorHandler(kafkaTemplate: KafkaTemplate<String, String>): CommonErrorHandler {
        val cseh = CommonContainerStoppingErrorHandler()
        val consumer2 = AtomicReference<Consumer<*, *>>()
        val container2 = AtomicReference<MessageListenerContainer>()

        // DeadLetterPublishingRecoverer 생성
        val deadLetterRecoverer = DeadLetterPublishingRecoverer(kafkaTemplate)

        val errorHandler: DefaultErrorHandler = object : DefaultErrorHandler(
            ConsumerRecordRecoverer { rec: ConsumerRecord<*, *>, ex: Exception ->
                // DeadLetterPublishingRecoverer로 실패한 메시지를 Dead Letter Topic에 전송
                deadLetterRecoverer.accept(rec, ex)

                // 추가적으로 CommonContainerStoppingErrorHandler를 호출하도록 유지
                cseh.handleRemaining(ex, listOf(rec), consumer2.get(), container2.get())
            }, generateBackOff()
        ) {
            override fun handleRemaining(
                thrownException: java.lang.Exception,
                records: MutableList<ConsumerRecord<*, *>>,
                consumer: Consumer<*, *>,
                container: MessageListenerContainer
            ) {
                consumer2.set(consumer)
                container2.set(container)
                super.handleRemaining(thrownException, records, consumer, container)
            }
        }

        // 비재시도 예외 추가
        errorHandler.addNotRetryableExceptions(
            JsonProcessingException::class.java,
            MismatchedInputException::class.java
        )
        return errorHandler
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val configProps: Map<String, Any> = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.ACKS_CONFIG to "-1",
            ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to "true",
            ProducerConfig.TRANSACTIONAL_ID_CONFIG to "tx-",  // 트랜잭션 ID 접두사 추가
        )
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}

