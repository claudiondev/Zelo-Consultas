package com.zeloconsultas.modules.consultation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsultationPdfController {

    private final ConsultationPdfService consultationPdfService;

    public ConsultationPdfController(ConsultationPdfService consultationPdfService) {
        this.consultationPdfService = consultationPdfService;
    }

    @GetMapping("/consultas/{consultationId}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long consultationId) {
        byte[] pdf = consultationPdfService.gerarPdf(consultationId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=consulta-" + consultationId + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
