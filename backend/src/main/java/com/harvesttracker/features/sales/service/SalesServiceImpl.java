package com.harvesttracker.features.sales.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.harvest.domain.HarvestRecord;
import com.harvesttracker.features.harvest.repository.HarvestRecordRepository;
import com.harvesttracker.features.masterdata.domain.CropVariant;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.domain.PaymentMethod;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import com.harvesttracker.features.masterdata.repository.CropVariantRepository;
import com.harvesttracker.features.masterdata.repository.FruitTypeRepository;
import com.harvesttracker.features.masterdata.repository.PaymentMethodRepository;
import com.harvesttracker.features.masterdata.repository.UnitOfMeasureRepository;
import com.harvesttracker.features.sales.domain.*;
import com.harvesttracker.features.sales.dto.*;
import com.harvesttracker.features.sales.mapper.*;
import com.harvesttracker.features.sales.repository.*;
import com.harvesttracker.features.sales.specification.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SalesServiceImpl implements SalesService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final HarvestRecordRepository harvestRecordRepository;
    private final FruitTypeRepository fruitTypeRepository;
    private final CropVariantRepository cropVariantRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerMapper customerMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final InvoiceMapper invoiceMapper;
    private final PaymentMapper paymentMapper;

    public SalesServiceImpl(
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            InvoiceRepository invoiceRepository,
            PaymentRepository paymentRepository,
            HarvestRecordRepository harvestRecordRepository,
            FruitTypeRepository fruitTypeRepository,
            CropVariantRepository cropVariantRepository,
            UnitOfMeasureRepository unitOfMeasureRepository,
            PaymentMethodRepository paymentMethodRepository,
            CustomerMapper customerMapper,
            OrderMapper orderMapper,
            OrderItemMapper orderItemMapper,
            InvoiceMapper invoiceMapper,
            PaymentMapper paymentMapper) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.harvestRecordRepository = harvestRecordRepository;
        this.fruitTypeRepository = fruitTypeRepository;
        this.cropVariantRepository = cropVariantRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.customerMapper = customerMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.invoiceMapper = invoiceMapper;
        this.paymentMapper = paymentMapper;
    }

    // --- Customer Management ---

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CustomerDto.CustomerResponse> getAllCustomers(
            int page, int size, String sort, String direction,
            String search, String name, String phone, String email,
            String customerType, String status, Boolean isActive) {

        Sort sortObj = direction.equalsIgnoreCase("DESC") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Customer> spec = CustomerSpecification.filterCustomers(
                search, name, phone, email, customerType, status, isActive);

        Page<Customer> customerPage = customerRepository.findAll(spec, pageable);
        Page<CustomerDto.CustomerResponse> dtoPage = customerPage.map(customerMapper::toResponse);

        return PagedResponse.of(dtoPage.getContent(), customerPage);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDto.CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return customerMapper.toResponse(customer);
    }

    @Override
    public CustomerDto.CustomerResponse createCustomer(CustomerDto.CustomerRequest request) {
        validateCustomerRequest(request, null);

        Customer customer = new Customer();
        customerMapper.updateEntity(customer, request);

        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponse(saved);
    }

    @Override
    public CustomerDto.CustomerResponse updateCustomer(Long id, CustomerDto.CustomerRequest request) {
        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        validateCustomerRequest(request, id);
        customerMapper.updateEntity(customer, request);

        Customer updated = customerRepository.save(customer);
        return customerMapper.toResponse(updated);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setDeletedAt(OffsetDateTime.now());
        customer.setIsActive(false);
        customer.setStatus("INACTIVE");
        customerRepository.save(customer);
    }

    // --- Order Management ---

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderDto.OrderResponse> getAllOrders(
            int page, int size, String sort, String direction,
            Long customerId, String orderStatus, Long cropVariantId, Long farmId,
            LocalDate startDate, LocalDate endDate, String search, Boolean isActive) {

        Sort sortObj = direction.equalsIgnoreCase("DESC") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Order> spec = OrderSpecification.filterOrders(
                customerId, orderStatus, cropVariantId, farmId, startDate, endDate, search, isActive);

        Page<Order> orderPage = orderRepository.findAll(spec, pageable);
        Page<OrderDto.OrderResponse> dtoPage = orderPage.map(orderMapper::toResponse);

        return PagedResponse.of(dtoPage.getContent(), orderPage);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto.OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderDto.OrderResponse createOrder(OrderDto.OrderRequest request) {
        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one order item");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(request.getOrderDate() != null ? request.getOrderDate() : LocalDate.now());
        if (request.getOrderStatus() != null) {
            order.setOrderStatus(request.getOrderStatus().toUpperCase().trim());
        }
        order.setNotes(request.getNotes());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemDto.OrderItemRequest itemReq : request.getItems()) {
            OrderItem item = buildOrderItem(order, itemReq);
            items.add(item);
            total = total.add(item.getLineTotal());
        }

        order.setOrderItems(items);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    @Override
    public OrderDto.OrderResponse updateOrder(Long id, OrderDto.OrderRequest request) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        Customer customer = customerRepository.findByIdAndDeletedAtIsNull(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + request.getCustomerId()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one order item");
        }

        order.setCustomer(customer);
        if (request.getOrderDate() != null) {
            order.setOrderDate(request.getOrderDate());
        }
        if (request.getOrderStatus() != null) {
            order.setOrderStatus(request.getOrderStatus().toUpperCase().trim());
        }
        order.setNotes(request.getNotes());

        order.getOrderItems().clear();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDto.OrderItemRequest itemReq : request.getItems()) {
            OrderItem item = buildOrderItem(order, itemReq);
            order.getOrderItems().add(item);
            total = total.add(item.getLineTotal());
        }

        order.setTotalAmount(total);

        Order updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }

    @Override
    public OrderDto.OrderResponse updateOrderStatus(Long id, String newStatus) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        String targetStatus = newStatus.toUpperCase().trim();
        validateOrderStatusTransition(order.getOrderStatus(), targetStatus);

        order.setOrderStatus(targetStatus);
        Order updated = orderRepository.save(order);
        return orderMapper.toResponse(updated);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setDeletedAt(OffsetDateTime.now());
        order.setIsActive(false);
        orderRepository.save(order);
    }

    // --- Order Items Management ---

    @Override
    public OrderItemDto.OrderItemResponse addOrderItem(Long orderId, OrderItemDto.OrderItemRequest request) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderItem item = buildOrderItem(order, request);
        order.getOrderItems().add(item);

        recalculateOrderTotal(order);
        orderRepository.save(order);

        return orderItemMapper.toResponse(item);
    }

    @Override
    public OrderItemDto.OrderItemResponse updateOrderItem(Long orderId, Long itemId, OrderItemDto.OrderItemRequest request) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderItem item = orderItemRepository.findByIdAndDeletedAtIsNull(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + itemId));

        HarvestRecord harvestRecord = null;
        if (request.getHarvestRecordId() != null) {
            harvestRecord = harvestRecordRepository.findByIdAndDeletedAtIsNull(request.getHarvestRecordId())
                    .orElseThrow(() -> new ResourceNotFoundException("Harvest record not found with id: " + request.getHarvestRecordId()));

            if (harvestRecord.getHarvestQuantity().compareTo(request.getQuantity()) < 0) {
                throw new IllegalArgumentException("Insufficient harvest stock available. Requested: " +
                        request.getQuantity() + ", Available: " + harvestRecord.getHarvestQuantity());
            }
        }

        CropVariant cropVariant = null;
        if (request.getCropVariantId() != null) {
            cropVariant = cropVariantRepository.findByIdAndDeletedAtIsNull(request.getCropVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop variant not found with id: " + request.getCropVariantId()));

            if (!cropVariant.getIsActive()) {
                throw new IllegalArgumentException("Cannot sell inactive crop variant: " + cropVariant.getName());
            }
        }

        FruitType fruitType = null;
        if (request.getFruitTypeId() != null) {
            fruitType = fruitTypeRepository.findByIdAndDeletedAtIsNull(request.getFruitTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with id: " + request.getFruitTypeId()));
        }

        UnitOfMeasure uom = null;
        if (request.getQuantityUomId() != null) {
            uom = unitOfMeasureRepository.findByIdAndDeletedAtIsNull(request.getQuantityUomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quantity UOM not found with id: " + request.getQuantityUomId()));
        }

        item.setHarvestRecord(harvestRecord);
        item.setFruitType(fruitType);
        item.setCropVariant(cropVariant);
        item.setQuantityUom(uom);
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setLineTotal(request.getQuantity().multiply(request.getUnitPrice()));
        item.setNotes(request.getNotes());

        recalculateOrderTotal(order);
        orderRepository.save(order);

        return orderItemMapper.toResponse(item);
    }

    @Override
    public void deleteOrderItem(Long orderId, Long itemId) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        OrderItem item = orderItemRepository.findByIdAndDeletedAtIsNull(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found with id: " + itemId));

        item.setDeletedAt(OffsetDateTime.now());
        item.setIsActive(false);

        recalculateOrderTotal(order);
        orderRepository.save(order);
    }

    // --- Invoice Management ---

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InvoiceDto.InvoiceResponse> getAllInvoices(
            int page, int size, String sort, String direction,
            Long customerId, Long orderId, String invoiceNumber, String invoiceStatus,
            LocalDate startDate, LocalDate endDate, String search, Boolean isActive) {

        Sort sortObj = direction.equalsIgnoreCase("DESC") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Invoice> spec = InvoiceSpecification.filterInvoices(
                customerId, orderId, invoiceNumber, invoiceStatus, startDate, endDate, search, isActive);

        Page<Invoice> invoicePage = invoiceRepository.findAll(spec, pageable);
        Page<InvoiceDto.InvoiceResponse> dtoPage = invoicePage.map(invoiceMapper::toResponse);

        return PagedResponse.of(dtoPage.getContent(), invoicePage);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto.InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    public InvoiceDto.InvoiceResponse createInvoiceForOrder(Long orderId, InvoiceDto.InvoiceRequest request) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!List.of("CONFIRMED", "DISPATCHED", "DELIVERED", "INVOICED", "PAID").contains(order.getOrderStatus().toUpperCase())) {
            throw new IllegalArgumentException("Invoices can only be generated for confirmed or fulfilled orders");
        }

        if (invoiceRepository.existsByOrderIdAndDeletedAtIsNull(orderId)) {
            throw new DuplicateResourceException("Invoice already exists for order id: " + orderId);
        }

        String invNum = "INV-" + System.currentTimeMillis();
        Invoice invoice = new Invoice(order, order.getCustomer(), invNum, request.getDueDate(), order.getTotalAmount());
        if (request.getInvoiceStatus() != null) {
            invoice.setInvoiceStatus(request.getInvoiceStatus().toUpperCase().trim());
        }
        invoice.setNotes(request.getNotes());

        order.setOrderStatus("INVOICED");
        orderRepository.save(order);

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(saved);
    }

    // --- Payment Management ---

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PaymentDto.PaymentResponse> getAllPayments(
            int page, int size, String sort, String direction,
            Long invoiceId, Long paymentMethodId, String paymentStatus, String referenceNumber,
            LocalDate startDate, LocalDate endDate, String search, Boolean isActive) {

        Sort sortObj = direction.equalsIgnoreCase("DESC") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Payment> spec = PaymentSpecification.filterPayments(
                invoiceId, paymentMethodId, paymentStatus, referenceNumber, startDate, endDate, search, isActive);

        Page<Payment> paymentPage = paymentRepository.findAll(spec, pageable);
        Page<PaymentDto.PaymentResponse> dtoPage = paymentPage.map(paymentMapper::toResponse);

        return PagedResponse.of(dtoPage.getContent(), paymentPage);
    }

    @Override
    public PaymentDto.PaymentResponse recordPaymentForInvoice(Long invoiceId, PaymentDto.PaymentRequest request) {
        Invoice invoice = invoiceRepository.findByIdAndDeletedAtIsNull(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        BigDecimal remainingBalance = invoice.getTotalAmount().subtract(invoice.getPaidAmount());
        if (request.getAmount().compareTo(remainingBalance) > 0) {
            throw new IllegalArgumentException("Payment amount (" + request.getAmount() +
                    ") exceeds remaining unpaid balance (" + remainingBalance + ")");
        }

        PaymentMethod paymentMethod = null;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findByIdAndDeletedAtIsNull(request.getPaymentMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + request.getPaymentMethodId()));
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now());
        payment.setAmount(request.getAmount());
        if (request.getPaymentStatus() != null) {
            payment.setPaymentStatus(request.getPaymentStatus().toUpperCase().trim());
        }
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setNotes(request.getNotes());

        if ("COMPLETED".equalsIgnoreCase(payment.getPaymentStatus())) {
            BigDecimal newPaid = invoice.getPaidAmount().add(request.getAmount());
            invoice.setPaidAmount(newPaid);

            if (newPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoice.setInvoiceStatus("PAID");
                if (invoice.getOrder() != null) {
                    invoice.getOrder().setOrderStatus("PAID");
                    orderRepository.save(invoice.getOrder());
                }
            } else {
                invoice.setInvoiceStatus("PARTIALLY_PAID");
            }
            invoiceRepository.save(invoice);
        }

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    // --- Analytics & Summaries ---

    @Override
    @Transactional(readOnly = true)
    public SalesSummaryDto.SalesSummaryResponse getSalesSummary() {
        List<Order> orders = orderRepository.findAll((root, query, cb) -> cb.isNull(root.get("deletedAt")));
        long totalCust = customerRepository.count();

        SalesSummaryDto.SalesSummaryResponse summary = new SalesSummaryDto.SalesSummaryResponse();
        summary.setTotalCustomers(totalCust);
        summary.setTotalOrders(orders.size());

        BigDecimal totalVal = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalOrderValue(totalVal);

        summary.setDraftOrders(orders.stream().filter(o -> "DRAFT".equalsIgnoreCase(o.getOrderStatus())).count());
        summary.setConfirmedOrders(orders.stream().filter(o -> "CONFIRMED".equalsIgnoreCase(o.getOrderStatus())).count());
        summary.setInvoicedOrders(orders.stream().filter(o -> "INVOICED".equalsIgnoreCase(o.getOrderStatus())).count());
        summary.setPaidOrders(orders.stream().filter(o -> "PAID".equalsIgnoreCase(o.getOrderStatus())).count());
        summary.setCancelledOrders(orders.stream().filter(o -> "CANCELLED".equalsIgnoreCase(o.getOrderStatus())).count());

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public RevenueSummaryDto.RevenueSummaryResponse getRevenueSummary() {
        List<Invoice> invoices = invoiceRepository.findAll((root, query, cb) -> cb.isNull(root.get("deletedAt")));

        RevenueSummaryDto.RevenueSummaryResponse summary = new RevenueSummaryDto.RevenueSummaryResponse();
        summary.setTotalInvoices(invoices.size());

        BigDecimal totalInvoiced = invoices.stream().map(Invoice::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCollected = invoices.stream().map(Invoice::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        summary.setTotalInvoicedAmount(totalInvoiced);
        summary.setTotalCollectedRevenue(totalCollected);
        summary.setTotalPendingRevenue(totalInvoiced.subtract(totalCollected));

        summary.setPaidInvoices(invoices.stream().filter(i -> "PAID".equalsIgnoreCase(i.getInvoiceStatus())).count());
        summary.setUnpaidInvoices(invoices.stream().filter(i -> List.of("DRAFT", "ISSUED", "PARTIALLY_PAID").contains(i.getInvoiceStatus().toUpperCase())).count());
        summary.setOverdueInvoices(invoices.stream().filter(i -> "OVERDUE".equalsIgnoreCase(i.getInvoiceStatus())).count());

        return summary;
    }

    // --- Helper Methods ---

    private OrderItem buildOrderItem(Order order, OrderItemDto.OrderItemRequest request) {
        HarvestRecord harvestRecord = null;
        if (request.getHarvestRecordId() != null) {
            harvestRecord = harvestRecordRepository.findByIdAndDeletedAtIsNull(request.getHarvestRecordId())
                    .orElseThrow(() -> new ResourceNotFoundException("Harvest record not found with id: " + request.getHarvestRecordId()));

            if (harvestRecord.getHarvestQuantity().compareTo(request.getQuantity()) < 0) {
                throw new IllegalArgumentException("Insufficient harvest stock available. Requested: " +
                        request.getQuantity() + ", Available: " + harvestRecord.getHarvestQuantity());
            }
        }

        CropVariant cropVariant = null;
        if (request.getCropVariantId() != null) {
            cropVariant = cropVariantRepository.findByIdAndDeletedAtIsNull(request.getCropVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop variant not found with id: " + request.getCropVariantId()));

            if (!cropVariant.getIsActive()) {
                throw new IllegalArgumentException("Cannot sell inactive crop variant: " + cropVariant.getName());
            }
        }

        FruitType fruitType = null;
        if (request.getFruitTypeId() != null) {
            fruitType = fruitTypeRepository.findByIdAndDeletedAtIsNull(request.getFruitTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with id: " + request.getFruitTypeId()));
        }

        UnitOfMeasure uom = null;
        if (request.getQuantityUomId() != null) {
            uom = unitOfMeasureRepository.findByIdAndDeletedAtIsNull(request.getQuantityUomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quantity UOM not found with id: " + request.getQuantityUomId()));
        }

        OrderItem item = new OrderItem(order, request.getQuantity(), request.getUnitPrice());
        item.setHarvestRecord(harvestRecord);
        item.setFruitType(fruitType);
        item.setCropVariant(cropVariant);
        item.setQuantityUom(uom);
        item.setNotes(request.getNotes());
        return item;
    }

    private void recalculateOrderTotal(Order order) {
        BigDecimal total = order.getOrderItems().stream()
                .filter(item -> item.getDeletedAt() == null)
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
    }

    private void validateCustomerRequest(CustomerDto.CustomerRequest request, Long existingId) {
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            String phone = request.getPhone().trim();
            if (existingId == null) {
                if (customerRepository.existsByPhoneAndDeletedAtIsNull(phone)) {
                    throw new DuplicateResourceException("Customer with phone number '" + phone + "' already exists");
                }
            } else {
                if (customerRepository.existsByPhoneAndIdNotAndDeletedAtIsNull(phone, existingId)) {
                    throw new DuplicateResourceException("Customer with phone number '" + phone + "' already exists");
                }
            }
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String email = request.getEmail().trim();
            if (existingId == null) {
                if (customerRepository.existsByEmailAndDeletedAtIsNull(email)) {
                    throw new DuplicateResourceException("Customer with email '" + email + "' already exists");
                }
            } else {
                if (customerRepository.existsByEmailAndIdNotAndDeletedAtIsNull(email, existingId)) {
                    throw new DuplicateResourceException("Customer with email '" + email + "' already exists");
                }
            }
        }
    }

    private void validateOrderStatusTransition(String currentStatus, String targetStatus) {
        if (currentStatus.equalsIgnoreCase(targetStatus)) {
            return;
        }

        if (List.of("PAID", "CANCELLED").contains(currentStatus.toUpperCase())) {
            throw new IllegalArgumentException("Cannot change status of an order that is " + currentStatus);
        }

        if ("DRAFT".equalsIgnoreCase(currentStatus)) {
            if (!List.of("CONFIRMED", "CANCELLED").contains(targetStatus)) {
                throw new IllegalArgumentException("Invalid order status transition from DRAFT to " + targetStatus);
            }
        }
    }
}
