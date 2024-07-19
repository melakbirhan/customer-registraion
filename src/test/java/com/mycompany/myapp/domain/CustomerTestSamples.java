package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerTestSamples {

    private static final Random random = new Random();
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Customer getCustomerSample1() {
        return new Customer().id("id1").fullName("fullName1").customerId(1).department("department1").telephone("telephone1");
    }

    public static Customer getCustomerSample2() {
        return new Customer().id("id2").fullName("fullName2").customerId(2).department("department2").telephone("telephone2");
    }

    public static Customer getCustomerRandomSampleGenerator() {
        return new Customer()
            .id(UUID.randomUUID().toString())
            .fullName(UUID.randomUUID().toString())
            .customerId(intCount.incrementAndGet())
            .department(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString());
    }
}
