package com.harvesttracker.features.sales.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.sales.dto.*;

import java.time.LocalDate;

public interface SalesService {

    // Customer management
    PagedResponse<CustomerDto.CustomerResponse> getAllCustomers(
            int page, int size, String sort, String direction,
            String search, String name, String phone, String email,
            String customerType, String status, Boolean isActive);

    CustomerDto.CustomerResponse getCustomerById(Long id);

    CustomerDto.CustomerResponse createCustomer(CustomerDto.CustomerRequest request);

    CustomerDto.CustomerResponse updateCustomer(Long id, CustomerDto.CustomerRequest request);

    void deleteCustomer(Long id);

    // Order management
    PagedResponse<OrderDto.OrderResponse> getAllOrders(
            int page, int size, String sort, String direction,
            Long customerId, String orderStatus, Long cropVariantId, Long farmId,
            LocalDate startDate, LocalDate endDate, String search, Boolean isActive);

    OrderDto.OrderResponse getOrderById(Long id);

    OrderDto.OrderResponse createOrder(OrderDto.OrderRequest request);

    OrderDto.OrderResponse updateOrder(Long id, OrderDto.OrderRequest request);

    OrderDto.OrderResponse updateOrderStatus(Long id, String status);

    void deleteOrder(Long id);

    // Order Item management
    OrderItemDto.OrderItemResponse addOrderItem(Long orderId, OrderItemDto.OrderItemRequest request);

    OrderItemDto.OrderItemResponse updateOrderItem(Long orderId, Long itemId, OrderItemDto.OrderItemRequest request);

    void deleteOrderItem(Long orderId, Long itemId);

    // Invoice management
    PagedResponse<InvoiceDto.InvoiceResponse> getAllInvoices(
            int page, int size, String sort, String direction,
            Long customerId, Long orderId, String invoiceNumber, String invoiceStatus,
            LocalDate startDate, LocalDate endDate, String search, Boolean isActive);

    InvoiceDto.InvoiceResponse getInvoiceById(Long id);

    InvoiceDto.InvoiceResponse createInvoiceForOrder(Long orderId, InvoiceDto.InvoiceRequest request);

    // Payment management
    PagedResponse<PaymentDto.PaymentResponse> getAllPayments(
            int page, int size, String sort, String direction,
            Long invoiceId, Long paymentMethodId, String paymentStatus, String referenceNumber,
            LocalDate startDate, LocalDate endDate, String search, Boolean isActive);

    PaymentDto.PaymentResponse recordPaymentForInvoice(Long invoiceId, PaymentDto.PaymentRequest request);

    // Analytics & Summaries
    SalesSummaryDto.SalesSummaryResponse getSalesSummary();

    RevenueSummaryDto.RevenueSummaryResponse getRevenueSummary();
}
