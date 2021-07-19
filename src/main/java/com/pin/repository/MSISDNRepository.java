package com.pin.repository;

import com.pin.model.MSISDN;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MSISDNRepository extends CrudRepository<MSISDN, Long> {

    Optional<MSISDN> findByPhoneNumber(String phoneNumber);

}
