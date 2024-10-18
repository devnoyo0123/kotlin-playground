package com.example.bookorder.book

import com.example.bookorder.core.AuditEntity
import jakarta.persistence.*
import java.math.BigDecimal


@Entity
@Table(name = "book_tbl")
class BookEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var author: String,

    @Column(nullable = false)
    var price: BigDecimal,

    @Column(nullable = false)
    var stock: Int,

    @Version  // 낙관적 락을 위한 버전 필드
    var version: Long = 0
) : AuditEntity() {
}