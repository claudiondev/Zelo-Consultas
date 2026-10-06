# Estrutura da Ficha Médica real — modelo

Transcrição da estrutura de campos do arquivo `ficha-medica-modelo-real.doc` (extraído via `strings` do binário .doc, formato antigo Composite Document/OLE). **Este documento contém só os rótulos dos campos, sem dados de nenhum paciente real** — o arquivo original tem dados de um paciente de verdade e não deve ser commitado/compartilhado sem anonimizar.

7 páginas no total. Um arquivo por paciente (nome do arquivo = nome do paciente).

## Cabeçalho
Dra. Ana Ribeiro — CRM-UF 0000

- Nome
- Plano de Saúde
- D.N. (data de nascimento)
- Sexo
- Endereço
- Local de Atendimento
- Fone
- Religião
- Profissão
- Acompanhante
- Valor
- Recibo / NF
- CPF

## 1. QP — Queixa Principal
Texto livre.

## 2. HDA — História da Doença Atual
Texto livre, mais um checklist de sintomas observados durante a entrevista ("Cs durante entrevista"):
Humor, culpa, suicídio, insônia, pensamento, agitação, ansiedade/tensão, TIQUES, TOC, dif. intelectuais, sint. digestórios, sint. muscular, sint. genitourinários, hipocondria, crítica da doença, incapacidade de sentir.

Mais campos clínicos gerais: Fumo, Álcool, SPA (substância psicoativa), Altura/Peso, Sinal/Particularidade, Observação, Cirurgias, Tto médico, AFDM, A.FAM. (antecedentes familiares), DUM-TPM, Anticoncepcional.

## 3. Criança
Alim., Marcha, Ling. (linguagem), Sono, Esfíncteres, Tiques, Escolar, Social, DPI, Hospit., Vacinas, uso de celular.

## 4. Adolescente
Socialização, Namoro-Sexo, Drogas, Problemas legais, Obs.

## 5. Adulto
- **Laboral:** forma, função, adequação
- **Social:** amizade, hábitos, lazer/atividade física
- **Família:** cônjuge, mora com

## 6. PPM
Passado Psiquiátrico Mórbido — campo de texto livre.

## 7. H.Psiq — História Psiquiátrica
Tratamento medicamentoso/fármacos, tempo, adesão, médico responsável/tempo, ECT, hospitalização, exames.

## Escalas padronizadas (embutidas no modelo)

**Inventário de Ansiedade de Beck (BAI)** — 21 itens, cada um pontuado em 4 níveis (Absolutamente não / Levemente — não me incomodou muito / Moderadamente — foi muito desagradável mas pude suportar / Gravemente — dificilmente pude suportar). Itens: dormência/formigamento, sensação de calor, tremores nas pernas, incapaz de relaxar, medo que aconteça o pior, atordoado/tonto, palpitação/aceleração do coração, sem equilíbrio físico/mental, aterrorizado, nervoso, sensação de sufocar, tremores nas mãos, trêmulo, medo de perder o controle, dificuldade de respirar, medo de morrer, assustado, indigestão/desconforto abdominal, desmaio, rosto afogueado, suor (não por calor). Termina com campo de pontuação/classificação.

**Inventário de Depressão de Beck (BDI)** — mesma escala de 4 níveis, 21 itens: tristeza, falta de coragem/pessimismo, fracasso, insatisfação, culpa, punição, desapontamento, autocrítica, ideação suicida (ideia/plano/meios), tentativas anteriores, choro, irritação, falta de interesse, dificuldade de tomar decisões, aparência (unhas/cabelo), dificuldade de trabalhar, dificuldade de dormir, cansaço, falta/aumento de apetite, perda de peso, preocupação com a saúde, falta de interesse sexual. Depois um bloco de classificação: recorrente / psicótica / luto / comorbidade / unipolar / grau / início precoce / pontuação.

## 8. Consultas (evolução por retorno)

Esse bloco se repete **cerca de 20 vezes** no arquivo — é literalmente o que "esgota" o modelo quando todas as linhas são preenchidas, forçando a médica a criar um novo arquivo.

Primeira entrada (mais completa):
- DATA
- HISTÓRICO
- TRATAMENTO
- OBS
- CID
- ATESTADO (tempo/dias)
- Recomendação (ex.: "lembrar CUIDADO ao dirigir")
- Risco de Suicídio
- Valor R$
- Retornar em
- Obs

Entradas seguintes (mais enxutas, se repetem):
- DATA
- HISTÓRICO
- TRATAMENTO
- Valor R$
- Retornar em
- Obs

**Observação de design:** o atestado não é um formulário separado preenchido do zero — é um campo dentro da própria consulta (`ATESTADO (tempo)`), usando o nome/CPF que já estão no cadastro do paciente. Ou seja, gerar o PDF do atestado pode ser uma ação a partir do registro da consulta, sem precisar digitar nome/CPF de novo.
