package com.courseRegistration.studentRegistration.controller;

import com.courseRegistration.studentRegistration.dto.ApiMessage;
import com.courseRegistration.studentRegistration.dto.PaymentReceiptDTO;
import com.courseRegistration.studentRegistration.dto.PaymentRequest;
import com.courseRegistration.studentRegistration.dto.RefundRequest;
import com.courseRegistration.studentRegistration.model.Payment;
import com.courseRegistration.studentRegistration.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public Payment createPayment(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/transaction/{transactionRef}")
    public Payment getPaymentByTransactionRef(@PathVariable String transactionRef) {
        return paymentService.getPaymentByTransactionRef(transactionRef);
    }

    @GetMapping("/student/{studentId}")
    public List<Payment> getPaymentsByStudent(@PathVariable Long studentId) {
        return paymentService.getPaymentsByStudentId(studentId);
    }

    @PostMapping("/{id}/refund")
    public Payment refundPayment(@PathVariable Long id, @RequestBody RefundRequest request) {
        return paymentService.processRefund(id, request);
    }

    @GetMapping("/{id}/receipt")
    public PaymentReceiptDTO getReceipt(@PathVariable Long id) {
        return paymentService.generateReceipt(id);
    }

}