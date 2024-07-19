package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.Session;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Customer.
 */
@Document(collection = "customer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("full_name")
    private String fullName;

    @Field("customer_id")
    private Integer customerId;

    @Field("department")
    private String department;

    @Field("session_type")
    private Session sessionType;

    @Field("telephone")
    private String telephone;

    @Field("date")
    private Instant date;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Customer id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public Customer fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Customer customerId(Integer customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getDepartment() {
        return this.department;
    }

    public Customer department(String department) {
        this.setDepartment(department);
        return this;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Session getSessionType() {
        return this.sessionType;
    }

    public Customer sessionType(Session sessionType) {
        this.setSessionType(sessionType);
        return this;
    }

    public void setSessionType(Session sessionType) {
        this.sessionType = sessionType;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Customer telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Instant getDate() {
        return this.date;
    }

    public Customer date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return getId() != null && getId().equals(((Customer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", fullName='" + getFullName() + "'" +
            ", customerId=" + getCustomerId() +
            ", department='" + getDepartment() + "'" +
            ", sessionType='" + getSessionType() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
