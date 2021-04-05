package ru.yakovlev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yakovlev.entities.Address;

/**
 * Address repository.
 *
 * @author Yakovlev Aleksandr (sanyakovlev@yandex.ru)
 * @since 0.1.0
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
}
