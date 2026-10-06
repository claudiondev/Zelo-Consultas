// Intercepta formularios/links marcados com .form-gera-pdf / .link-gera-pdf
// e mostra o PDF num modal (iframe + botao X) em vez de navegar pra fora da
// pagina atual. Se o backend devolver a pagina de formulario de novo (erro
// de validacao), troca so o <main> em vez de navegar — mantem cabecalho/nav.
(function () {
    let urlAtual = null;

    function garantirModal() {
        let modal = document.getElementById('pdf-modal');
        if (modal) return modal;

        modal = document.createElement('div');
        modal.id = 'pdf-modal';
        modal.className = 'pdf-modal';
        modal.innerHTML =
            '<div class="pdf-modal-conteudo">' +
                '<button type="button" class="pdf-modal-fechar" aria-label="Fechar">&times;</button>' +
                '<iframe class="pdf-modal-frame" title="PDF gerado"></iframe>' +
            '</div>';
        document.body.appendChild(modal);

        modal.querySelector('.pdf-modal-fechar').addEventListener('click', fecharModal);
        modal.addEventListener('click', function (evento) {
            if (evento.target === modal) fecharModal();
        });
        document.addEventListener('keydown', function (evento) {
            if (evento.key === 'Escape') fecharModal();
        });

        return modal;
    }

    function abrirModalComBlob(blob) {
        const modal = garantirModal();
        if (urlAtual) URL.revokeObjectURL(urlAtual);
        urlAtual = URL.createObjectURL(blob);
        modal.querySelector('.pdf-modal-frame').src = urlAtual;
        modal.classList.add('aberto');
    }

    function fecharModal() {
        const modal = document.getElementById('pdf-modal');
        if (!modal || !modal.classList.contains('aberto')) return;
        modal.classList.remove('aberto');
        modal.querySelector('.pdf-modal-frame').src = '';
        if (urlAtual) {
            URL.revokeObjectURL(urlAtual);
            urlAtual = null;
        }
    }

    async function tratarResposta(resposta) {
        const tipo = resposta.headers.get('Content-Type') || '';
        if (tipo.includes('application/pdf')) {
            abrirModalComBlob(await resposta.blob());
            return;
        }

        // Nao veio PDF: provavelmente a mesma pagina de formulario com erro
        // de validacao. Troca so o <main>, preservando cabecalho/navegacao.
        const html = await resposta.text();
        const paginaNova = new DOMParser().parseFromString(html, 'text/html');
        const mainNovo = paginaNova.querySelector('main');
        const mainAtual = document.querySelector('main');
        if (mainNovo && mainAtual) {
            mainAtual.replaceWith(mainNovo);
        }
    }

    function falhaGenerica() {
        alert('Nao foi possivel gerar o PDF. Tente novamente.');
    }

    document.addEventListener('submit', function (evento) {
        const form = evento.target;
        if (!form.matches('.form-gera-pdf')) return;
        evento.preventDefault();

        // application/x-www-form-urlencoded em vez de FormData direto: sem
        // arquivo pra enviar, nao precisa de multipart — e formularios com
        // muitos campos (ex.: linhas dinamicas do esquema terapeutico) batem
        // no limite de "partes" que o Tomcat impoe por padrao em multipart.
        const dados = new URLSearchParams([...new FormData(form)]);
        fetch(form.action, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: dados,
        })
            .then(tratarResposta)
            .catch(falhaGenerica);
    });

    document.addEventListener('click', function (evento) {
        const link = evento.target.closest('.link-gera-pdf');
        if (!link) return;
        evento.preventDefault();

        fetch(link.href)
            .then(tratarResposta)
            .catch(falhaGenerica);
    });
})();
