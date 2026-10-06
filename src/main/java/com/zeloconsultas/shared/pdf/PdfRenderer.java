package com.zeloconsultas.shared.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.Map;

// Renderiza um template Thymeleaf em PDF: processa o HTML com os dados e
// entrega os bytes prontos. Centralizado aqui pra qualquer PDF futuro
// (atestado, consulta) reusar o mesmo caminho.
@Component
public class PdfRenderer {

    private final ITemplateEngine templateEngine;

    public PdfRenderer(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] render(String templateName, Map<String, Object> variaveis) {
        Context context = new Context();
        context.setVariables(variaveis);
        String html = templateEngine.process(templateName, context);

        try (ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            // baseUri vazio: o template nao referencia recursos externos (o PDF
            // roda offline, sem CDN); estilos vao inline no proprio HTML.
            builder.withHtmlContent(html, "");
            builder.toStream(saida);
            builder.run();
            return saida.toByteArray();
        } catch (Exception e) {
            throw new PdfGenerationException("Falha ao gerar o PDF do template " + templateName, e);
        }
    }
}
