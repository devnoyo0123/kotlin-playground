package com.example.bookorder

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cloud.aws.s3")
data class S3Properties(
    val region: String,
    val accessKey: String,
    val secretKey: String,
){
    init {
        require(region.isNotBlank()) { "AWS region must not be blank" }
        require(accessKey.isNotBlank()) { "AWS access key must not be blank" }
        require(secretKey.isNotBlank()) { "AWS secret key must not be blank" }
    }
}

