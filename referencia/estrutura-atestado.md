# Estrutura do Atestado Médico — modelo

Transcrição da estrutura do atestado a partir do modelo real (foto em `atestado-modelo-real.jpg`, gitignored). **Este documento contém só o layout e o texto-modelo, com os dados do paciente substituídos por marcadores** — a foto original tem nome e CPF de uma paciente de verdade e não deve ser commitada/compartilhada.

O atestado é gerado no papel timbrado da **Clínica Modelo** (onde a médica atende).

## Cabeçalho (fixo, timbre da clínica)
```
CLÍNICA MODELO
CLÍNICA MODELO DEMONSTRATIVA LTDA.
RUA EXEMPLO, 123 – CENTRO - CIDADE - UF
CEP 00000-000 - FONE (00) 0000-0000 - E-MAIL: contato@exemplo.com
```

## Título
`ATESTADO MÉDICO` (centralizado, sublinhado)

## Corpo (texto-modelo)
> Atesto para os devidos fins que {a Sra.|o Sr.} **{NOME}** de CPF: **{CPF}**, {é portadora|é portador} de patologias **{CID}**. Não apresenta condições de exercer as suas atividades laborativas e necessita de afastamento por um período de **{DIAS}** (**{DIAS por extenso}**) dias a contar da presente data.

Partes variáveis:
- **Saudação** (`a Sra.` / `o Sr.`) e **flexão** (`portadora` / `portador`) — derivadas do `sexo` do paciente.
- **NOME** e **CPF** — do cadastro do paciente (CPF formatado `000.000.000-00`).
- **CID** — da consulta (`Consultation.cid`); no modelo real aparecem dois códigos somados (ex.: `F32 + F41.8`). **Confirmado: sempre incluir o CID no atestado.**
- **DIAS** — de `Consultation.atestadoDias`, escrito como número **e** por extenso: `60 (sessenta)`.

**Confirmado com Claudio (2026-07-08):** o texto do corpo **e a data** são **editáveis** antes de gerar o PDF — o sistema pré-preenche com o texto-modelo acima (tudo já interpolado) e a data de hoje, e a médica ajusta se precisar.

## Local + data
`Cidade-UF, {DATA por extenso}.`  (ex.: `Cidade-UF, 18 de Junho de 2024.`)
Data pré-preenchida com hoje, editável.

## Assinatura (fixa)
```
_______________________________
Ana Ribeiro
Médica
CRM-UF: 0000
```

## Implicação de implementação
- Não é entidade — é uma ação de gerar PDF a partir de uma `Consultation` + dados do `Patient` (confirma a modelagem do plano).
- Fluxo: histórico do paciente → consulta com atestado → tela de edição (texto + data pré-preenchidos) → gerar PDF.
- Dados fixos (clínica, médica, cidade) ficam em configuração (`application.yml`), não hardcoded em Java, pra ela poder ajustar sem recompilar.
