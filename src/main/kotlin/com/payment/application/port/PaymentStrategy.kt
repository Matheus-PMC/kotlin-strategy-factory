package com.payment.application.port

import java.math.BigDecimal

interface PaymentStrategy {
    fun processPayment(amount: BigDecimal)
}
