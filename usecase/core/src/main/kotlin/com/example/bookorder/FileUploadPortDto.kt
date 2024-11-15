package com.example.bookorder

/**
 * 파일 업로드를 위한 포트
 * S3, GCS 등의 클라우드 스토리지에 파일을 업로드할 때 사용
 * @see S3FileUploadAdapter
 * presignedUrl: 업로드할 파일을 저장할 URL
 * fileKey: 업로드할 파일의 키
 *
 */
data class PresignedUrlInfo(
    val presignedUrl: String,
    val fileKey: String
)