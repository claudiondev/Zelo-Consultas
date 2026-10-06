package com.zeloconsultas.modules.anamnesis.web;

import com.zeloconsultas.modules.anamnesis.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

// Campos inicializados direto na declaracao (nao no construtor) pra que o
// formulario "em branco" (GET /consultas/nova) ja tenha os grupos aninhados
// instanciados — sem isso, th:field="*{dadosClinicos.fumante}" quebraria com
// NullPointerException antes mesmo do primeiro POST.
@Getter
@Setter
@NoArgsConstructor
public class AnamnesisFormCommand implements Serializable {

    @NotBlank(message = "Queixa principal e obrigatoria")
    private String queixaPrincipal;

    @NotBlank(message = "Historia da doenca atual e obrigatoria")
    private String historiaDoencaAtual;

    @Valid
    private ChecklistSintomasCommand checklistSintomas = new ChecklistSintomasCommand();

    @Valid
    private DadosClinicosCommand dadosClinicos = new DadosClinicosCommand();

    @Valid
    private DadosCriancaCommand dadosCrianca = new DadosCriancaCommand();

    @Valid
    private DadosAdolescenteCommand dadosAdolescente = new DadosAdolescenteCommand();

    @Valid
    private DadosAdultoCommand dadosAdulto = new DadosAdultoCommand();

    private String passadoPsiquiatricoMorbido;

    @Valid
    private HistoriaPsiquiatricaCommand historiaPsiquiatrica = new HistoriaPsiquiatricaCommand();

    public AnamnesisForm toForm() {
        return new AnamnesisForm(
                queixaPrincipal,
                historiaDoencaAtual,
                checklistSintomas.toForm(),
                dadosClinicos.toForm(),
                dadosCrianca.toForm(),
                dadosAdolescente.toForm(),
                dadosAdulto.toForm(),
                passadoPsiquiatricoMorbido,
                historiaPsiquiatrica.toForm()
        );
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ChecklistSintomasCommand implements Serializable {
        private Boolean humor;
        private Boolean culpa;
        private Boolean suicidio;
        private Boolean insonia;
        private Boolean pensamento;
        private Boolean agitacao;
        private Boolean ansiedadeTensao;
        private Boolean tiques;
        private Boolean toc;
        private Boolean dificuldadesIntelectuais;
        private Boolean sintomasDigestorios;
        private Boolean sintomasMuscular;
        private Boolean sintomasGenitourinarios;
        private Boolean hipocondria;
        private Boolean criticaDaDoenca;
        private Boolean incapacidadeDeSentir;

        public ChecklistSintomasForm toForm() {
            return new ChecklistSintomasForm(humor, culpa, suicidio, insonia, pensamento, agitacao,
                    ansiedadeTensao, tiques, toc, dificuldadesIntelectuais, sintomasDigestorios,
                    sintomasMuscular, sintomasGenitourinarios, hipocondria, criticaDaDoenca, incapacidadeDeSentir);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DadosClinicosCommand implements Serializable {
        private Boolean fumante;
        private Boolean etilista;
        private Boolean usoSpa;
        private Integer alturaCm;
        private Double pesoKg;
        private String sinalParticularidade;
        private String observacao;
        private String cirurgias;
        private String tratamentoMedico;
        private String antecedentesFamiliaresDoencaMental;
        private String antecedentesFamiliares;
        private String dumTpm;
        private String anticoncepcional;

        public DadosClinicosForm toForm() {
            return new DadosClinicosForm(fumante, etilista, usoSpa, alturaCm, pesoKg, sinalParticularidade,
                    observacao, cirurgias, tratamentoMedico, antecedentesFamiliaresDoencaMental,
                    antecedentesFamiliares, dumTpm, anticoncepcional);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DadosCriancaCommand implements Serializable {
        private String alimentacao;
        private String marcha;
        private String linguagem;
        private String sono;
        private String esfincteres;
        private String tiques;
        private String escolar;
        private String social;
        private String dpi;
        private String hospitalizacao;
        private String vacinas;
        private String usoCelular;

        public DadosCriancaForm toForm() {
            return new DadosCriancaForm(alimentacao, marcha, linguagem, sono, esfincteres, tiques,
                    escolar, social, dpi, hospitalizacao, vacinas, usoCelular);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DadosAdolescenteCommand implements Serializable {
        private String socializacao;
        private String namoroSexo;
        private String drogas;
        private String problemasLegais;
        private String observacao;

        public DadosAdolescenteForm toForm() {
            return new DadosAdolescenteForm(socializacao, namoroSexo, drogas, problemasLegais, observacao);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DadosAdultoCommand implements Serializable {
        private String laboralForma;
        private String laboralFuncao;
        private String laboralAdequacao;
        private String socialAmizade;
        private String socialHabitos;
        private String socialLazer;
        private String familiaConjuge;
        private String familiaMoraCom;

        public DadosAdultoForm toForm() {
            return new DadosAdultoForm(laboralForma, laboralFuncao, laboralAdequacao, socialAmizade,
                    socialHabitos, socialLazer, familiaConjuge, familiaMoraCom);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HistoriaPsiquiatricaCommand implements Serializable {
        private String tratamentoMedicamentoso;
        private String tempo;
        private String adesao;
        private String medicoResponsavel;
        private Boolean ect;
        private String hospitalizacao;
        private String exames;

        public HistoriaPsiquiatricaForm toForm() {
            return new HistoriaPsiquiatricaForm(tratamentoMedicamentoso, tempo, adesao,
                    medicoResponsavel, ect, hospitalizacao, exames);
        }
    }
}
