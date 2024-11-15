package com.example.bookorder

import org.springframework.util.MimeType
import java.util.concurrent.TimeUnit

/**
 * 파일 업로드 포트
 * S3Presigner를 이용하여 파일 업로드에 필요한 Presigned URL을 생성하는 기능을 제공한다.
 * fileKey : 파일의 경로 + file명.확장자
 * contentType : 파일의 MIME 타입
 */
interface FileUploadPort {
    fun generatePresignedUrl(
        bucketName: String,
        fileKey: String,
        contentType: MimeType,
        duration: Long = 10,
        timeUnit: TimeUnit = TimeUnit.MINUTES
    ): PresignedUrlInfo
}