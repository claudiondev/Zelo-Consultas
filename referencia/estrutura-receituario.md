# Estrutura do Receituário de Controle Especial — modelo

Transcrição do leiaute a partir da foto real (`receituario-modelo-real.jpeg`, gitignored — a foto mostra vários talões preenchidos com nome/endereço/medicação de pacientes de verdade e não deve ser commitada/compartilhada). **Este documento contém só o layout, com os dados substituídos por marcadores.**

## O que é (e o que NÃO é)
É o formulário em papel de **Receituário de Controle Especial** (Portaria SVS/MS 344/98 da Anvisa), usado pra prescrever substâncias psicotrópicas/controladas (no modelo real: Amplictil, Haldol, Escitalopram). **Não tem relação com o sistema de assinatura digital certificada do CFM** (`prescricao.cfm.org.br`, ICP-Brasil) — aquele foi avaliado e descartado (ver decisão de 2026-07-09 no histórico do projeto): reproduzir o leiaute de um documento certificado sem a certificação real por trás seria arriscado. Este aqui é só um formulário simples, sem assinatura digital.

O talão físico original tem uma seção inferior de talão azul ("IDENTIFICAÇÃO DO COMPRADOR" / "IDENTIFICAÇÃO DO FORNECEDOR") que é específica do papel pré-numerado da gráfica — **não faz parte do que o sistema gera**. O sistema produz **2ª via em papel branco simples**, sem essa seção.

## Cabeçalho (fixo, mesmo timbre institucional do atestado)
```
RECEITUÁRIO CONTROLE ESPECIAL
IDENTIFICAÇÃO DO EMITENTE

CLÍNICA MODELO DEMONSTRATIVA LTDA.
RUA EXEMPLO, 123 – CENTRO - CIDADE - UF
CEP 00000-000 - FONE (00) 0000-0000
```
Reaproveita `prontuario.clinica.*` (mesma config já usada no atestado).

## Corpo (variável)
```
Paciente: {NOME}
Endereço: {ENDEREÇO}

Prescrição: {VIA DE USO — ex. "USO ORAL"}

{texto livre da prescrição — medicamento, dosagem, quantidade,
posologia. Ex.:
  Escitalopram 20mg/ml — 03 cxs
  tomar 01 cp 2x ao dia}
```
- **NOME** e **ENDEREÇO** vêm do cadastro do paciente.
- **Prescrição** é texto livre (textarea), como o corpo do atestado — a médica digita a medicação e a posologia; sem estrutura rígida por item porque a quantidade de remédios varia por receita.

## Assinatura (fixa, com um campo novo)
```
_______________________________
Ana Ribeiro
Médica
CRM-UF: 0000
RQE Nº: {RQE — AINDA NÃO CONFIRMADO}
```
O modelo real mostra RQE de outro médico da clínica (com RQE próprio) — **precisamos do RQE da própria médica** pra preencher `prontuario.medica.rqe`. Até confirmar, o campo fica em branco/lacuna no PDF (mesmo padrão de lacuna usado no atestado para CID/dias ausentes).

## Local + data
`Cidade-UF, {DATA por extenso}.` — mesmo padrão do atestado (pré-preenchida com hoje, editável).

## 2ª via
Confirmado com Claudio (2026-07-09): gerar em **papel branco simples, 2 vias** (não o talão azul pré-numerado) — as duas vias saem no mesmo PDF, uma abaixo da outra, rotuladas "1ª via" e "2ª via", com uma linha pontilhada de corte entre elas.

## Implicação de implementação
Mesmo padrão do atestado avulso (`CertificateService` / `PatientCertificateController`): não é entidade persistida, é uma ação de gerar PDF a partir do `Patient` direto (`/pacientes/{id}/receituario`), sem depender de uma consulta específica.
