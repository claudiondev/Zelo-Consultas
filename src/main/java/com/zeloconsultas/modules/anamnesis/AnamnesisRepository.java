package com.zeloconsultas.modules.anamnesis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnamnesisRepository extends JpaRepository<Anamnesis, Long> {

    Optional<Anamnesis> findByPatientId(Long patientId);

    boolean existsByPatientId(Long patientId);
}
