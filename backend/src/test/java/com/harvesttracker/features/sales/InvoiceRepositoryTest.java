package com.harvesttracker.features.sales;

import com.harvesttracker.features.sales.domain.Customer;
import com.harvesttracker.features.sales.domain.Invoice;
import com.harvesttracker.features.sales.domain.Order;
import com.harvesttracker.features.sales.domain.Payment;
import com.harvesttracker.features.sales.repository.CustomerRepository;
import com.harvesttracker.features.sales.repository.InvoiceRepository;
import com.harvesttracker.features.sales.repository.OrderRepository;
import com.harvesttracker.features.sales.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Order testOrder;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.save(new Customer("Invoice Test Customer", "+919777766666", "WHOLESALE"));
        Order order = new Order(testCustomer, LocalDate.now());
        order.setTotalAmount(new BigDecimal("5000.00"));
        order.setOrderStatus("CONFIRMED");
        testOrder = orderRepository.save(order);
    }

    @Test
    void testSaveInvoiceAndPayments() {
        Invoice invoice = new Invoice(testOrder, testCustomer, "INV-TEST-001", LocalDate.now().plusDays(15), new BigDecimal("5000.00"));
        invoice.setInvoiceStatus("ISSUED");

        Invoice savedInv = invoiceRepository.save(invoice);
        assertThat(savedInv.getId()).isNotNull();

        Payment payment = new Payment(savedInv, LocalDate.now(), new BigDecimal("2000.00"));
        Payment savedPayment = paymentRepository.save(payment);

        assertThat(savedPayment.getId()).isNotNull();
        assertThat(paymentRepository.findByInvoiceIdAndDeletedAtIsNull(savedInv.getId())).hasSize(1);

        Optional<Invoice> found = invoiceRepository.findByIdAndDeletedAtIsNull(savedInv.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getInvoiceNumber()).isEqualTo("INV-TEST-001");
    }
}
