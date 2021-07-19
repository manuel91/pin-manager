package com.pin.repository;

import com.pin.model.PIN;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PINRepository extends CrudRepository<PIN, Long> {
}
