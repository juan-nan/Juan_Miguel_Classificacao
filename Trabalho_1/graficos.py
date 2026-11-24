# gera os gráficos da apresentação a partir dos CSVs do experimento
# uso: python3 graficos.py src/resultados_organizados.csv <pasta de saída>
import sys
import numpy as np
import pandas as pd
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.ticker import FixedLocator, NullLocator

CSV, OUT = sys.argv[1], sys.argv[2]
d = pd.read_csv(CSV)
media = d.set_index(["Algoritmo", "Tamanho", "Caso"])["Media"]
execucoes = pd.read_csv(CSV.replace("resultados_organizados.csv", "resultados_experimento.csv"))

TEXT, MUTED, GRID = "#1F2A30", "#5B6770", "#DDE3E6"
QUAD, NLOGN, LIN = "#9A031E", "#0F4C5C", "#E36414"
plt.rcParams.update({"font.family": "Noto Sans", "font.size": 12, "text.color": TEXT,
                     "axes.labelcolor": MUTED, "xtick.color": MUTED, "ytick.color": MUTED})
TAMANHO_FIG = (7.6, 5.3)


def fmt_tempo(ms):
    if ms < 10:
        return f"{ms:.1f} ms".replace(".", ",")
    if ms < 1000:
        return f"{ms:.0f} ms"
    s = ms / 1000
    if s < 60:
        return f"{s:.0f} s"
    if s < 3600:
        return f"{s / 60:.0f} min"
    return f"{s / 3600:.1f} h".replace(".", ",")


NOMES_TICKS = {0.01: "0,01 ms", 0.1: "0,1 ms", 1: "1 ms", 10: "10 ms", 100: "100 ms", 1000: "1 s",
               10000: "10 s", 60000: "1 min", 600000: "10 min", 3600000: "1 h"}


def eixo_tempo(eixo, ticks):
    eixo.set_major_locator(FixedLocator(ticks))
    eixo.set_minor_locator(NullLocator())
    eixo.set_ticklabels([NOMES_TICKS[t] for t in ticks])


def limpar(ax):
    for s in ("top", "right", "left"):
        ax.spines[s].set_visible(False)
    ax.spines["bottom"].set_color("#9AA5AB")
    ax.tick_params(length=0, pad=6)


def salvar(fig, nome):
    fig.savefig(f"{OUT}/{nome}.png", dpi=200, transparent=True)
    plt.close(fig)


# ---------- 1: crescimento com n (aleatório, log-log) ----------
TAM = [1000, 10000, 100000, 1000000]
SERIES = [  # algoritmo, cor, marcador, deslocamento vertical do rótulo (pontos)
    ("Bubble Sort", QUAD, "o", 3),
    ("Insertion Sort", "#E36414", "s", 3),
    ("Selection Sort", "#C9A227", "^", -11),
    ("Shell Sort", "#7FB7BE", "D", 9),
    ("Heap Sort", "#3A8F9C", "v", 3),
    ("Merge Sort", "#1F6F7F", "P", -4),
    ("Quick Sort", NLOGN, "o", -3),
]
fig, ax = plt.subplots(figsize=TAMANHO_FIG)
ax.axvspan(3.2e5, 3.2e6, color="#EEF4F5", zorder=0)
ax.text(1e6, 1.4e-2, "1 execução\n(demais: 10)", ha="center", va="bottom", fontsize=10, color=MUTED)
for nome, cor, mk, dy in SERIES:
    ys = [media[(nome, n, "aleatorio")] for n in TAM]
    ax.plot(TAM, ys, color=cor, lw=2.2, marker=mk, ms=6.5, zorder=3, markeredgecolor="white", markeredgewidth=1)
    ax.annotate(nome.replace(" Sort", ""), (TAM[-1], ys[-1]), xytext=(10, dy),
                textcoords="offset points", va="center", fontsize=11.5)
ax.set_xscale("log")
ax.set_yscale("log")
ax.set_xlim(7e2, 1.4e6)
ax.set_ylim(1e-2, 4e7)
ax.xaxis.set_major_locator(FixedLocator(TAM))
ax.xaxis.set_minor_locator(NullLocator())
ax.set_xticklabels(["10³", "10⁴", "10⁵", "10⁶"])
eixo_tempo(ax.yaxis, [0.01, 0.1, 1, 10, 100, 1000, 10000, 60000, 600000, 3600000])
ax.grid(axis="y", color=GRID, lw=0.8)
ax.set_axisbelow(True)
limpar(ax)
ax.set_xlabel("tamanho do vetor n (ordem aleatória)")
ax.set_ylabel("tempo médio (escala log)")
ax.text(1.1e3, 1.2e7, "O(n²): Bubble, Insertion, Selection", color=QUAD, fontsize=12, weight="bold")
ax.text(1.1e3, 3.2e6, "O(n log n) e Shell: Heap, Merge, Quick, Shell", color=NLOGN, fontsize=12, weight="bold")
fig.subplots_adjust(left=0.13, right=0.85, top=0.97, bottom=0.11)
salvar(fig, "grafico_crescimento")

# ---------- 2: efeito da ordem dos dados (n = 10⁵, os 10 algoritmos) ----------
ALGS = ["Bubble Sort", "Insertion Sort", "Selection Sort", "Shell Sort", "Heap Sort", "Merge Sort",
        "Quick Sort", "Counting Sort", "Radix Sort", "Bucket Sort"]
CASOS = [("crescente", "Crescente", "#3A9DA8", "^", -0.26),
         ("decrescente", "Decrescente", NLOGN, "v", 0.0),
         ("aleatorio", "Aleatório", LIN, "o", 0.26)]
