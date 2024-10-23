package com.example.bookorder.consumer

import com.example.bookorder.order.OrderCreatedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class MyConsumer(
    @Qualifier("mysqlConnectorObjectMapper") private val objectMapper: ObjectMapper,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = ["local.test.order_created_event_tbl"],
        groupId = "payment-worker-0",
        containerFactory = "kafkaListenerContainerFactory")

        fun consume(message: ConsumerRecord<String, String>) {

            val orderEvent = objectMapper.readValue(message.value(), OrderCreatedEvent::class.java)
            logger.info("Order event consumed: $orderEvent")
        }
}