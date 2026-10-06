package com.zeloconsultas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;

// Experiencia de usuaria nao-tecnica no computador da médica: ao subir,
// abre o navegador sozinho e coloca um icone na bandeja (nao uma janela de
// terminal), com opcao de fechar o sistema por ali.
//
// So e criado quando "prontuario.launcher.enabled=true" (profile desktop) —
// em dev/CI/servidor headless este bean nem existe, entao nao atrapalha nada.
// As partes graficas (Desktop/SystemTray) exigem ambiente com tela; num
// ambiente headless o metodo apenas registra e sai.
@Component
@ConditionalOnProperty(name = "prontuario.launcher.enabled", havingValue = "true")
public class DesktopLauncher {

    private static final Logger log = LoggerFactory.getLogger(DesktopLauncher.class);

    private final ApplicationContext applicationContext;
    private final ProntuarioProperties properties;

    public DesktopLauncher(ApplicationContext applicationContext, ProntuarioProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @EventListener
    public void aoIniciarServidor(WebServerInitializedEvent evento) {
        if (GraphicsEnvironment.isHeadless()) {
            log.info("Ambiente sem interface grafica — launcher desktop nao sera ativado.");
            return;
        }

        int porta = evento.getWebServer().getPort();
        String url = "http://localhost:" + porta;

        abrirNavegador(url);
        instalarBandeja(url);
    }

    private void abrirNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
                log.info("Navegador aberto em {}", url);
                return;
            }
        } catch (Exception e) {
            log.warn("java.awt.Desktop falhou ao abrir o navegador, tentando via 'cmd /c start'. URL: {}", url, e);
        }

        // Fallback: a API Desktop as vezes falha silenciosamente ou lanca
        // excecao dependendo do runtime empacotado (jlink) e da configuracao
        // do Windows. "cmd /c start" e o mesmo mecanismo que um duplo clique
        // num link usa e e mais confiavel nesse ambiente empacotado.
        try {
            new ProcessBuilder("cmd", "/c", "start", "", url).start();
            log.info("Navegador aberto via 'cmd /c start' em {}", url);
        } catch (Exception e) {
            log.warn("Nao consegui abrir o navegador automaticamente. Abra manualmente: {}", url, e);
        }
    }

    private void instalarBandeja(String url) {
        if (!SystemTray.isSupported()) {
            log.info("Bandeja do sistema nao suportada neste ambiente.");
            return;
        }
        try {
            PopupMenu menu = new PopupMenu();

            MenuItem abrir = new MenuItem("Abrir o prontuário");
            abrir.addActionListener(e -> abrirNavegador(url));
            menu.add(abrir);

            MenuItem sair = new MenuItem("Fechar o sistema");
            sair.addActionListener(e -> encerrar());
            menu.add(sair);

            // Icone minimo gerado em memoria (evita depender de arquivo externo).
            Image icone = Toolkit.getDefaultToolkit().createImage(new byte[0]);
            TrayIcon trayIcon = new TrayIcon(icone, properties.getNomeApp(), menu);
            trayIcon.setImageAutoSize(true);
            SystemTray.getSystemTray().add(trayIcon);
            log.info("Icone na bandeja instalado.");
        } catch (Exception e) {
            log.warn("Nao consegui instalar o icone na bandeja.", e);
        }
    }

    // Fecha o contexto Spring (para o servidor) e encerra a JVM — a usuaria
    // fecha tudo pela bandeja, sem terminal.
    private void encerrar() {
        log.info("Encerrando o sistema pela bandeja.");
        int codigo = SpringApplication.exit(applicationContext);
        System.exit(codigo);
    }
}
