package com.example.bookorder.order

import com.example.bookorder.book.BookId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class OrderAdapter(private val orderRepository: OrderRepository) : OrderPort {
    override fun findById(id: OrderId): Order? {
        return orderRepository.findByIdOrNull(id.value)?.let {
            OrderEntityConverter.toOrderModel(it)
        }
    }

    override fun save(order: Order): Order {
        val orderEntity = OrderEntityConverter.toOrderEntity(order)
        val savedEntity = orderRepository.save(orderEntity)
        return OrderEntityConverter.toOrderModel(savedEntity)
    }

    override fun findByIdempotencyKey(idempotencyKey: String): Order? {
        return orderRepository.findByIdempotencyKey(idempotencyKey)?.let {
            OrderEntityConverter.toOrderModel(it)
        }
    }

    override fun deleteAll() {
        orderRepository.deleteAll()
    }
}

object OrderEntityConverter {
    fun toOrderEntity(order: Order): OrderEntity {
        val orderEntity = OrderEntity(
            id = order.id?.value,
            idempotencyKey = order.idempotencyKey,
            totalAmount = order.totalAmount,
            status = order.status
        ).apply {
            createdAt = order.createdAt
            updatedAt = order.updatedAt
            deletedAt = order.deletedAt
        }

        order.orderItems.forEach { orderItem ->
            orderEntity.addOrderItem(toOrderItemEntity(orderItem))
        }

        return orderEntity
    }

    fun toOrderModel(entity: OrderEntity): Order {
        return Order(
            id = OrderId.of(entity.id),
            idempotencyKey = entity.idempotencyKey,
            totalAmount = entity.totalAmount,
            status = entity.status,
            orderItems = entity.orderItems.map { toOrderItemModel(it) }
        ).apply {
            createdAt = entity.createdAt
            updatedAt = entity.updatedAt
            deletedAt = entity.deletedAt
        }
    }

    private fun toOrderItemEntity(orderItem: OrderItem): OrderItemEntity {
        return OrderItemEntity(
            id = orderItem.getEntityIdOrNull()?.value,
            bookId = orderItem.bookId.value,
            quantity = orderItem.quantity,
            price = orderItem.price
        ).apply {
            createdAt = orderItem.createdAt
            updatedAt = orderItem.updatedAt
            deletedAt = orderItem.deletedAt
        }
    }


    private fun toOrderItemModel(entity: OrderItemEntity): OrderItem {
        return OrderItem(
            id = OrderItemId.of(entity.id),
            bookId = BookId.of(entity.bookId),
            quantity = entity.quantity,
            price = entity.price
        ).apply {
            createdAt = entity.createdAt
            updatedAt = entity.updatedAt
            deletedAt = entity.deletedAt
        }
    }
}