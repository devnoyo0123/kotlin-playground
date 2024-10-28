package com.example.bookorder.consumer

import com.example.bookorder.HandlePaymentProcessingDLQUseCase
import com.example.bookorder.MySQLConnectorObjectMapper
import com.example.bookorder.payment.PaymentEvent
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class PaymentProcessingDLQWorker(
    private val handlePaymentProcessingDLQUseCase: HandlePaymentProcessingDLQUseCase
)  {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = MySQLConnectorObjectMapper.objectMapper

    @KafkaListener(
        topics = ["local.test.payment_event_tbl.DLT"],
        groupId = "payment-dlq-worker-0",
        containerFactory = "kafkaListenerContainerFactory")

    fun consume(message: ConsumerRecord<String, String>) {
        val paymentEvent = objectMapper.readValue(message.value(), PaymentEvent::class.java)
        logger.info("Payment event consumed: $paymentEvent")

        handlePaymentProcessingDLQUseCase.cancelPayment(paymentEvent)
    }
}