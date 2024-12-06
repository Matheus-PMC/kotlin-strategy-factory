package com.payment.adapter

import com.payment.adapter.event.PaymentEvent
import com.payment.application.core.PaymentCore
import com.payment.extension.LoggableExtension
import io.awspring.cloud.sqs.annotation.SqsListener
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import java.util.*

@Component
class PaymentListener(
    private val coroutineScope: CoroutineScope,
    private val paymentCore: PaymentCore
) : LoggableExtension() {

    @SqsListener("sample-queue")
    fun receivePayment(paymentEvent: PaymentEvent, ack: Acknowledgement): Job {
        paymentEvent.correlationId = UUID.randomUUID().toString()
        log.info("Entrou correlationId: ${paymentEvent.correlationId}")

        return coroutineScope.launch {
            runCatching {
                paymentCore.makingPayment(paymentEvent).processPayment(paymentEvent.amount)
            }.onSuccess {
                log.info("mensagem finalizada com sucesso: ${paymentEvent.correlationId}")
                ack.acknowledge()
            }.onFailure { ex ->
                log.error("mensagem finalizada com erro: ${paymentEvent.correlationId}", ex)
            }.also {
                log.info("Finalizou: ${paymentEvent.correlationId}")
            }
        }
    }
}
