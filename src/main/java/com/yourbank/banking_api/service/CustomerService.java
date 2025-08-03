package com.yourbank.banking_api.service;

import com.yourbank.banking_api.dto.CustomerDTO;
import com.yourbank.banking_api.exception.ResourceAlreadyExistsException;
import com.yourbank.banking_api.exception.ResourceNotFoundException;
import com.yourbank.banking_api.model.Customer;
import com.yourbank.banking_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        // Check if email already exists
        if (customerRepository.findByEmail(customerDTO.email()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        // Check if ID number already exists
        if (customerRepository.findByIdNumber(customerDTO.idNumber()).isPresent()) {
            throw new ResourceAlreadyExistsException("ID number already exists");
        }

        Customer customer = Customer.builder()
                .firstName(customerDTO.firstName())
                .lastName(customerDTO.lastName())
                .email(customerDTO.email())
                .phone(customerDTO.phone())
                .address(customerDTO.address())
                .idNumber(customerDTO.idNumber())
                .createdAt(LocalDateTime.now())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        return toDTO(savedCustomer);
    }

    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return toDTO(customer);
    }

    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private CustomerDTO toDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .idNumber(customer.getIdNumber())
                .build();
    }
}
