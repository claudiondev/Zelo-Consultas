// Adiciona/remove linhas de medicamento na tela do esquema terapeutico.
// Spring faz bind de List por indice sequencial (linhas[0].campo,
// linhas[1].campo, ...) — por isso toda alteracao renumera todo mundo, senao
// um buraco no meio da sequencia faz o back-end nao enxergar as linhas depois
// do buraco.
(function () {
    function renumerar() {
        document.querySelectorAll('.linha-medicamento').forEach(function (linha, indice) {
            linha.querySelectorAll('[data-campo]').forEach(function (campo) {
                campo.name = 'linhas[' + indice + '].' + campo.dataset.campo;
            });
        });
    }

    function criarLinha() {
        const linha = document.createElement('tr');
        linha.className = 'linha-medicamento';
        linha.innerHTML =
            '<td><input type="text" data-campo="nomeMedicamento"></td>' +
            '<td><input type="text" data-campo="manha"></td>' +
            '<td><input type="text" data-campo="tarde"></td>' +
            '<td><input type="text" data-campo="noite"></td>' +
            '<td><button type="button" class="botao secundario remover-linha">Remover</button></td>';
        return linha;
    }

    document.addEventListener('click', function (evento) {
        if (evento.target.matches('#adicionar-medicamento')) {
            document.getElementById('linhas-medicamentos').appendChild(criarLinha());
            renumerar();
        }
        if (evento.target.matches('.remover-linha')) {
            evento.target.closest('.linha-medicamento').remove();
            renumerar();
        }
    });
})();
