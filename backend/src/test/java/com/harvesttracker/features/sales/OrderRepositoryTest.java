package com.harvesttracker.features.sales;

import com.harvesttracker.features.sales.domain.Customer;
import com.harvesttracker.features.sales.domain.Order;
import com.harvesttracker.features.sales.domain.OrderItem;
import com.harvesttracker.features.sales.repository.CustomerRepository;
import com.harvesttracker.features.sales.repository.OrderRepository;
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
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.save(new Customer("Order Test Customer", "+919876500000", "RETAIL"));
    }

    @Test
    void testSaveOrderWithItems() {
        Order order = new Order(testCustomer, LocalDate.now());
        order.setOrderStatus("DRAFT");

        OrderItem item1 = new OrderItem(order, new BigDecimal("10.000"), new BigDecimal("150.00"));
        OrderItem item2 = new OrderItem(order, new BigDecimal("5.000"), new BigDecimal("200.00"));

        order.getOrderItems().add(item1);
        order.getOrderItems().add(item2);
        order.setTotalAmount(item1.getLineTotal().add(item2.getLineTotal())); // 1500 + 1000 = 2500

        Order saved = orderRepository.save(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTotalAmount()).isEqualByComparingTo("2500.00");
        assertThat(saved.getOrderItems()).hasSize(2);

        Optional<Order> found = orderRepository.findByIdAndDeletedAtIsNull(saved.getId());
        assertThat(found).isPresent();
    }
}
