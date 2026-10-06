package com.zeloconsultas.modules.consultation;

import com.zeloconsultas.config.ProntuarioProperties;
import com.zeloconsultas.modules.patient.Patient;
import com.zeloconsultas.shared.exception.ResourceNotFoundException;
import com.zeloconsultas.shared.pdf.PdfRenderer;
import com.zeloconsultas.shared.validation.CpfUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

// Gera o PDF do registro de uma consulta. Tudo dentro da transacao pra poder
// acessar a associacao lazy patient (open-in-view esta desligado).
@Service
@Transactional(readOnly = true)
public class ConsultationPdfService {

    private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ConsultationRepository consultationRepository;
    private final PdfRenderer pdfRenderer;
    private final ProntuarioProperties properties;

    public ConsultationPdfService(ConsultationRepository consultationRepository,
                                   PdfRenderer pdfRenderer,
                                   ProntuarioProperties properties) {
        this.consultationRepository = consultationRepository;
        this.pdfRenderer = pdfRenderer;
        this.properties = properties;
    }

    public byte[] gerarPdf(Long consultationId) {
        Consultation consulta = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta nao encontrada: id " + consultationId));
        Patient paciente = consulta.getPatient();

        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("clinica", properties.getClinica());
        variaveis.put("medica", properties.getMedica());
        variaveis.put("consulta", consulta);
        variaveis.put("paciente", pacienteView(paciente));
        variaveis.put("dataConsulta", consulta.getData().format(DATA_BR));
        variaveis.put("dataNascimento", paciente.getDataNascimento() != null
                ? paciente.getDataNascimento().format(DATA_BR) : "—");

        return pdfRenderer.render("pdf/consulta", variaveis);
    }

    // Mapa simples com os campos do paciente ja formatados pra exibicao no PDF
    // (CPF com mascara), sem expor a entidade JPA inteira no template.
    private Map<String, Object> pacienteView(Patient paciente) {
        Map<String, Object> view = new HashMap<>();
        view.put("nome", paciente.getNome());
        view.put("cpf", paciente.getCpf() != null ? CpfUtils.formatar(paciente.getCpf()) : null);
        return view;
    }
}
