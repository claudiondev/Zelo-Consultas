package com.zeloconsultas.modules.consultation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    // Historico do paciente, mais recente primeiro.
    List<Consultation> findByPatientIdOrderByDataDesc(Long patientId);

    // Ultimos atendimentos de qualquer paciente, pra home.
    List<Consultation> findTop5ByOrderByDataDescIdDesc();

    // Ultima consulta do paciente, pra sugerir CID/tratamento no retorno.
    Optional<Consultation> findFirstByPatientIdOrderByDataDesc(Long patientId);
}
