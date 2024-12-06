package com.payment.application.core.strategy

import com.payment.application.port.PaymentStrategy
import com.payment.extension.LoggableExtension
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class DebitCardPayment : PaymentStrategy, LoggableExtension() {
    override fun processPayment(amount: BigDecimal) {
        log.info("Processing payment of $$amount using Debit Card.")
    }
}
