package com.zeloconsultas.modules.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    // Usado na tela de busca: nome parcial (case-insensitive) OU cpf exato.
    List<Patient> findByNomeContainingIgnoreCaseOrCpf(String nomeParcial, String cpf);

    // Usado quando a tela de pacientes abre sem termo de busca: lista todo mundo.
    List<Patient> findAllByOrderByNomeAsc();

    // Usado na deteccao de duplicidade ao salvar uma consulta nova.
    List<Patient> findByNomeContainingIgnoreCase(String nomeParcial);

    List<Patient> findByCpf(String cpf);
}
