package com.zeloconsultas.modules.prescription;

import com.zeloconsultas.config.ProntuarioProperties;
import com.zeloconsultas.modules.prescription.web.PrescriptionForm;
import com.zeloconsultas.shared.pdf.PdfRenderer;
import com.zeloconsultas.shared.util.DataPorExtenso;
import com.zeloconsultas.shared.validation.CpfUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// Receituario de controle especial (Portaria SVS/MS 344/98), NAO e a receita
// digital certificada do CFM — e so um formulario simples (ver
// referencia/estrutura-receituario.md pra decisao completa). Nao e entidade:
// e uma acao de gerar PDF. Reusado tanto pela ficha de Consultas (Patient)
// quanto pela ficha de Internos (Inpatient) — essas duas entidades nao tem
// um tipo em comum, entao o service recebe so o nome ja resolvido; CPF e
// endereco vem do form (ver PrescriptionForm — sao obrigatorios so aqui,
// digitados na hora quando o cadastro nao tem).
@Service
public class PrescriptionService {

    private final PdfRenderer pdfRenderer;
    private final ProntuarioProperties properties;

    public PrescriptionService(PdfRenderer pdfRenderer, ProntuarioProperties properties) {
        this.pdfRenderer = pdfRenderer;
        this.properties = properties;
    }

    public PrescriptionForm novoFormulario() {
        PrescriptionForm form = new PrescriptionForm();
        form.setData(LocalDate.now());
        return form;
    }

    public byte[] gerarPdf(String pacienteNome, PrescriptionForm form) {
        Map<String, Object> variaveis = new HashMap<>();
        variaveis.put("clinica", properties.getClinica());
        variaveis.put("medica", properties.getMedica());
        variaveis.put("cidade", properties.getClinica().getCidade());
        variaveis.put("pacienteNome", pacienteNome);
        variaveis.put("pacienteCpf", CpfUtils.formatar(form.getCpf()));
        variaveis.put("pacienteEndereco", form.getEndereco());
        variaveis.put("viaUso", form.getViaUso());
        variaveis.put("prescricao", form.getPrescricao());
        variaveis.put("dataExtenso", DataPorExtenso.formatar(form.getData()));
        return pdfRenderer.render("pdf/receituario", variaveis);
    }
}
