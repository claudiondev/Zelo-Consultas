package com.zeloconsultas.modules.patient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// equals/hashCode so por id: com todos os campos (padrao do Lombok) uma entidade
// gerenciada pelo Hibernate pode "mudar de identidade" a cada setter, quebrando
// Sets/Maps que a contenham.
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Guardado so em digitos (normalizado por CpfUtils.normalizar antes de chegar aqui).
    // Nao e chave de negocio nem tem constraint de unicidade — ver PatientService.
    @Column(length = 11)
    private String cpf;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false, length = 1)
    private Character sexo;

    @Column(name = "plano_saude")
    private String planoSaude;

    private String endereco;

    @Column(name = "local_atendimento")
    private String localAtendimento;

    @Column(nullable = false)
    private String telefone;

    private String religiao;

    private String profissao;

    private String acompanhante;

    @Column(name = "valor_consulta_padrao", precision = 10, scale = 2)
    private BigDecimal valorConsultaPadrao;

    @Column(name = "recibo_nf")
    private String reciboNf;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @PrePersist
    void aoCriar() {
        criadoEm = Instant.now();
    }
}
