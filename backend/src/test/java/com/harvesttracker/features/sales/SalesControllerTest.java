package com.harvesttracker.features.sales;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harvesttracker.common.security.JwtTokenProvider;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.sales.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SalesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded"));

        Role managerRole = roleRepository.findByCodeAndDeletedAtIsNull("MANAGER")
                .orElseThrow(() -> new IllegalStateException("MANAGER role not seeded"));

        User adminUser = userRepository.findByEmailAndDeletedAtIsNull("admin@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("System Admin");
                    u.setEmail("admin@harvesttracker.local");
                    u.setPasswordHash("hashed_password");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });

        User normalUser = userRepository.findByEmailAndDeletedAtIsNull("manager@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Farm Manager");
                    u.setEmail("manager@harvesttracker.local");
                    u.setPasswordHash("hashed_password");
                    u.setRole(managerRole);
                    return userRepository.save(u);
                });

        adminToken = jwtTokenProvider.generateAccessToken(
                adminUser.getId(), adminUser.getEmail(), "ADMIN", List.of("ROLE_ADMIN"));

        userToken = jwtTokenProvider.generateAccessToken(
                normalUser.getId(), normalUser.getEmail(), "MANAGER", List.of("ROLE_MANAGER"));
    }

    @Test
    void testGetAllCustomers_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllCustomers_Authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/customers")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetSalesSummary() throws Exception {
        mockMvc.perform(get("/api/v1/sales/summary")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCustomers").isNumber());
    }

    @Test
    void testGetRevenueSummary() throws Exception {
        mockMvc.perform(get("/api/v1/sales/revenue")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalInvoices").isNumber());
    }

    @Test
    void testSalesFullLifecycle_AdminSuccess() throws Exception {
        // 1. Create Customer
        CustomerDto.CustomerRequest custReq = new CustomerDto.CustomerRequest();
        custReq.setName("Global Agri Importers");
        custReq.setPhone("+919900011122");
        custReq.setCustomerType("EXPORT");

        String custResult = mockMvc.perform(post("/api/v1/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(custReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Global Agri Importers"))
                .andReturn().getResponse().getContentAsString();

        Long custId = objectMapper.readTree(custResult).get("data").get("id").asLong();

        // 2. Create Order
        OrderDto.OrderRequest orderReq = new OrderDto.OrderRequest();
        orderReq.setCustomerId(custId);
        orderReq.setOrderDate(LocalDate.now());

        OrderItemDto.OrderItemRequest itemReq = new OrderItemDto.OrderItemRequest();
        itemReq.setQuantity(new BigDecimal("50.000"));
        itemReq.setUnitPrice(new BigDecimal("200.00"));
        orderReq.setItems(List.of(itemReq));

        String orderResult = mockMvc.perform(post("/api/v1/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.totalAmount").value(10000.00))
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(orderResult).get("data").get("id").asLong();

        // 3. Update Order Status (DRAFT -> CONFIRMED)
        mockMvc.perform(patch("/api/v1/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderStatus").value("CONFIRMED"));

        // 4. Generate Invoice
        InvoiceDto.InvoiceRequest invReq = new InvoiceDto.InvoiceRequest();
        invReq.setOrderId(orderId);
        invReq.setDueDate(LocalDate.now().plusDays(30));

        String invResult = mockMvc.perform(post("/api/v1/invoices")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.totalAmount").value(10000.00))
                .andReturn().getResponse().getContentAsString();

        Long invId = objectMapper.readTree(invResult).get("data").get("id").asLong();

        // 5. Record Payment
        PaymentDto.PaymentRequest payReq = new PaymentDto.PaymentRequest();
        payReq.setAmount(new BigDecimal("10000.00"));
        payReq.setPaymentStatus("COMPLETED");

        mockMvc.perform(post("/api/v1/payments/invoices/" + invId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.amount").value(10000.00));

        // 6. Verify Invoice Status is PAID
        mockMvc.perform(get("/api/v1/invoices/" + invId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.invoiceStatus").value("PAID"));
    }
}
