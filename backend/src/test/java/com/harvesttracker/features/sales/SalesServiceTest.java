package com.harvesttracker.features.sales;

import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.sales.dto.*;
import com.harvesttracker.features.sales.service.SalesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SalesServiceTest {

    @Autowired
    private SalesService salesService;

    private CustomerDto.CustomerResponse testCustomer;

    @BeforeEach
    void setUp() {
        CustomerDto.CustomerRequest req = new CustomerDto.CustomerRequest();
        req.setName("Fresh Supermarket Ltd");
        req.setPhone("+919888877777");
        req.setEmail("orders@freshsupermarket.local");
        req.setCustomerType("CORPORATE");

        testCustomer = salesService.createCustomer(req);
    }

    @Test
    void testCreateCustomer_DuplicatePhoneThrowsException() {
        CustomerDto.CustomerRequest req = new CustomerDto.CustomerRequest();
        req.setName("Another Customer");
        req.setPhone("+919888877777"); // Duplicate phone!

        assertThatThrownBy(() -> salesService.createCustomer(req))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void testCreateOrder_MissingItemsThrowsException() {
        OrderDto.OrderRequest req = new OrderDto.OrderRequest();
        req.setCustomerId(testCustomer.getId());
        req.setItems(List.of()); // Empty items!

        assertThatThrownBy(() -> salesService.createOrder(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one order item");
    }

    @Test
    void testCreateOrder_SuccessAndInvoiceGeneration() {
        OrderDto.OrderRequest orderReq = new OrderDto.OrderRequest();
        orderReq.setCustomerId(testCustomer.getId());
        orderReq.setOrderDate(LocalDate.now());

        OrderItemDto.OrderItemRequest item = new OrderItemDto.OrderItemRequest();
        item.setQuantity(new BigDecimal("100.000"));
        item.setUnitPrice(new BigDecimal("50.00"));

        orderReq.setItems(List.of(item));

        OrderDto.OrderResponse orderResp = salesService.createOrder(orderReq);

        assertThat(orderResp).isNotNull();
        assertThat(orderResp.getTotalAmount()).isEqualByComparingTo("5000.00");
        assertThat(orderResp.getOrderStatus()).isEqualTo("DRAFT");

        // Transition order status to CONFIRMED
        salesService.updateOrderStatus(orderResp.getId(), "CONFIRMED");

        // Generate Invoice
        InvoiceDto.InvoiceRequest invReq = new InvoiceDto.InvoiceRequest();
        invReq.setOrderId(orderResp.getId());
        invReq.setDueDate(LocalDate.now().plusDays(30));

        InvoiceDto.InvoiceResponse invResp = salesService.createInvoiceForOrder(orderResp.getId(), invReq);

        assertThat(invResp).isNotNull();
        assertThat(invResp.getTotalAmount()).isEqualByComparingTo("5000.00");
        assertThat(invResp.getPaidAmount()).isEqualByComparingTo("0.00");

        // Record Partial Payment
        PaymentDto.PaymentRequest payReq = new PaymentDto.PaymentRequest();
        payReq.setAmount(new BigDecimal("2000.00"));
        payReq.setPaymentStatus("COMPLETED");

        PaymentDto.PaymentResponse payResp = salesService.recordPaymentForInvoice(invResp.getId(), payReq);

        assertThat(payResp).isNotNull();
        assertThat(payResp.getAmount()).isEqualByComparingTo("2000.00");

        // Get updated invoice
        InvoiceDto.InvoiceResponse updatedInv = salesService.getInvoiceById(invResp.getId());
        assertThat(updatedInv.getPaidAmount()).isEqualByComparingTo("2000.00");
        assertThat(updatedInv.getInvoiceStatus()).isEqualTo("PARTIALLY_PAID");
    }

    @Test
    void testPaymentExceedingBalanceThrowsException() {
        OrderDto.OrderRequest orderReq = new OrderDto.OrderRequest();
        orderReq.setCustomerId(testCustomer.getId());

        OrderItemDto.OrderItemRequest item = new OrderItemDto.OrderItemRequest();
        item.setQuantity(new BigDecimal("10.000"));
        item.setUnitPrice(new BigDecimal("100.00"));
        orderReq.setItems(List.of(item));

        OrderDto.OrderResponse orderResp = salesService.createOrder(orderReq);
        salesService.updateOrderStatus(orderResp.getId(), "CONFIRMED");

        InvoiceDto.InvoiceRequest invReq = new InvoiceDto.InvoiceRequest();
        invReq.setOrderId(orderResp.getId());
        invReq.setDueDate(LocalDate.now().plusDays(15));

        InvoiceDto.InvoiceResponse invResp = salesService.createInvoiceForOrder(orderResp.getId(), invReq);

        // Attempt payment exceeding total amount ($1000 total, pay $1500)
        PaymentDto.PaymentRequest payReq = new PaymentDto.PaymentRequest();
        payReq.setAmount(new BigDecimal("1500.00"));

        assertThatThrownBy(() -> salesService.recordPaymentForInvoice(invResp.getId(), payReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exceeds remaining unpaid balance");
    }

    @Test
    void testSalesAndRevenueSummaries() {
        SalesSummaryDto.SalesSummaryResponse salesSummary = salesService.getSalesSummary();
        assertThat(salesSummary).isNotNull();
        assertThat(salesSummary.getTotalCustomers()).isGreaterThanOrEqualTo(1);

        RevenueSummaryDto.RevenueSummaryResponse revenueSummary = salesService.getRevenueSummary();
        assertThat(revenueSummary).isNotNull();
    }
}
