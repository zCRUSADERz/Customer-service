package ru.yakovlev.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Address entity.
 *
 * @author Yakovlev Aleksandr (sanyakovlev@yandex.ru)
 * @since 0.1.0
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq_generator")
    @SequenceGenerator(name = "address_seq_generator", sequenceName = "address_id_seq")
    private Long id;

    @Column
    @Size(max = 255)
    @Setter
    private String country;

    @Column
    @Size(max = 255)
    @Setter
    private String region;

    @Column
    @Size(max = 255)
    @Setter
    private String city;

    @Column
    @Size(max = 255)
    @Setter
    private String street;

    @Column
    @Size(max = 255)
    @Setter
    private String house;

    @Column
    @Size(max = 255)
    @Setter
    private String flat;

    @Column(updatable = false)
    @CreatedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant created;

    @Column
    @LastModifiedDate
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant modified;
}
