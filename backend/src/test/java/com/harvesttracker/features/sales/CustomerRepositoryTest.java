package com.harvesttracker.features.sales;

import com.harvesttracker.features.sales.domain.Customer;
import com.harvesttracker.features.sales.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testSaveAndFindCustomer() {
        Customer customer = new Customer("Agri Traders Corp", "+919988776655", "WHOLESALE");
        customer.setEmail("info@agritraders.local");
        customer.setAddress("APMC Market, Pune");

        Customer saved = customerRepository.save(customer);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Agri Traders Corp");

        Optional<Customer> found = customerRepository.findByIdAndDeletedAtIsNull(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getPhone()).isEqualTo("+919988776655");
    }

    @Test
    void testPhoneAndEmailUniquenessCheck() {
        Customer c1 = new Customer("Customer One", "+919111122222", "RETAIL");
        c1.setEmail("cust1@harvest.local");
        customerRepository.save(c1);

        boolean phoneExists = customerRepository.existsByPhoneAndDeletedAtIsNull("+919111122222");
        assertThat(phoneExists).isTrue();

        boolean emailExists = customerRepository.existsByEmailAndDeletedAtIsNull("cust1@harvest.local");
        assertThat(emailExists).isTrue();
    }
}
