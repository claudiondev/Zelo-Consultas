package com.zeloconsultas.modules.certificate.web;

// Carrega de uma vez o que a tela de atestado precisa, montado dentro da
// transacao do service — assim o controller nao toca associacao lazy
// (patient) fora da transacao (open-in-view esta desligado de proposito).
public record AtestadoContexto(Long patientId, String pacienteNome, CertificateForm form) {
}
