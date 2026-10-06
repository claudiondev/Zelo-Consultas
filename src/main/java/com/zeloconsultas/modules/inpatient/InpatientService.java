package com.zeloconsultas.modules.inpatient;

import com.zeloconsultas.modules.inpatient.dto.InpatientNoteSummary;
import com.zeloconsultas.modules.inpatient.dto.InpatientSummary;
import com.zeloconsultas.modules.inpatient.web.InpatientNoteFormCommand;
import com.zeloconsultas.modules.inpatient.web.NovoInternoCommand;
import com.zeloconsultas.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Um unico service cobre Inpatient + InpatientNote — o modulo e enxuto de
// proposito (ver spec), nao precisa da divisao que Consultation/Patient tem.
@Service
@Transactional(readOnly = true)
public class InpatientService {

    private final InpatientRepository inpatientRepository;
    private final InpatientNoteRepository inpatientNoteRepository;

    public InpatientService(InpatientRepository inpatientRepository,
                             InpatientNoteRepository inpatientNoteRepository) {
        this.inpatientRepository = inpatientRepository;
        this.inpatientNoteRepository = inpatientNoteRepository;
    }

    public List<InpatientSummary> listarTodos() {
        return inpatientRepository.findAllByOrderByNomeAsc().stream()
                .map(InpatientSummary::from)
                .collect(Collectors.toList());
    }

    public Inpatient buscarPorId(Long id) {
        return inpatientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interno nao encontrado: id " + id));
    }

    public List<InpatientNoteSummary> notasDoInterno(Long inpatientId) {
        return inpatientNoteRepository.findByInpatientIdOrderByDataDescIdDesc(inpatientId).stream()
                .map(InpatientNoteSummary::from)
                .collect(Collectors.toList());
    }

    // Cadastra o interno e a primeira anotacao juntos — ou salva tudo, ou nao salva nada.
    @Transactional
    public Inpatient registrar(NovoInternoCommand comando) {
        Inpatient inpatient = Inpatient.builder()
                .nome(comando.getNome().trim())
                .dataNascimento(comando.getDataNascimento())
                .build();
        inpatientRepository.save(inpatient);

        InpatientNote nota = InpatientNote.builder()
                .inpatient(inpatient)
                .data(comando.getData())
                .anotacao(comando.getAnotacao().trim())
                .build();
        inpatientNoteRepository.save(nota);

        return inpatient;
    }

    @Transactional
    public void registrarNota(Long inpatientId, InpatientNoteFormCommand comando) {
        Inpatient inpatient = buscarPorId(inpatientId);

        InpatientNote nota = InpatientNote.builder()
                .inpatient(inpatient)
                .data(comando.getData())
                .anotacao(comando.getAnotacao().trim())
                .build();
        inpatientNoteRepository.save(nota);
    }
}
