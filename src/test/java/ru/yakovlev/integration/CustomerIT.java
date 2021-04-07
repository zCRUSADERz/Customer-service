/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Yakovlev Aleksandr
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.yakovlev.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yakovlev.entities.Address;
import ru.yakovlev.entities.Customer;
import ru.yakovlev.entities.embedded.Sex;
import ru.yakovlev.repositories.AddressRepository;
import ru.yakovlev.repositories.CustomerRepository;
import utils.IntegrationTestUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Customer integration tests")
class CustomerIT {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("search by first and last name must return customers with the specified first and last name")
    void whenFindByFirstAndLastNameThenReturnCustomersWithSpecifiedFirstAndLastNames() {
        var russia = new Address();
        russia.setCountry("Россия");
        russia = this.addressRepository.save(russia);
        var ukraine = new Address();
        ukraine.setCountry("Украина");
        ukraine = this.addressRepository.save(ukraine);
        var france = new Address();
        france.setCountry("Франция");
        france = this.addressRepository.save(france);
        val firstName = "Иван";
        val lastname = "Крылов";
        this.customerRepository.save(
                new Customer(null, russia, ukraine, "Сергей", "Смирнов", "Генадьевич", Sex.MALE));
        this.customerRepository.save(
                new Customer(null, ukraine, france, firstName, "Попов", "Константинович", Sex.MALE));
        this.customerRepository.save(
                new Customer(null, france, russia, "Виктор", lastname, "Максимович", Sex.MALE));
        this.customerRepository.save(
                new Customer(null, russia, ukraine, firstName, lastname, "Алексеевич", Sex.MALE));
        val result = this.customerRepository.findByFirstNameAndLastName(firstName, lastname, PageRequest.of(0, 100));
        val allMatch = result.getContent().stream().allMatch(
                (customer -> firstName.equals(customer.getFirstName()) && lastname.equals(customer.getLastName())));
        Assertions.assertThat(allMatch).isTrue();
    }

    @Test
    @DisplayName("The customer can be created through a post request for a collection of customers")
    void whenCreateCustomerByPostRequestThenCustomerCreated(@Autowired MockMvc mvc) throws Exception {
        val registeredAddress = this.createAddress(mvc);
        val actualAddress = this.createAddress(mvc);
        val firstName = "Андрей";
        val lastName = "Соболев";
        val middleName = "Максимович";
        val sex = "MALE";
        val postResult = mvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"registeredAddress\": \"%s\", \"actualAddress\": \"%s\", "
                        + "\"firstName\": \"%s\", \"lastName\": \"%s\", \"middleName\": \"%s\", \"sex\": \"%s\"}",
                        registeredAddress, actualAddress, firstName, lastName, middleName, sex))
        ).andReturn();
        Long id = IntegrationTestUtils.parseIdFromLocationHeader(postResult.getResponse());
        mvc.perform(get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.middleName").value(middleName))
                .andExpect(jsonPath("$.sex").value(sex));
        mvc.perform(get("/customers/{id}/registeredAddress", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(registeredAddress));
        mvc.perform(get("/customers/{id}/actualAddress", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(actualAddress));
    }

    @Test
    @DisplayName("The client's actual address can be changed")
    void whenPutNewActualAddressThenGetCustomerActualAddressReturnNewAddress(@Autowired MockMvc mvc) throws Exception {
        val addressLocation = this.createAddress(mvc);
        val newAddressLocation = this.createAddress(mvc);
        val postResult = mvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(
                        "{\"registeredAddress\": \"%s\", \"actualAddress\": \"%s\", \"sex\": \"FEMALE\"}",
                        addressLocation, addressLocation))
        ).andReturn();
        Long id = IntegrationTestUtils.parseIdFromLocationHeader(postResult.getResponse());
        mvc.perform(put("/customers/{id}/actualAddress", id)
            .contentType(RestMediaTypes.TEXT_URI_LIST_VALUE)
            .content(newAddressLocation));
        mvc.perform(get("/customers/{id}/actualAddress", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(newAddressLocation));
    }

    private String createAddress(MockMvc mvc) throws Exception {
        val address = new Address();
        return mvc.perform(post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(address))
        ).andReturn().getResponse().getHeader("Location");
    }
}
