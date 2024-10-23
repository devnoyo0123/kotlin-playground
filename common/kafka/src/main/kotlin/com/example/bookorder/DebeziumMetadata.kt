package com.example.bookorder

abstract class DebeziumMetadata(
    open val deleted: String? = null,
    open val op: String? = null,
    open val sourceTsMs: Long? = null,
    open val db: String? = null,
    open val table: String? = null
)