package com.phuquy.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.phuquy.entity.Form;
import com.phuquy.entity.FormDomain;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormDomainRepository extends JpaRepository<FormDomain, Long> {

    Form findById(int formID);

    List<FormDomain> findByForm_FormID(Long id);

}
