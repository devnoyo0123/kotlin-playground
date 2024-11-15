package com.example.bookorder.controller

import com.example.bookorder.FileUploadPort
import com.example.bookorder.RestApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MimeType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/files")
class FileUploadController(
    private val fileUploadPort: FileUploadPort
) {
    @PostMapping("/presigned-url")
    fun getPresignedUrl(
        @RequestBody request: FileUpload.Request
    ): ResponseEntity<RestApiResponse<FileUpload.Response>> {
        val presignedUrlInfo = fileUploadPort.generatePresignedUrl(
            bucketName = "mildang-crm",
            fileKey = request.fileKey,
            contentType = request.contentType,
            duration = request.duration ?: 10,
            timeUnit = request.timeUnit ?: TimeUnit.MINUTES
        )

        return RestApiResponse.success(
            data = FileUpload.Response(
                presignedUrl = presignedUrlInfo.presignedUrl,
                fileKey = presignedUrlInfo.fileKey
            ),
            message = "Presigned URL generated successfully",
            HttpStatus.OK
        )
    }
}

class FileUpload {
    data class Request(
        val fileKey: String,
        val contentType: MimeType,
        val duration: Long? = null,
        val timeUnit: TimeUnit? = null
    )

    data class Response(
        val presignedUrl: String,
        val fileKey: String
    )
}