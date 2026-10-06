# Empacotamento para Windows (jpackage)

Objetivo: entregar para a médica um instalador que ela clica duas vezes, sem terminal e sem precisar instalar Java. O `jpackage` embute um JRE junto com o app.

> **Importante:** o `jpackage` gera instalador **para o sistema onde ele roda**. Para produzir o `.exe`/`.msi` do Windows, estes passos precisam ser executados **numa máquina Windows** (com JDK 17+ e o WiX Toolkit instalado). Não dá para gerar o instalador Windows a partir do Linux. Os comandos abaixo estão prontos para rodar no Windows dela (ou numa VM Windows).

## 1. Gerar o JAR executável

Na pasta do projeto:

```
mvnw.cmd clean package
```

Isso produz `target/zelo-consultas-0.1.0.jar`.

## 2. Gerar o instalador Windows

Pré-requisitos no Windows: JDK 17+ (tem `jpackage`) e [WiX Toolkit 3.x](https://wixtoolset.org/) no PATH.

```
jpackage ^
  --type msi ^
  --name "Zelo" ^
  --app-version 0.1.0 ^
  --input target ^
  --main-jar zelo-consultas-0.1.0.jar ^
  --main-class org.springframework.boot.loader.launch.JarLauncher ^
  --java-options "-Dspring.profiles.active=desktop -Dprontuario.login.senha=TROCAR_AQUI" ^
  --win-shortcut ^
  --win-menu ^
  --win-dir-chooser
```

- `--java-options "-Dspring.profiles.active=desktop"` liga o launcher (abre o navegador sozinho + ícone na bandeja).
- **`-Dprontuario.login.senha=TROCAR_AQUI`** — a senha de login não fica em nenhum arquivo versionado (ver `SecurityConfig`/`ProntuarioProperties`). Substitua `TROCAR_AQUI` pela senha real só na hora de rodar esse comando; nunca cole a senha real de volta neste arquivo.
- `--win-shortcut` cria o atalho na área de trabalho.
- `--main-class ...JarLauncher` é o launcher do Spring Boot para JAR executável (Boot 3.2+). Se um dia mudar a versão do Boot, confira o nome dessa classe.

O resultado é um `.msi`: ela executa, instala e passa a ter o atalho.

## 3. Comportamento ao abrir (profile desktop)

- Escolhe uma porta livre a partir de 8080 (se estiver ocupada, tenta a próxima — não trava).
- Abre o navegador padrão na página inicial.
- Coloca um ícone na bandeja com "Abrir o prontuário" e "Fechar o sistema".
- Fechar pela bandeja encerra o servidor.

## O que ainda falta validar (precisa da máquina Windows)

Estas partes foram implementadas mas **não puderam ser testadas neste ambiente Linux/headless** — validar no Windows da médica ou numa VM Windows:

- Abertura automática do navegador e ícone na bandeja (dependem de ambiente gráfico).
- Detecção da unidade removível do pendrive (usa a API do Windows para reconhecer "unidade removível"; em Linux o conceito é diferente).
- O próprio instalador `jpackage` (só gera Windows rodando no Windows).

A lógica de escolher porta livre, o backup local (H2 `BACKUP TO`) e a mecânica de cópia pro pendrive (zip + rename atômico + rotação) **foram testadas** (testes automatizados + validação manual no Linux).

## Ícone (opcional)

Para um ícone próprio no atalho, adicione `--icon caminho\para\icone.ico` ao comando do `jpackage`.

## Checklist de validação na primeira instalação real

Rodar isso na primeira vez que o `.msi` for instalado num Windows de verdade (o notebook dela ou outro), antes de considerar o empacotamento pronto:

- [ ] `.msi` instala sem erro (Avançar/Avançar/Concluir), cria atalho na área de trabalho e no menu iniciar.
- [ ] Duplo clique no atalho abre o navegador padrão sozinho, direto na tela de login.
- [ ] Ícone aparece na bandeja do sistema (perto do relógio) com as opções "Abrir o prontuário" e "Fechar o sistema".
- [ ] "Abrir o prontuário" (bandeja) reabre a página no navegador sem duplicar o servidor.
- [ ] Login com usuário/senha reais funciona.
- [ ] Fechar pela bandeja ("Fechar o sistema") derruba o servidor de verdade (não fica processo Java travado — checar Gerenciador de Tarefas).
- [ ] Reabrir o atalho depois de fechado funciona de novo (porta não fica presa).
- [ ] Cadastrar um paciente de teste, gerar consulta, verificar se o backup automático local aconteceu (pasta de backups criada/atualizada).
- [ ] Conectar um pendrive de verdade, usar o botão de cópia de backup — confirmar que ele detecta a unidade removível sozinho e grava o zip nela (esse é o ponto mais incerto: a detecção é Windows-específica e nunca foi testada de verdade).
- [ ] Testar em tela de toque (é um 2-em-1): botões e campos dão pra tocar sem precisar de mouse/teclado.
- [ ] Desinstalar pelo Painel de Controle remove o app limpo (não deixa lixo nem quebra o atalho).

Se algum item falhar, **não apagar os dados de teste do jeito errado** — o BACKUP TO e o zip do pendrive são a prova de que a lógica funcionou mesmo se a UI travar; olhar os logs antes de reinstalar do zero.
