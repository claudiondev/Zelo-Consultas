package com.zeloconsultas.modules.anamnesis;

import com.zeloconsultas.modules.anamnesis.dto.AnamnesisForm;
import com.zeloconsultas.modules.patient.Patient;
import com.zeloconsultas.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AnamnesisService {

    private final AnamnesisRepository anamnesisRepository;

    public AnamnesisService(AnamnesisRepository anamnesisRepository) {
        this.anamnesisRepository = anamnesisRepository;
    }

    public boolean jaTemAnamnese(Long patientId) {
        return anamnesisRepository.existsByPatientId(patientId);
    }

    public Anamnesis buscarPorPaciente(Long patientId) {
        return anamnesisRepository.findByPatientId(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Paciente id " + patientId + " ainda nao tem anamnese registrada"));
    }

    // So chamado no primeiro atendimento — ConsultationService decide se e
    // "primeira consulta" (com anamnese) ou "retorno" (so evolucao).
    @Transactional
    public Anamnesis registrar(Patient patient, AnamnesisForm form) {
        Anamnesis anamnesis = Anamnesis.builder()
                .patient(patient)
                .queixaPrincipal(form.queixaPrincipal().trim())
                .historiaDoencaAtual(form.historiaDoencaAtual().trim())
                .checklistSintomas(ChecklistSintomas.builder()
                        .humor(form.checklistSintomas().humor())
                        .culpa(form.checklistSintomas().culpa())
                        .suicidio(form.checklistSintomas().suicidio())
                        .insonia(form.checklistSintomas().insonia())
                        .pensamento(form.checklistSintomas().pensamento())
                        .agitacao(form.checklistSintomas().agitacao())
                        .ansiedadeTensao(form.checklistSintomas().ansiedadeTensao())
                        .tiques(form.checklistSintomas().tiques())
                        .toc(form.checklistSintomas().toc())
                        .dificuldadesIntelectuais(form.checklistSintomas().dificuldadesIntelectuais())
                        .sintomasDigestorios(form.checklistSintomas().sintomasDigestorios())
                        .sintomasMuscular(form.checklistSintomas().sintomasMuscular())
                        .sintomasGenitourinarios(form.checklistSintomas().sintomasGenitourinarios())
                        .hipocondria(form.checklistSintomas().hipocondria())
                        .criticaDaDoenca(form.checklistSintomas().criticaDaDoenca())
                        .incapacidadeDeSentir(form.checklistSintomas().incapacidadeDeSentir())
                        .build())
                .dadosClinicos(DadosClinicos.builder()
                        .fumante(form.dadosClinicos().fumante())
                        .etilista(form.dadosClinicos().etilista())
                        .usoSpa(form.dadosClinicos().usoSpa())
                        .alturaCm(form.dadosClinicos().alturaCm())
                        .pesoKg(form.dadosClinicos().pesoKg())
                        .sinalParticularidade(form.dadosClinicos().sinalParticularidade())
                        .observacao(form.dadosClinicos().observacao())
                        .cirurgias(form.dadosClinicos().cirurgias())
                        .tratamentoMedico(form.dadosClinicos().tratamentoMedico())
                        .antecedentesFamiliaresDoencaMental(form.dadosClinicos().antecedentesFamiliaresDoencaMental())
                        .antecedentesFamiliares(form.dadosClinicos().antecedentesFamiliares())
                        .dumTpm(form.dadosClinicos().dumTpm())
                        .anticoncepcional(form.dadosClinicos().anticoncepcional())
                        .build())
                .dadosCrianca(mapDadosCrianca(form))
                .dadosAdolescente(mapDadosAdolescente(form))
                .dadosAdulto(mapDadosAdulto(form))
                .passadoPsiquiatricoMorbido(blankToNull(form.passadoPsiquiatricoMorbido()))
                .historiaPsiquiatrica(mapHistoriaPsiquiatrica(form))
                .build();

        return anamnesisRepository.save(anamnesis);
    }

    private DadosCrianca mapDadosCrianca(AnamnesisForm form) {
        if (form.dadosCrianca() == null) {
            return null;
        }
        var f = form.dadosCrianca();
        return DadosCrianca.builder()
                .alimentacao(f.alimentacao())
                .marcha(f.marcha())
                .linguagem(f.linguagem())
                .sono(f.sono())
                .esfincteres(f.esfincteres())
                .tiques(f.tiques())
                .escolar(f.escolar())
                .social(f.social())
                .dpi(f.dpi())
                .hospitalizacao(f.hospitalizacao())
                .vacinas(f.vacinas())
                .usoCelular(f.usoCelular())
                .build();
    }

    private DadosAdolescente mapDadosAdolescente(AnamnesisForm form) {
        if (form.dadosAdolescente() == null) {
            return null;
        }
        var f = form.dadosAdolescente();
        return DadosAdolescente.builder()
                .socializacao(f.socializacao())
                .namoroSexo(f.namoroSexo())
                .drogas(f.drogas())
                .problemasLegais(f.problemasLegais())
                .observacao(f.observacao())
                .build();
    }

    private DadosAdulto mapDadosAdulto(AnamnesisForm form) {
        if (form.dadosAdulto() == null) {
            return null;
        }
        var f = form.dadosAdulto();
        return DadosAdulto.builder()
                .laboralForma(f.laboralForma())
                .laboralFuncao(f.laboralFuncao())
                .laboralAdequacao(f.laboralAdequacao())
                .socialAmizade(f.socialAmizade())
                .socialHabitos(f.socialHabitos())
                .socialLazer(f.socialLazer())
                .familiaConjuge(f.familiaConjuge())
                .familiaMoraCom(f.familiaMoraCom())
                .build();
    }

    private HistoriaPsiquiatrica mapHistoriaPsiquiatrica(AnamnesisForm form) {
        if (form.historiaPsiquiatrica() == null) {
            return null;
        }
        var f = form.historiaPsiquiatrica();
        return HistoriaPsiquiatrica.builder()
                .tratamentoMedicamentoso(f.tratamentoMedicamentoso())
                .tempo(f.tempo())
                .adesao(f.adesao())
                .medicoResponsavel(f.medicoResponsavel())
                .ect(f.ect())
                .hospitalizacao(f.hospitalizacao())
                .exames(f.exames())
                .build();
    }

    private String blankToNull(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
