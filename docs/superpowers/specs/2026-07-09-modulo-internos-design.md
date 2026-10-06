# Módulo Internos — design

**Data:** 2026-07-09
**Status:** aprovado por seções com o Claudio, aguardando revisão final antes de virar plano de implementação

## Contexto

A médica também acompanha pacientes internados na clínica (não são as consultas ambulatoriais de sempre). Ela pediu um jeito de anotar, de forma rápida, quem ela está acompanhando internado, sem precisar preencher a ficha completa (anamnese, CPF, telefone etc.) que a tela de "Nova consulta" exige.

A ideia original era gerar receitas médicas pra esses casos, mas isso foi descartado nesta mesma conversa: receita com validade jurídica plena exige certificado ICP-Brasil da médica e homologação do sistema como parceiro do CFM — fora de escopo pra um app local de uso único. Ficou definido que o app cobre o registro/acompanhamento dos internados, não a prescrição.

## Objetivo

Dar à médica um cadastro simples de pacientes internados (nome + data de nascimento) com um histórico de anotações de rotina datadas, e reorganizar a tela inicial pra ela escolher entre "Consultas" (fluxo que já existe, inalterado) e "Internos" (fluxo novo) assim que abre o sistema.

## Fora de escopo (decisões explícitas)

- **Receita médica**: cancelada nesta conversa (ver Contexto acima). Não faz parte deste módulo.
- **CPF, telefone, endereço, anamnese** para internos — só nome e data de nascimento.
- **Detecção de duplicidade** (como existe em Consultas) — não se aplica aqui; com poucos campos identificadores o recurso ajudaria pouco e atrapalharia o fluxo rápido que a médica quer.
- **PDF de anotações** — não foi pedido; Consultas mantém seu PDF, Internos não tem.
- **Leito/quarto, data de internação, motivo de internação** — não foram pedidos; se surgir necessidade depois, é uma extensão futura, não faz parte deste desenho.

## Modelo de dados

Novo módulo `com.zeloconsultas.modules.inpatient`, seguindo o mesmo padrão de pacote dos módulos existentes (`patient`, `consultation`): entity, repository, service, controller, `dto/` e `web/` conforme necessário.

### `Inpatient` (tabela `inpatients`)
- `id` (Long, gerado)
- `nome` (String, obrigatório)
- `dataNascimento` (LocalDate, obrigatório, no passado)
- `criadoEm` (Instant, `@PrePersist`)

### `InpatientNote` (tabela `inpatient_notes`)
- `id` (Long, gerado)
- `inpatient` (`@ManyToOne(fetch = LAZY, optional = false)`, FK pra `Inpatient`)
- `data` (LocalDate, obrigatório — default hoje ao abrir o formulário, mesmo padrão de `@DateTimeFormat(pattern = "yyyy-MM-dd")` já usado nos outros campos de data do sistema, pra não reincidir no bug de formatação corrigido nesta sessão)
- `anotacao` (String, `@Lob`, obrigatório — texto livre)
- `criadoEm` (Instant, `@PrePersist`)

### Serviço
Um único `InpatientService` cobre as duas entidades (cadastrar interno + primeira anotação juntos numa transação, listar internos, buscar por id, registrar nova anotação, listar histórico de anotações de um interno) — não é dividido em dois serviços porque o módulo é intencionalmente pequeno.

### Backup
Nenhuma mudança necessária — o backup automático (`BackupService`, `BACKUP TO`) copia o arquivo H2 inteiro, então as tabelas novas já ficam cobertas.

## Navegação

**Tela inicial (`GET /`)** deixa de mostrar o dashboard atual e vira uma tela de escolha: dois emblemas grandes lado a lado, "Consultas" e "Internos", cada um com um ícone circular em traço fino (ver seção Ícones). O cabeçalho (logo + menu) continua aparecendo normalmente.

**Menu do topo** passa a ter quatro links fixos: **Consultas | Pacientes | Internos | Backup**. O link "Backup" sai de dentro da home (onde hoje é só um cartão) e vira um item de menu, apontando pra rota `/backup` que já existe hoje (`BackupController`, sem mudança nenhuma nela) — só ganha um atalho fixo no menu, porque agora ele protege dado dos dois mundos (Consultas e Internos), não só de um.

**`GET /consultas`** (novo endpoint, dentro do `ConsultationController` existente) passa a servir o conteúdo que hoje está em `index.html`: contagem de pacientes cadastrados, lista de últimos atendimentos, cartões de ação (Nova consulta / Pacientes / Backup) e o aviso de lembrete de cópia de segurança. Nada nesse conteúdo muda de comportamento — só de endereço. `HomeController` fica só com a rota `GET /` (tela de escolha).

## Telas de Internos

- **`GET /internos`** — lista de internos cadastrados (nome + data de nascimento), ordenada por nome, com botão "Novo interno". Mesmo espírito visual da tela "Pacientes", mais enxuta (sem coluna de CPF).
- **`GET /internos/novo`** / **`POST /internos/novo`** — formulário único com nome, data de nascimento e a primeira anotação (texto livre). Salva `Inpatient` + primeira `InpatientNote` juntos, numa transação (mesmo padrão de "Nova consulta", que junta paciente + primeira consulta).
- **`GET /internos/{id}`** — ficha do interno: nome e data de nascimento no topo; logo abaixo, um campo de texto pra escrever a próxima anotação **direto na mesma página** (decisão confirmada com o Claudio: sem tela separada, porque é só um campo — reduz clique em relação ao padrão de retorno de Consultas); abaixo, a lista de anotações anteriores, mais recente primeiro, cada uma com sua data.
- **`POST /internos/{id}/notas`** — salva a nova anotação e recarrega a ficha.

## Ícones

Emblema circular (badge), traço fino, na cor pinheiro do sistema (`--pinheiro`), sem imagem externa — SVG inline no próprio HTML, mantendo o app 100% offline:
- **Consultas**: prancheta com uma ficha.
- **Internos**: leito/cama simples.

## O que não muda

O fluxo de Consultas (Nova consulta, Pacientes, histórico, atestado, retorno) continua exatamente como está hoje — nenhuma tela, rota ou comportamento desse fluxo é alterado por este módulo, só o endereço da home antiga (`/` → `/consultas`).
