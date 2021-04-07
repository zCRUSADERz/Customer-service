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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yakovlev.entities.Address;
import ru.yakovlev.repositories.AddressRepository;
import utils.IntegrationTestUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Address integration tests")
class AddressIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    @DisplayName("The address can be created through a post request for a collection of addresses")
    void whenCreateAddressByPostRequestThenAddressCreated(@Autowired MockMvc mvc) throws Exception {
        val country = "Россия";
        val region = "Новосибирская обл.";
        val city = "Новосибирск";
        val street = "Фрунзе";
        val house = "202";
        val flat = "45";
        val address = new Address(null, country, region, city, street, house, flat, null, null);
        val result = mvc.perform(post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address))
        ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        Long id = IntegrationTestUtils.parseIdFromLocationHeader(result.getResponse());
        mvc.perform(get("/addresses/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value(country))
                .andExpect(jsonPath("$.region").value(region))
                .andExpect(jsonPath("$.city").value(city))
                .andExpect(jsonPath("$.street").value(street))
                .andExpect(jsonPath("$.house").value(house))
                .andExpect(jsonPath("$.flat").value(flat))
                .andExpect(jsonPath("$.created").exists())
                .andExpect(jsonPath("$.modified").exists());
    }

    @Test
    @DisplayName("The address can be changed by a PUT request")
    void whenPutAddressThenGetReturnUpdatedAddress(@Autowired MockMvc mvc) throws Exception {
        val address = new Address();
        val result = mvc.perform(post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address))
        ).andReturn();
        val newCountry = "Россия";
        val newRegion = "Московская";
        val newCity = "Москва";
        val newStreet = "Пролетарская";
        val newHouse = "123";
        val newFlat = "1";
        address.setCountry(newCountry);
        address.setRegion(newRegion);
        address.setCity(newCity);
        address.setStreet(newStreet);
        address.setHouse(newHouse);
        address.setFlat(newFlat);
        Long id = IntegrationTestUtils.parseIdFromLocationHeader(result.getResponse());
        mvc.perform(put("/addresses/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address)));
        mvc.perform(get("/addresses/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value(newCountry))
                .andExpect(jsonPath("$.region").value(newRegion))
                .andExpect(jsonPath("$.city").value(newCity))
                .andExpect(jsonPath("$.street").value(newStreet))
                .andExpect(jsonPath("$.house").value(newHouse))
                .andExpect(jsonPath("$.flat").value(newFlat));
    }

    @Test
    @DisplayName("The address can be changed by a PATCH request")
    void whenPatchAddressThenGetReturnUpdatedAddress(@Autowired MockMvc mvc) throws Exception {
        val address = new Address();
        val oldCountry = "Бельгия";
        address.setCountry(oldCountry);
        val result = mvc.perform(post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(address))
        ).andReturn();
        Long id = IntegrationTestUtils.parseIdFromLocationHeader(result.getResponse());
        val newCountry = "Россия";
        mvc.perform(patch("/addresses/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"country\": \"%s\"}", newCountry)));
        mvc.perform(get("/addresses/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value(newCountry));
    }

    @Test
    @DisplayName("The address can be deleted")
    void whenDeleteAddressThenGetReturnNotFound(@Autowired MockMvc mvc) throws Exception {
        val address = new Address();
        val result = mvc.perform(post("/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(address))
        ).andReturn();
        Long id = IntegrationTestUtils.parseIdFromLocationHeader(result.getResponse());
        mvc.perform(delete("/addresses/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/addresses/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("When creating an address, the created property will be set")
    void whenCreateNewAddressThenCreatedIsSet() {
        val address = new Address();
        val saved = this.addressRepository.save(address);
        assertThat(saved.getCreated()).isNotNull();
    }

    @Test
    @DisplayName("When creating an address, the modified property will be set")
    void whenCreateNewAddressThenModifiedIsSet() {
        val address = new Address();
        val saved = this.addressRepository.save(address);
        assertThat(saved.getModified()).isNotNull();
    }

    @Test
    @DisplayName("After changing the address, the property modified must be updated with the new value")
    void whenAddressUpdatedThenModifiedMustBeUpdated() {
        val address = new Address();
        val saved = this.addressRepository.save(address);
        saved.setCity("Киев");
        val modifiedAfterSave = saved.getModified();
        val modifiedAddress = this.addressRepository.save(saved);
        assertThat(modifiedAddress.getModified()).isAfter(modifiedAfterSave);
    }

    @Test
    @DisplayName("After changing the address, the property created should not be updated with the new value")
    void whenAddressUpdatedThenCreatedShouldNotBeUpdated() {
        val address = new Address();
        val saved = this.addressRepository.save(address);
        saved.setCity("Минск");
        val createdAfterSave = saved.getCreated();
        val modifiedAddress = this.addressRepository.save(saved);
        assertThat(modifiedAddress.getCreated()).isEqualTo(createdAfterSave);
    }

}
