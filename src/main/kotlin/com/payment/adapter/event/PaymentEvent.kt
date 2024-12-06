package com.payment.adapter.event

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class PaymentEvent(
    @JsonProperty("correlationId")
    var correlationId: String?,
    @JsonProperty("amount")
    val amount: BigDecimal,
    @JsonProperty("methodPayment")
    val methodPayment: String
)
