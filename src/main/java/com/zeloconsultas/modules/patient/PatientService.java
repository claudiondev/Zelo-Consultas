package com.zeloconsultas.modules.patient;

import com.zeloconsultas.modules.patient.dto.PatientForm;
import com.zeloconsultas.modules.patient.dto.PatientSummary;
import com.zeloconsultas.shared.exception.ResourceNotFoundException;
import com.zeloconsultas.shared.validation.CpfUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Decisao de produto (ver plano): CPF nunca bloqueia nem faz upsert automatico.
    // Isso so devolve candidatos para a médica confirmar manualmente na tela
    // "ja existe um paciente parecido, e o mesmo?".
    public List<PatientSummary> buscarCandidatosDuplicidade(String nome, String cpf) {
        String cpfNormalizado = CpfUtils.normalizar(cpf);

        Stream<Patient> porNome = patientRepository.findByNomeContainingIgnoreCase(nome.trim()).stream();
        Stream<Patient> porCpf = cpfNormalizado == null
                ? Stream.empty()
                : patientRepository.findByCpf(cpfNormalizado).stream();

        return Stream.concat(porNome, porCpf)
                .distinct()
                .sorted(Comparator.comparing(Patient::getNome))
                .map(PatientSummary::from)
                .collect(Collectors.toList());
    }

    // Lista de pacientes sem filtro nenhum — abertura padrao da tela de pacientes.
    public List<PatientSummary> listarTodos() {
        return patientRepository.findAllByOrderByNomeAsc().stream()
                .map(PatientSummary::from)
                .collect(Collectors.toList());
    }

    public long total() {
        return patientRepository.count();
    }

    public List<PatientSummary> buscar(String termo) {
        String termoBusca = termo == null ? "" : termo.trim();
        String cpfNormalizado = CpfUtils.normalizar(termoBusca);
        // Se o campo cpf vier nulo pro findBy...OrCpf, o Spring Data monta "cpf IS NULL"
        // e casaria com todo paciente sem CPF cadastrado — por isso o fallback pra "".
        String cpfBusca = cpfNormalizado == null ? "" : cpfNormalizado;

        return patientRepository.findByNomeContainingIgnoreCaseOrCpf(termoBusca, cpfBusca).stream()
                .sorted(Comparator.comparing(Patient::getNome))
                .map(PatientSummary::from)
                .collect(Collectors.toList());
    }

    public Patient buscarPorId(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente nao encontrado: id " + id));
    }

    @Transactional
    public Patient registrar(PatientForm form) {
        Patient patient = Patient.builder()
                .nome(form.nome().trim())
                .cpf(CpfUtils.normalizar(form.cpf()))
                .dataNascimento(form.dataNascimento())
                .sexo(form.sexo())
                .planoSaude(form.planoSaude())
                .endereco(form.endereco())
                .localAtendimento(form.localAtendimento())
                .telefone(form.telefone())
                .religiao(form.religiao())
                .profissao(form.profissao())
                .acompanhante(form.acompanhante())
                .valorConsultaPadrao(form.valorConsultaPadrao())
                .reciboNf(form.reciboNf())
                .build();

        return patientRepository.save(patient);
    }
}
