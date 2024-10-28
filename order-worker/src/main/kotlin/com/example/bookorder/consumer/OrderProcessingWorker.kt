package com.example.bookorder.consumer

import com.example.bookorder.MySQLConnectorObjectMapper
import com.example.bookorder.payment.PaymentEvent
import com.example.bookorder.update.ProcessOrderUseCase
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderProcessingWorker(
    private val processOrderUseCase: ProcessOrderUseCase
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = MySQLConnectorObjectMapper.objectMapper

    @KafkaListener(
        topics = ["local.test.payment_event_tbl"],
        groupId = "order-worker-0",
        containerFactory = "kafkaListenerContainerFactory")

    fun consume(message: ConsumerRecord<String, String>) {
        val paymentEvent = objectMapper.readValue(message.value(), PaymentEvent::class.java)
        logger.info("Payment event consumed: $paymentEvent")

        processOrderUseCase.processOrder(paymentEvent)
    }
}
