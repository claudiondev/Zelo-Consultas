package com.zeloconsultas.modules.inpatient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InpatientRepository extends JpaRepository<Inpatient, Long> {

    // Lista da tela /internos, ordenada por nome.
    List<Inpatient> findAllByOrderByNomeAsc();
}
