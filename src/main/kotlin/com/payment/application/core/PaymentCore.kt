package com.payment.application.core

import com.payment.adapter.event.PaymentEvent
import com.payment.application.core.strategy.CreditCardPayment
import com.payment.application.core.strategy.PayPalPayment
import com.payment.application.enumeration.MethodPayment
import com.payment.application.port.PaymentStrategy
import org.springframework.stereotype.Component

@Component
class PaymentCore {
    fun makingPayment(paymentEvent: PaymentEvent): PaymentStrategy {
        return when (paymentEvent.methodPayment) {
            MethodPayment.CREDIT_CARD.name -> CreditCardPayment()
            MethodPayment.DEBIT_CARD.name -> CreditCardPayment()
            MethodPayment.PAY_PAL.name -> PayPalPayment()
            else -> throw IllegalArgumentException("Unknown payment type")
        }
    }
}
