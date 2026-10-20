/*
 * dashboard.js
 * Monta os gráficos do painel usando o Chart.js.
 * Os dados chegam prontos do Thymeleaf (ver o <script th:inline> no index.html),
 * então aqui não há nenhuma regra de negócio: só configuração visual dos gráficos.
 */

function iniciarGraficosDashboard(dados) {
    criarDonut('grafico-donut-mes-passado', dados.mesPassado);
    criarDonut('grafico-donut-mes-retrasado', dados.mesRetrasado);
    criarDonut('grafico-donut-total', dados.total);
    criarBarrasTop5('grafico-top5-servidor', dados.top5Servidor, '#dc3545');
    criarBarrasTop5('grafico-top5-chefia', dados.top5Chefia, '#ffc107');
}

// Plugin: escreve o TOTAL DE PLANOS no buraco central do donut, com o rótulo "planos"
// pra deixar explícito que é total de planos (não de usuários/servidores).
const pluginTotalNoCentro = {
    id: 'totalNoCentro',
    afterDraw(chart) {
        const cfg = chart.options.plugins.totalNoCentro;
        if (!cfg) {
            return;
        }
        const ctx = chart.ctx;
        const x = (chart.chartArea.left + chart.chartArea.right) / 2;
        const y = (chart.chartArea.top + chart.chartArea.bottom) / 2;

        ctx.save();
        ctx.textAlign = 'center';
        ctx.textBaseline = 'middle';
        ctx.fillStyle = '#343a40';
        ctx.font = '700 24px sans-serif';
        ctx.fillText(Number(cfg.total || 0).toLocaleString('pt-BR'), x, y - 6);
        ctx.fillStyle = '#8a94a6';
        ctx.font = '400 12px sans-serif';
        ctx.fillText('planos', x, y + 16);
        ctx.restore();
    }
};

// Donut com 3 fatias: sem pendência (adimplentes), pendência da chefia e pendência do servidor.
// A fatia "sem pendência" é derivada: total - chefia - servidor. O total aparece no centro.
function criarDonut(idCanvas, resumo) {
    const qtdSemPendencia = Math.max(resumo.total - resumo.chefia - resumo.servidor, 0);

    new Chart(document.getElementById(idCanvas), {
        type: 'doughnut',
        data: {
            labels: ['Sem pendência', 'Pendência da chefia', 'Pendência do servidor'],
            datasets: [{
                data: [qtdSemPendencia, resumo.chefia, resumo.servidor],
                backgroundColor: ['#28a745', '#ffc107', '#dc3545'],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '62%', // buraco maior pra caber o total no centro
            plugins: {
                legend: { position: 'bottom' },
                totalNoCentro: { total: resumo.total }
            }
        },
        plugins: [pluginTotalNoCentro]
    });
}

// Ranking horizontal (top 5) já vem ordenado e limitado pelo backend.
function criarBarrasTop5(idCanvas, top5, cor) {
    new Chart(document.getElementById(idCanvas), {
        type: 'bar',
        data: {
            labels: top5.labels,
            datasets: [{
                label: 'Pendências',
                data: top5.valores,
                backgroundColor: cor
            }]
        },
        options: {
            indexAxis: 'y', // deixa as barras na horizontal
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: { beginAtZero: true, ticks: { precision: 0 } } // contagem inteira, sem casas decimais
            }
        }
    });
}
