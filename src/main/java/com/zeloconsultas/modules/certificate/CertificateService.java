package com.zeloconsultas.modules.certificate;

import com.zeloconsultas.config.ProntuarioProperties;
import com.zeloconsultas.modules.certificate.web.AtestadoContexto;
import com.zeloconsultas.modules.certificate.web.CertificateForm;
import com.zeloconsultas.modules.consultation.Consultation;
import com.zeloconsultas.modules.consultation.ConsultationRepository;
import com.zeloconsultas.modules.patient.Patient;
import com.zeloconsultas.modules.patient.PatientService;
import com.zeloconsultas.shared.exception.ResourceNotFoundException;
import com.zeloconsultas.shared.pdf.PdfRenderer;
import com.zeloconsultas.shared.util.DataPorExtenso;
import com.zeloconsultas.shared.util.NumeroPorExtenso;
import com.zeloconsultas.shared.validation.CpfUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// Atestado nao e entidade — e uma acao de gerar PDF a partir de uma consulta
// (ver plano). Aqui ficam as duas responsabilidades: pre-preencher o texto e
// renderizar o PDF final com o texto/data que a medica confirmou.
@Service
@Transactional(readOnly = true)
public class CertificateService {

    // Espaco reservado quando um dado esperado nao esta preenchido, pra ela
    // completar na hora em vez de sair um atestado com "null".
    private static final String LACUNA = "___";

    private final ConsultationRepository consultationRepository;
    private final PatientService patientService;
    private final PdfRenderer pdfRenderer;
    private final ProntuarioProperties properties;

    public CertificateService(ConsultationRepository consultationRepository,
                               PatientService patientService,
                               PdfRenderer pdfRenderer,
                               ProntuarioProperties properties) {
        this.consultationRepository = consultationRepository;
        this.patientService = patientService;
        this.pdfRenderer = pdfRenderer;
        this.properties = properties;
    }

    // Monta, dentro da transacao, o nome do paciente + o formulario ja
    // preenchido (texto-modelo com nome/CPF/CID/dias interpolados e data de
    // hoje). Tudo que acessa a associacao lazy patient fica aqui dentro.
    public AtestadoContexto prepararContexto(Long consultationId) {
        Consultation consulta = buscarConsulta(consultationId);
        CertificateForm form = new CertificateForm();
        form.setTexto(montarTextoPadrao(consulta));
        form.setData(LocalDate.now());
        return new AtestadoContexto(consulta.getPatient().getId(), consulta.getPatient().getNome(), form);
    }

    // Atestado avulso: sem consulta por tras, direto do historico do paciente.
    // CID e dias ficam como lacuna no texto padrao pra ela preencher na hora.
    public AtestadoContexto prepararContextoAvulso(Long patientId) {
        Patient paciente = patientService.buscarPorId(patientId);
        CertificateForm form = new CertificateForm();
        form.setTexto(montarTextoPadrao(paciente, null, null));
        form.setData(LocalDate.now());
        return new AtestadoContexto(paciente.getId(), paciente.getNome(), form);
    }

    public String buscarNomePaciente(Long consultationId) {
        return buscarConsulta(consultationId).getPatient().getNome();
    }

    public Long buscarPatientId(Long consultationId) {
        return buscarConsulta(consultationId).getPatient().getId();
    }

    private Consultation buscarConsulta(Long consultationId) {
        return consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta nao encontrada: id " + consultationId));
    }

    public byte[] gerarPdf(CertificateForm form) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("clinica", properties.getClinica());
        variaveis.put("medica", properties.getMedica());
        variaveis.put("cidade", properties.getClinica().getCidade());
        variaveis.put("texto", form.getTexto());
        variaveis.put("dataExtenso", DataPorExtenso.formatar(form.getData()));
        return pdfRenderer.render("pdf/atestado", variaveis);
    }

    private String montarTextoPadrao(Consultation consulta) {
        return montarTextoPadrao(consulta.getPatient(), consulta.getCid(), consulta.getAtestadoDias());
    }

    // Compartilhado entre o atestado ligado a uma consulta e o avulso (sem
    // consulta) — nesse ultimo, cid/atestadoDias vem nulos e caem na lacuna,
    // pra medica preencher direto no texto editavel.
    private String montarTextoPadrao(Patient paciente, String cid, Integer atestadoDias) {
        boolean feminino = isFeminino(paciente.getSexo());

        String saudacao = feminino ? "a Sra." : "o Sr.";
        String flexao = feminino ? "é portadora" : "é portador";
        String cpf = paciente.getCpf() != null ? CpfUtils.formatar(paciente.getCpf()) : LACUNA;
        String cidTexto = (cid != null && !cid.isBlank()) ? cid : LACUNA;
        String dias = atestadoDias != null
                ? atestadoDias + " (" + NumeroPorExtenso.escrever(atestadoDias) + ")"
                : LACUNA;

        return "Atesto para os devidos fins que " + saudacao + " " + paciente.getNome().toUpperCase()
                + " de CPF: " + cpf + ", " + flexao + " de patologias " + cidTexto
                + ". Não apresenta condições de exercer as suas atividades laborativas e necessita de"
                + " afastamento por um período de " + dias + " dias a contar da presente data.";
    }

    private boolean isFeminino(Character sexo) {
        return sexo != null && Character.toUpperCase(sexo) == 'F';
    }
}