fig, ax = plt.subplots(figsize=(7.6, 5.6))
for i, alg in enumerate(ALGS):
    if i % 2 == 0:
        ax.axhspan(i - 0.46, i + 0.46, color="#F4F8F9", zorder=0)
    for caso, rot, cor, mk, off in CASOS:
        v = media[(alg, 100000, caso)]
        ax.plot([0.2, v], [i + off] * 2, color=cor, lw=0.8, alpha=0.35, zorder=1)
        ax.plot(v, i + off, marker=mk, ms=7.5, color=cor, markeredgecolor="white",
                markeredgewidth=0.8, zorder=3, linestyle="none")
        ax.annotate(fmt_tempo(v), (v, i + off), xytext=(7, 0), textcoords="offset points",
                    va="center", fontsize=10)
for caso, rot, cor, mk, off in CASOS:
    ax.plot([], [], marker=mk, ms=9, color=cor, linestyle="none", label=rot)
ax.axhline(6.5, color="#9AA5AB", lw=1, ls="--")
ax.text(3e5, 6.62, "tempo linear: no aleatório a faixa de valores\ntambém é maior (até 10⁷ contra n)",
        ha="right", va="top", fontsize=9.5, color=LIN)
ax.set_xscale("log")
ax.set_xlim(0.2, 3e5)
eixo_tempo(ax.xaxis, [1, 10, 100, 1000, 10000])
ax.set_yticks(range(len(ALGS)))
ax.set_yticklabels([a.replace(" Sort", "") for a in ALGS], fontsize=12, color=TEXT)
ax.set_ylim(len(ALGS) - 0.5, -0.5)
ax.grid(axis="x", color=GRID, lw=0.8)
ax.set_axisbelow(True)
limpar(ax)
ax.set_xlabel("tempo médio com n = 10⁵ (escala log, 10 execuções)")
ax.legend(loc="lower center", bbox_to_anchor=(0.45, 1.0), ncol=3, frameon=False, fontsize=11.5,
          handletextpad=0.3, columnspacing=1.4)
fig.subplots_adjust(left=0.14, right=0.98, top=0.93, bottom=0.1)
salvar(fig, "grafico_ordem")

# ---------- 3: variação entre as 10 execuções (boxplot, n = 10⁵, aleatório) ----------
r = execucoes[(execucoes.Tamanho == 100000) & (execucoes.Caso == "aleatorio")]
GRUPO = {"Bubble Sort": QUAD, "Insertion Sort": QUAD, "Selection Sort": QUAD,
         "Shell Sort": NLOGN, "Heap Sort": NLOGN, "Merge Sort": NLOGN, "Quick Sort": NLOGN,
         "Counting Sort": LIN, "Radix Sort": LIN, "Bucket Sort": LIN}
ordem = list(r.groupby("Algoritmo").TempoMs.median().sort_values().index)
dados = []
for alg in ordem:
    t = r[r.Algoritmo == alg].TempoMs
    dados.append((t / t.median()).values)
fig, ax = plt.subplots(figsize=TAMANHO_FIG)
bp = ax.boxplot(dados, vert=False, whis=(0, 100), widths=0.55, patch_artist=True,
                medianprops=dict(color=TEXT, lw=2), whiskerprops=dict(color=MUTED, lw=1.1),
                capprops=dict(color=MUTED, lw=1.1), showfliers=False)
for caixa, alg in zip(bp["boxes"], ordem):
    caixa.set(facecolor=GRUPO[alg] + "33", edgecolor=GRUPO[alg], linewidth=1.5)
rng = np.random.default_rng(1)
for i, (alg, v) in enumerate(zip(ordem, dados), start=1):
    ax.plot(v, i + rng.uniform(-0.13, 0.13, len(v)), "o", ms=4.5, color=GRUPO[alg],
            markeredgecolor="white", markeredgewidth=0.6, zorder=3)
    ax.annotate(f"máx/mín {v.max() / v.min():.2f}".replace(".", ","), (v.max(), i), xytext=(7, 0),
                textcoords="offset points", va="center", fontsize=9.5, color=MUTED)
ax.set_yticks(range(1, len(ordem) + 1))
ax.set_yticklabels([f"{a.replace(' Sort', '')}  ({fmt_tempo(r[r.Algoritmo == a].TempoMs.median())})"
                    for a in ordem], fontsize=11, color=TEXT)
ax.axvline(1, color="#9AA5AB", lw=1, zorder=0)
ax.set_xlim(0.9, 1.42)
ax.xaxis.set_major_locator(FixedLocator([0.9, 1.0, 1.1, 1.2, 1.3, 1.4]))
ax.set_xticklabels(["-10%", "mediana", "+10%", "+20%", "+30%", "+40%"])
ax.grid(axis="x", color=GRID, lw=0.8)
ax.set_axisbelow(True)
limpar(ax)
ax.set_xlabel("tempo de cada execução em relação à mediana do algoritmo\n"
              "(n = 10⁵, aleatório; caixa = quartis, hastes = mín. e máx.)")
for cor, rot, x in ((QUAD, "O(n²)", 0.0), (NLOGN, "O(n log n) e Shell", 0.17), (LIN, "tempo linear", 0.55)):
    ax.text(x, 1.02, rot, transform=ax.transAxes, color=cor, fontsize=12, weight="bold")
fig.subplots_adjust(left=0.27, right=0.97, top=0.93, bottom=0.17)
salvar(fig, "grafico_boxplot")
