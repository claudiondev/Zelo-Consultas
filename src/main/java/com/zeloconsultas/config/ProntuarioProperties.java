package com.zeloconsultas.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// Liga o bloco "prontuario.*" do application.yml a objetos Java tipados,
// em vez de espalhar @Value("${...}") pelo codigo. So clinica/medica por
// enquanto — o subtree "backup" fica pro BackupService (feature futura).
@Component
@ConfigurationProperties(prefix = "prontuario")
@Getter
@Setter
public class ProntuarioProperties {

    // Nome comercial do produto (exibido em titulo de pagina, cabecalho e
    // empacotamento) — separado do artifactId/pacote Java, que nao mudam.
    private String nomeApp = "Zelo";

    private Clinica clinica = new Clinica();
    private Medica medica = new Medica();
    private Backup backup = new Backup();
    private Login login = new Login();

    // Trava de PIN simples (pedido explicito: usuario/senha fixos, sem
    // cadastro de conta — e um app local de uso unico). Usuario nao e
    // sensivel (e so o nome dela), mas a senha NUNCA tem default aqui nem no
    // application.yml — repositorio e versionado, ela so pode vir de fora
    // (variavel de ambiente PRONTUARIO_LOGIN_SENHA ou -Dprontuario.login.senha,
    // Spring resolve as duas formas automaticamente). SecurityConfig recusa
    // subir se estiver em branco (ver userDetailsService).
    @Getter
    @Setter
    public static class Login {
        private String usuario = "ADMIN";
        private String senha;
    }

    @Getter
    @Setter
    public static class Backup {
        private String diretorio = "./backups";
        private int maximoCopiasLocais = 30;
        private int maximoCopiasPendrive = 8;
        // Se a ultima copia pro pendrive passou disso, o app avisa ao abrir.
        private int lembreteDias = 7;
    }

    @Getter
    @Setter
    public static class Clinica {
        private String nome;
        private String razaoSocial;
        private String endereco;
        private String complemento;
        private String cidade;
    }

    @Getter
    @Setter
    public static class Medica {
        private String nome;
        private String titulo;
        private String especialidade;
        private String crm;
        // Aparece no receituario de controle especial (Portaria SVS/MS 344/98).
        // Nao confirmado ainda com a médica — fica vazio (o template
        // simplesmente omite a linha) enquanto isso.
        private String rqe;

        // Derivados pra tela de login/home (saudacao, iniciais do avatar) —
        // evita logica de split de string espalhada nos templates Thymeleaf.
        public String getPrimeiroNome() {
            return nome == null || nome.isBlank() ? "" : nome.trim().split("\\s+")[0];
        }

        public String getInicial() {
            return nome == null || nome.isBlank() ? "" : nome.trim().substring(0, 1).toUpperCase();
        }
    }
}
