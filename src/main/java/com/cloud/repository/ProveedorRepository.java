package com.cloud.repository;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cloud.entities.Proveedor;

@Repository
@Transactional
public interface ProveedorRepository extends CrudRepository<Proveedor, Integer>{

}
