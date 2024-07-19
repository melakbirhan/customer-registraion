package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB reactive repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {}
