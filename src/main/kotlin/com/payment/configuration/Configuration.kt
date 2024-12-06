package com.payment.configuration


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import io.awspring.cloud.sqs.listener.acknowledgement.handler.AcknowledgementMode
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI


@Configuration
class Configuration(
    @Value("\${cloud.aws.endpoint.uri}") private var host: String,
    @Value("\${cloud.aws.credentials.access-key}") private var accessKeyId: String,
    @Value("\${cloud.aws.credentials.secret-key}") private var secretAccessKey: String,
    @Value("\${cloud.aws.region.static}") private var region: String
) {
    @Bean
    fun sqsAsyncClient(): SqsAsyncClient {
        return SqsAsyncClient.builder()
            .endpointOverride(URI.create(host)) // Endpoint do LocalStack
            .credentialsProvider { credentialProvider(accessKeyId, secretAccessKey) }
            .region(Region.of(region))
            .build()
    }

    @Bean
    fun defaultSqsListenerContainerFactory(): SqsMessageListenerContainerFactory<Any> {
        return SqsMessageListenerContainerFactory.builder<Any>()
            .sqsAsyncClient(sqsAsyncClient())
            .configure { it ->
                it.acknowledgementMode(AcknowledgementMode.MANUAL)
                it.messageConverter(
                    SqsMessagingMessageConverter().apply {
                        this.setObjectMapper(objectMapper())
                    }
                )
            }
            .build()
    }

    @Bean
    fun coroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(
                KotlinModule.Builder()
                    .build()
            )
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    companion object {
        fun credentialProvider(accessKeyId: String, secretAccessKey: String): AwsCredentials {
            return object : AwsCredentials {
                override fun accessKeyId(): String = accessKeyId
                override fun secretAccessKey(): String = secretAccessKey
            }
        }
    }
}
