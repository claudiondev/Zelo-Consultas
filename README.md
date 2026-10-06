# Zelo - Consultas

Prontuário psiquiátrico digital, **local e offline**, feito para o consultório de verdade: cadastro rápido, histórico único por paciente, atestado e receituário em PDF e backup automático. Roda inteiro no computador do médico, sem internet e sem servidor.

> Projeto real, já vendido para clientes. Este repositório é a versão pública, com identidade, clínica e médica **fictícias** (veja [Configuração](#configuração)).

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F) ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-UI-005F0F) ![H2](https://img.shields.io/badge/H2-banco%20em%20arquivo-blue)

---

## Por que existe

Antes do Zelo, as consultas ficavam num modelo de Word que se esgotava a cada ~20 atendimentos e fragmentava o histórico do paciente em vários arquivos. O Zelo troca isso por um sistema simples, pensado para quem **não é técnico** e usa um notebook modesto:

- interface com alvos de toque grandes (funciona em notebook 2 em 1);
- instalador de um clique para Windows, sem precisar instalar Java;
- nenhum dado sai do computador: sem nuvem, sem conta, sem internet.

## O que o sistema faz

- **Cadastro em formulário único**: dados do paciente, anamnese e primeira consulta juntos.
- **Consulta de retorno**: só a evolução, sem repetir a anamnese.
- **Detecção de duplicidade**: ao salvar, sugere pacientes parecidos por nome/CPF ("é o mesmo?"), sem forçar unicidade, já que nem todo paciente tem CPF.
- **Busca por nome ou CPF** e histórico completo de consultas do paciente.
- **Atestado em PDF** no modelo da clínica: texto e data editáveis, dias por extenso, CID incluído.
- **Receituário de controle especial em PDF**, em 2 vias.
- **Esquema terapêutico** em PDF.
- **Registro da consulta em PDF** para impressão.
- **Módulo de internos**: cadastro enxuto de pacientes internados com anotações datadas.
- **Backup automático** a cada consulta, com cópia extra para pendrive e lembrete quando faz tempo que não se copia.
- **Login com PIN** e servidor ligado só no próprio computador (`127.0.0.1`), nunca exposto na rede.

---

## Como rodar

Pré-requisitos: Java 17+ (o Maven vem embutido via `./mvnw`).

A senha de login **não fica em nenhum arquivo**: ela é obrigatória e vem de uma variável de ambiente. Sem ela o sistema se recusa a subir.

```bash
export PRONTUARIO_LOGIN_SENHA='escolha-uma-senha'
./mvnw spring-boot:run
```

Abra `http://localhost:8080` e entre com o usuário `ADMIN` e a senha definida.

Para conhecer o sistema com dados fictícios e um banco separado:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

Modo de desenvolvimento (liga o console do banco em `/h2-console`):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Testes:

```bash
./mvnw test
```

Gerar o instalador Windows (`.msi`): veja **[PACKAGING.md](PACKAGING.md)**.

---

## Configuração

Identidade e dados fixos ficam em `src/main/resources/application.yml`, no bloco `prontuario:`. Dá para ajustar sem mexer no código:

| Chave | O que controla |
| --- | --- |
| `prontuario.nome-app` | Nome exibido no título, cabeçalho e bandeja |
| `prontuario.login.usuario` | Usuário do login (a senha vem da variável `PRONTUARIO_LOGIN_SENHA`) |
| `prontuario.medica.*` | Nome, especialidade, CRM e RQE usados nos PDFs |
| `prontuario.clinica.*` | Timbre da clínica (nome, razão social, endereço, contato) |
| `prontuario.backup.*` | Pasta de backup e quantas cópias manter |

Os valores que acompanham o repositório são **fictícios** (Dra. Ana Ribeiro, Clínica Modelo). Para usar de verdade, troque pelos dados reais e **não publique esse arquivo preenchido**.

---

## Onde ficam os dados

- **Banco:** `data/prontuario.mv.db` (arquivo H2, no computador).
- **Backups automáticos:** pasta `backups/`, uma cópia a cada consulta salva, mantendo as ~30 mais recentes.
- **Cópias no pendrive:** `zelo-consultas-AAAA-MM-DD.zip` na raiz do pendrive.

Esses são dados de pacientes e **nunca vão para o repositório** (estão no `.gitignore`).

## Backup: as duas camadas

1. **Automática, no computador**, feita sozinha após cada consulta salva.
2. **No pendrive (manual)**, a única que sobrevive se o computador quebrar. O sistema avisa na tela inicial quando faz muito tempo que não se copia.

Para gravar no pendrive: plugue o pendrive, abra **Cópia de segurança**, clique em **Gravar cópia aqui**, espere a confirmação e guarde o pendrive separado do computador.

> A cópia é gravada primeiro como arquivo temporário e só vira a cópia final quando termina inteira. Se o pendrive for removido no meio, nunca sobrescreve uma cópia boa por uma pela metade.

## Restaurar os dados

Com o **sistema fechado**:

1. Abra o `.zip` da cópia desejada (de `backups/` ou do pendrive). Dentro há um arquivo `prontuario.mv.db`.
2. Copie esse arquivo para a pasta `data/`, substituindo o atual.
3. Abra o sistema de novo.

---

## Tecnologia

Java 17 · Spring Boot 3.5 · Spring Security · Spring Data JPA · Thymeleaf · H2 (banco em arquivo) · openhtmltopdf (PDF) · Lombok · Maven.

Arquitetura em camadas (Controller → Service → Repository → Entity), organizada por módulo:

```
com.zeloconsultas
├── config/          segurança, propriedades, launcher desktop
├── modules/
│   ├── patient/         cadastro e busca
│   ├── anamnesis/       anamnese (adulto, criança, adolescente)
│   ├── consultation/    consultas e PDF do registro
│   ├── certificate/     atestado
│   ├── prescription/    receituário de controle especial
│   ├── therapyschedule/ esquema terapêutico
│   └── inpatient/       internos e anotações
└── shared/          backup, PDF, validação de CPF, utilitários
```

## Documentação

- [PACKAGING.md](PACKAGING.md): como gerar o instalador Windows.
- [docs/proposta-zelo-consultas.html](docs/proposta-zelo-consultas.html): proposta e decisões de produto.
- [docs/superpowers/specs](docs/superpowers/specs): especificações dos módulos.

## Aviso de privacidade

Dados de saúde mental são dados sensíveis (LGPD). Este repositório **não contém nenhum dado de paciente**. Quem usar o sistema é responsável por proteger o computador, o banco e as cópias de segurança.

---

Desenvolvido por **Claudio Nascimento** · [github.com/claudiondev](https://github.com/claudiondev)
