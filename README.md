# Zelo - Consultas

Prontuário psiquiátrico digital, **local e offline**. Organiza as consultas por paciente, mantém um histórico único e buscável, e gera atestado e registro de consulta em PDF no modelo da clínica. Substitui o modelo Word que se esgota a cada tanto e fragmenta o histórico em vários arquivos.

Roda inteiro no próprio computador, sem internet, num único processo. Pensado para um notebook 2-em-1 de specs modestas (interface com alvos de toque grandes).

---

## O que o sistema faz

- **Cadastro em formulário único** — dados do paciente, anamnese e primeira consulta juntos.
- **Consulta de retorno** — só a evolução, sem repetir a anamnese.
- **Confirmação de duplicidade** — ao salvar, sugere pacientes parecidos por nome/CPF ("é o mesmo?"), sem forçar unicidade (nem todo paciente tem CPF).
- **Busca por nome ou CPF** + histórico de consultas do paciente.
- **Atestado em PDF** no modelo da clínica — texto e data editáveis, dias por extenso, CID incluído.
- **Registro da consulta em PDF** para impressão.
- **Backup automático** dos dados a cada consulta, com cópia extra para pendrive.

---

## Como rodar

Pré-requisitos: Java 17+ (o Maven vem embutido via `./mvnw`).

```bash
./mvnw spring-boot:run
```

Depois abra `http://localhost:8080` no navegador.

Modo de depuração (liga o console do banco em `/h2-console`):
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Rodar os testes:
```bash
./mvnw test
```

Gerar o instalador para Windows (`.msi`): ver **[PACKAGING.md](PACKAGING.md)**.

---

## Onde ficam os dados

- **Banco:** `data/prontuario.mv.db` (arquivo H2, no computador).
- **Backups automáticos:** pasta `backups/` — uma cópia a cada consulta salva, mantendo as ~30 mais recentes.
- **Cópias no pendrive:** `zelo-consultas-AAAA-MM-DD.zip` na raiz do pendrive.

Esses são os dados da clínica e **nunca vão para o repositório** (estão no `.gitignore`).

---

## Backup — as duas camadas

1. **Automática, no computador** — feita sozinha após cada consulta salva.
2. **No pendrive (manual)** — a única que sobrevive se o computador quebrar. O sistema avisa na tela inicial quando faz muito tempo que não se copia.

### Gravar a cópia no pendrive
1. Plugue o pendrive.
2. Abra **Cópia de segurança** → clique em **Gravar cópia aqui**.
3. Aguarde a confirmação, então desplugue e guarde o pendrive separado do computador.

> A cópia é gravada primeiro como arquivo temporário e só vira a cópia final quando termina inteira — se o pendrive for removido no meio, nunca sobrescreve uma cópia boa por uma pela metade.

---

## Restaurar (recuperar os dados)

Faça com o **sistema fechado**:

1. Feche o sistema.
2. Abra o `.zip` da cópia desejada (de `backups/` ou do pendrive) — dentro há um arquivo `prontuario.mv.db`.
3. Copie esse `prontuario.mv.db` para a pasta `data/`, substituindo o atual.
4. Abra o sistema de novo.

---

## Configuração

Dados fixos (timbre da clínica, nome/CRM da médica, pastas de backup) ficam em `src/main/resources/application.yml`, no bloco `prontuario:` — dá para ajustar sem mexer no código.

---

## Tecnologia

Java 17 · Spring Boot 3.5 · Thymeleaf · H2 (banco em arquivo) · openhtmltopdf (PDF) · Maven. Arquitetura em camadas (Controller → Service → Repository → Entity), organizada por módulo.
