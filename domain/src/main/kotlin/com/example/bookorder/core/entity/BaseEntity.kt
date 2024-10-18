package com.example.bookorder.core.entity

interface EntityId<T> {
    val value: T
}

interface BaseEntity<ID : EntityId<*>> {
    val id: ID?

    fun getEntityId(): ID = id ?: throw IllegalStateException("${this::class.simpleName} ID is not initialized")

    fun getEntityIdOrNull(): ID? = id
}