package com.pin.repository;

import com.pin.model.PIN;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PINRepository extends CrudRepository<PIN, Long> {

    List<PIN> findByCreationDateTimeGreaterThanEqual(LocalDateTime currentDateTime);

}
