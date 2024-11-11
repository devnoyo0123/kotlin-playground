package com.example.bookorder.consumer

import com.example.bookorder.MySQLConnectorObjectMapper
import com.example.bookorder.ProcessPaymentUseCase
import com.example.bookorder.order.OrderEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class PaymentProcessingWorker(
    private val processPaymentUseCase: ProcessPaymentUseCase
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = MySQLConnectorObjectMapper.objectMapper

    @KafkaListener(
        topics = ["local.test.order_event_tbl"],
        groupId = "payment-worker-0",
        containerFactory = "kafkaListenerContainerFactory")

        fun consume(message: ConsumerRecord<String, String>, ack: Acknowledgment) {
            val orderEvent = objectMapper.readValue(message.value(), OrderEvent::class.java)
            logger.info("Order event consumed: $orderEvent")

            processPaymentUseCase.processPayment(orderEvent)
            ack.acknowledge()
        }
}