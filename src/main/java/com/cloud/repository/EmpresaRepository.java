package com.cloud.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cloud.entities.Empresa;

@Repository
@Transactional
public interface EmpresaRepository extends CrudRepository<Empresa, Integer>{

}
