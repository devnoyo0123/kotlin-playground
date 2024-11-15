package com.example.bookorder

import org.springframework.stereotype.Component
import org.springframework.util.MimeType
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class S3FileUploadAdapter(
    private val s3Presigner: S3Presigner,
) : FileUploadPort {

    override fun generatePresignedUrl(
        bucketName: String,
        fileKey: String,
        contentType: MimeType,
        duration: Long,
        timeUnit: TimeUnit
    ): PresignedUrlInfo {

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(
                Duration.of(duration, timeUnit.toChronoUnit()
            ))
            .putObjectRequest(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(contentType.toString())
                    .build()
            )
            .build()

        val presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString()
        return PresignedUrlInfo(presignedUrl, fileKey)
    }
}
