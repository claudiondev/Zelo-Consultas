package com.zeloconsultas.modules.inpatient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InpatientNoteRepository extends JpaRepository<InpatientNote, Long> {

    // Historico de anotacoes do interno, mais recente primeiro. Desempate por
    // id quando duas anotacoes sao do mesmo dia (a ultima criada aparece primeiro).
    List<InpatientNote> findByInpatientIdOrderByDataDescIdDesc(Long inpatientId);
}
