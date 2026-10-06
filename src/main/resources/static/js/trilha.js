// Realca na trilha lateral a secao que esta na tela, enquanto a médica
// rola o formulario longo. Vanilla JS, sem dependencia — o app roda offline.
document.addEventListener('DOMContentLoaded', function () {
  var trilha = document.querySelector('.trilha');
  if (!trilha || !('IntersectionObserver' in window)) return;

  var pares = [];
  trilha.querySelectorAll('.trilha-item').forEach(function (link) {
    var alvo = document.querySelector(link.getAttribute('href'));
    if (alvo) pares.push({ link: link, alvo: alvo });
  });
  if (!pares.length) return;

  var observer = new IntersectionObserver(function (entradas) {
    entradas.forEach(function (entrada) {
      if (!entrada.isIntersecting) return;
      var par = pares.find(function (p) { return p.alvo === entrada.target; });
      if (!par) return;
      pares.forEach(function (p) { p.link.classList.remove('ativa'); });
      par.link.classList.add('ativa');
    });
  }, { rootMargin: '-25% 0px -65% 0px' });

  pares.forEach(function (p) { observer.observe(p.alvo); });
});
