package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CustomerAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.enumeration.Session;
import com.mycompany.myapp.repository.CustomerRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CustomerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CustomerResourceIT {

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_CUSTOMER_ID = 1;
    private static final Integer UPDATED_CUSTOMER_ID = 2;

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final Session DEFAULT_SESSION_TYPE = Session.Morning;
    private static final Session UPDATED_SESSION_TYPE = Session.AfterNoon;

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/customers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Customer customer;

    private Customer insertedCustomer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createEntity() {
        Customer customer = new Customer()
            .fullName(DEFAULT_FULL_NAME)
            .customerId(DEFAULT_CUSTOMER_ID)
            .department(DEFAULT_DEPARTMENT)
            .sessionType(DEFAULT_SESSION_TYPE)
            .telephone(DEFAULT_TELEPHONE)
            .date(DEFAULT_DATE);
        return customer;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Customer createUpdatedEntity() {
        Customer customer = new Customer()
            .fullName(UPDATED_FULL_NAME)
            .customerId(UPDATED_CUSTOMER_ID)
            .department(UPDATED_DEPARTMENT)
            .sessionType(UPDATED_SESSION_TYPE)
            .telephone(UPDATED_TELEPHONE)
            .date(UPDATED_DATE);
        return customer;
    }

    @BeforeEach
    public void initTest() {
        customer = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCustomer != null) {
            customerRepository.delete(insertedCustomer).block();
            insertedCustomer = null;
        }
    }

    @Test
    void createCustomer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Customer
        var returnedCustomer = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customer))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Customer.class)
            .returnResult()
            .getResponseBody();

        // Validate the Customer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertCustomerUpdatableFieldsEquals(returnedCustomer, getPersistedCustomer(returnedCustomer));

        insertedCustomer = returnedCustomer;
    }

    @Test
    void createCustomerWithExistingId() throws Exception {
        // Create the Customer with an existing ID
        customer.setId("existing_id");

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCustomersAsStream() {
        // Initialize the database
        customerRepository.save(customer).block();

        List<Customer> customerList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Customer.class)
            .getResponseBody()
            .filter(customer::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(customerList).isNotNull();
        assertThat(customerList).hasSize(1);
        Customer testCustomer = customerList.get(0);

        assertCustomerAllPropertiesEquals(customer, testCustomer);
    }

    @Test
    void getAllCustomers() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get all the customerList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(customer.getId()))
            .jsonPath("$.[*].fullName")
            .value(hasItem(DEFAULT_FULL_NAME))
            .jsonPath("$.[*].customerId")
            .value(hasItem(DEFAULT_CUSTOMER_ID))
            .jsonPath("$.[*].department")
            .value(hasItem(DEFAULT_DEPARTMENT))
            .jsonPath("$.[*].sessionType")
            .value(hasItem(DEFAULT_SESSION_TYPE.toString()))
            .jsonPath("$.[*].telephone")
            .value(hasItem(DEFAULT_TELEPHONE))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));
    }

    @Test
    void getCustomer() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        // Get the customer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, customer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(customer.getId()))
            .jsonPath("$.fullName")
            .value(is(DEFAULT_FULL_NAME))
            .jsonPath("$.customerId")
            .value(is(DEFAULT_CUSTOMER_ID))
            .jsonPath("$.department")
            .value(is(DEFAULT_DEPARTMENT))
            .jsonPath("$.sessionType")
            .value(is(DEFAULT_SESSION_TYPE.toString()))
            .jsonPath("$.telephone")
            .value(is(DEFAULT_TELEPHONE))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()));
    }

    @Test
    void getNonExistingCustomer() {
        // Get the customer
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCustomer() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer
        Customer updatedCustomer = customerRepository.findById(customer.getId()).block();
        updatedCustomer
            .fullName(UPDATED_FULL_NAME)
            .customerId(UPDATED_CUSTOMER_ID)
            .department(UPDATED_DEPARTMENT)
            .sessionType(UPDATED_SESSION_TYPE)
            .telephone(UPDATED_TELEPHONE)
            .date(UPDATED_DATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCustomer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedCustomer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCustomerToMatchAllProperties(updatedCustomer);
    }

    @Test
    void putNonExistingCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, customer.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(customer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer.fullName(UPDATED_FULL_NAME).telephone(UPDATED_TELEPHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Customer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCustomer, customer), getPersistedCustomer(customer));
    }

    @Test
    void fullUpdateCustomerWithPatch() throws Exception {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the customer using partial update
        Customer partialUpdatedCustomer = new Customer();
        partialUpdatedCustomer.setId(customer.getId());

        partialUpdatedCustomer
            .fullName(UPDATED_FULL_NAME)
            .customerId(UPDATED_CUSTOMER_ID)
            .department(UPDATED_DEPARTMENT)
            .sessionType(UPDATED_SESSION_TYPE)
            .telephone(UPDATED_TELEPHONE)
            .date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCustomer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCustomer))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Customer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCustomerUpdatableFieldsEquals(partialUpdatedCustomer, getPersistedCustomer(partialUpdatedCustomer));
    }

    @Test
    void patchNonExistingCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, customer.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customer))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCustomer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        customer.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(customer))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Customer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCustomer() {
        // Initialize the database
        insertedCustomer = customerRepository.save(customer).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the customer
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, customer.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return customerRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Customer getPersistedCustomer(Customer customer) {
        return customerRepository.findById(customer.getId()).block();
    }

    protected void assertPersistedCustomerToMatchAllProperties(Customer expectedCustomer) {
        assertCustomerAllPropertiesEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
    }

    protected void assertPersistedCustomerToMatchUpdatableProperties(Customer expectedCustomer) {
        assertCustomerAllUpdatablePropertiesEquals(expectedCustomer, getPersistedCustomer(expectedCustomer));
    }
}
