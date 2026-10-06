package com.zeloconsultas.modules.consultation;

// Publicado quando uma consulta e persistida. O backup escuta este evento
// APOS o commit (nao dentro da transacao) pra copiar dados ja gravados.
public record ConsultationSavedEvent(Long consultationId) {
}
