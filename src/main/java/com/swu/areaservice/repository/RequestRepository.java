package com.swu.areaservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swu.areaservice.data.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

}
