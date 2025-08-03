package com.yourbank.banking_api.service;

import com.yourbank.banking_api.dto.CustomerDTO;
import com.yourbank.banking_api.exception.ResourceAlreadyExistsException;
import com.yourbank.banking_api.exception.ResourceNotFoundException;
import com.yourbank.banking_api.model.Customer;
import com.yourbank.banking_api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_success() {
        CustomerDTO request = new CustomerDTO(
                null, "shishu", "tiwari", "shishutiwari2880@gmail.com",
                "9682698148", "Model Town", "1"
        );

        when(customerRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(customerRepository.findByIdNumber(any())).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CustomerDTO result = customerService.createCustomer(request);

        assertNotNull(result.id());
        assertEquals("shishu", result.firstName());
        verify(customerRepository).save(any());
    }

    @Test
    void createCustomer_emailExists_throwsException() {
        CustomerDTO request = new CustomerDTO(
                null, "shishu", "Tiwari", "shishutiwari2880@gmail.com",
                "9682698148", "Model Town", "1"
        );

        when(customerRepository.findByEmail(any())).thenReturn(Optional.of(new Customer()));

        assertThrows(ResourceAlreadyExistsException.class,
                () -> customerService.createCustomer(request));
    }

    @Test
    void getCustomerById_success() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("shishu");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.getCustomerById(1L);
        assertEquals("shishu", result.firstName());
    }

    @Test
    void getCustomerById_notFound_throwsException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> customerService.getCustomerById(1L));
    }
}
