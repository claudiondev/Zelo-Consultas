package com.zeloconsultas.modules.therapyschedule;

import com.zeloconsultas.config.ProntuarioProperties;
import com.zeloconsultas.modules.therapyschedule.web.EsquemaTerapeuticoForm;
import com.zeloconsultas.modules.therapyschedule.web.LinhaMedicamentoForm;
import com.zeloconsultas.shared.pdf.PdfRenderer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Esquema terapeutico (modelo real da Clinica Modelo): tabela com uma linha
// por medicamento e a dose em cada horario (manha/tarde/noite). Documento
// separado do receituario — nao tem CPF/endereco, so nome e data. Igual ao
// receituario/atestado: nao e entidade, e uma acao de gerar PDF, reusada
// tanto em Consultas (Patient) quanto em Internos (Inpatient).
@Service
public class TherapyScheduleService {

    private static final int LINHAS_INICIAIS = 8;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PdfRenderer pdfRenderer;
    private final ProntuarioProperties properties;

    public TherapyScheduleService(PdfRenderer pdfRenderer, ProntuarioProperties properties) {
        this.pdfRenderer = pdfRenderer;
        this.properties = properties;
    }

    public EsquemaTerapeuticoForm novoFormulario() {
        EsquemaTerapeuticoForm form = new EsquemaTerapeuticoForm();
        form.setData(LocalDate.now());
        form.setLinhas(Stream.generate(LinhaMedicamentoForm::new)
                .limit(LINHAS_INICIAIS)
                .collect(Collectors.toList()));
        return form;
    }

    // true se nenhuma linha tem nome de medicamento preenchido — controller usa
    // isso pra rejeitar o formulario antes de gerar um PDF vazio.
    public boolean semNenhumMedicamento(EsquemaTerapeuticoForm form) {
        return linhasPreenchidas(form).isEmpty();
    }

    public byte[] gerarPdf(String pacienteNome, EsquemaTerapeuticoForm form) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("clinica", properties.getClinica());
        variaveis.put("pacienteNome", pacienteNome);
        variaveis.put("linhas", linhasPreenchidas(form));
        variaveis.put("dataFormatada", form.getData().format(FORMATO_DATA));
        return pdfRenderer.render("pdf/esquema-terapeutico", variaveis);
    }

    private List<LinhaMedicamentoForm> linhasPreenchidas(EsquemaTerapeuticoForm form) {
        return form.getLinhas().stream()
                .filter(linha -> linha.getNomeMedicamento() != null && !linha.getNomeMedicamento().isBlank())
                .collect(Collectors.toList());
    }
}
