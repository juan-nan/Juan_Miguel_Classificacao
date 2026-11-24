# TP2 — Grafos: Divisão de Tarefas

Trabalho em dupla (**Miguel** e **Juan**) · Peso 18% · Algoritmos e Classificação de Dados (UNIPAMPA)

---

## Progresso (atualizado em 24/11/2026)

| Parte | Responsável | Situação |
|---|---|---|
| Etapa 0 (UML, GTgraph, grafos, decisões D1–D5) | Miguel e Juan | ✅ Concluída |
| Estruturas (`IGraph`, LA, MA, leitor, impressão) | Miguel | ✅ Concluída |
| Bellman-Ford, Dijkstra, Floyd-Warshall | Miguel | ✅ Concluída |
| Testes de corretude (69 testes, todos passando) | Miguel | ✅ Concluída |
| Experimento (60 blocos, 600 medições → CSV) | Miguel | ✅ Concluído (rodada preliminar, com carga na máquina) |
| Experimento final com a máquina ociosa | Miguel | ⏳ Pendente |
| (Bônus) Ocupação de memória | Miguel | ⏳ Pendente |
| Artigo: introdução, fundamentação, metodologia + slides | Miguel | ⏳ Pendente |
| BFS, DFS, Ord. Topológica, Kruskal, Prim, Ford-Fulkerson | Juan | ⏳ Não iniciado |
| Grafos de exemplo + menu interativo | Juan | ⏳ Não iniciado |
| Boxplots + testes estatísticos | Juan | ⏳ Não iniciado (o CSV já está disponível) |
| Artigo: resultados, discussão, ameaças, conclusão + slides | Juan | ⏳ Não iniciado |

### Arquivos já prontos

```
src/graph/        GraphType, Vertex, Edge, IGraph, AdjListGraph, AdjMatrixGraph, GraphReader, GraphPrinter
src/algorithms/   BellmanFord, Dijkstra, FloydWarshall, IndexedMinPQ, ShortestPathResult
src/experiment/   ExperimentRunner
src/tests/        CorrectnessTest
resultados/       resultados_experimento.csv (600 medições), log_experimento.txt
```

### Resultados preliminares (média de 10 execuções, em ms)

Rodada de 24/11/2026: 164,4 s no total, com Firefox e VS Code abertos (carga ~2). Esta rodada deve ser **substituída** pela rodada com a máquina ociosa.

| V | D nominal | BF LA | BF MA | MA/LA | Dijkstra LA | Dijkstra MA | MA/LA |
|---|---|---|---|---|---|---|---|
| 100 | 0,2 | 0,41 | 2,51 | 6,1× | 0,019 | 0,055 | 2,9× |
| 100 | 0,4 | 0,75 | 5,01 | 6,7× | 0,044 | 0,085 | 1,9× |
| 100 | 0,6 | 0,87 | 6,07 | 7,0× | 0,056 | 0,135 | 2,4× |
| 100 | 0,8 | 1,11 | 7,51 | 6,8× | 0,035 | 0,112 | 3,2× |
| 100 | 1,0 | 1,49 | 7,96 | 5,3× | 0,038 | 0,167 | 4,4× |
| 200 | 0,2 | 5,21 | 24,60 | 4,7× | 0,093 | 0,242 | 2,6× |
| 200 | 0,4 | 7,05 | 46,40 | 6,6× | 0,098 | 0,387 | 3,9× |
| 200 | 0,6 | 11,67 | 56,71 | 4,9× | 0,118 | 0,609 | 5,2× |
| 200 | 0,8 | 12,31 | 61,18 | 5,0× | 0,156 | 0,635 | 4,1× |
| 200 | 1,0 | 16,15 | 64,39 | 4,0× | 0,163 | 0,424 | 2,6× |
| 500 | 0,2 | 74,44 | 430,99 | 5,8× | 0,336 | 1,110 | 3,3× |
| 500 | 0,4 | 156,13 | 650,64 | 4,2× | 0,948 | 2,720 | 2,9× |
| 500 | 0,6 | 277,47 | 980,04 | 3,5× | 1,458 | 4,117 | 2,8× |
| 500 | 0,8 | 430,45 | 1241,10 | 2,9× | 1,750 | 3,752 | 2,1× |
| 500 | 1,0 | 502,22 | 1308,36 | 2,6× | 2,061 | 3,862 | 1,9× |

Observações para o Juan (análise e discussão):
- A **LA foi mais rápida nas 15 configurações**, nos dois algoritmos. Isso indica que H0' (LA ≈ MA) deve ser rejeitada, mas a confirmação depende do teste estatístico.
- **A vantagem da LA diminui com a densidade.** No BF com V=500, ela cai de 5,8× para 2,6×. É o que a teoria prevê: a MA custa V² por passada em qualquer densidade, e a LA custa proporcional a E.
- Os tempos do Dijkstra são muito pequenos (0,02–4 ms), então o ruído relativo é maior (coeficiente de variação de até ~0,5 em V=100). Se precisar, dá para rodar com `--runs 30`.
- Em todos os 15 arquivos, as distâncias calculadas nos 4 blocos (BF/Dijkstra × LA/MA) deram o mesmo resultado (hash conferido pelo `ExperimentRunner`).

---

## Etapa 0 — Miguel e Juan (antes de dividir) ✅

- [x] Obter o modelo de classes UML (`Graph.pdf`; ver seção abaixo)
- [x] Obter o GTgraph (código-fonte em `GTgraph/`)
- [x] Baixar os **15 grafos do professor**: estão em `RandomGraphSamples/` (`sample<V>-<E>.gr`, por exemplo `sample500-249500.gr`)
- [x] Verificar as arestas (u,v) repetidas nos arquivos do professor: **confirmado** (ver "Cuidados com o GTgraph")
- [x] Conferir nos slides qual representação (LA ou MA) cada algoritmo usa em aula (ver tabela abaixo)
- [x] Interfaces comuns: já definidas pelo UML (`IGraph`, `AdjListGraph`, `AdjMatrixGraph`, `Vertex`, `Edge`, `GraphType`)
- [x] Como o grafo `UNDIRECTED` é guardado: ver "Decisões de projeto" (D1)
- [x] Regra única para arestas duplicadas: ver "Decisões de projeto" (D2)
- [x] Estrutura de pacotes do projeto Java: ver "Decisões de projeto" (D3)
- [x] Quais densidades contam como esparso (Ɛ) e como denso (Ɗ): ver "Decisões de projeto" (D4). Levar ao professor para validar
- [x] Representação do Ford-Fulkerson: ver "Decisões de projeto" (D5). Levar ao professor para validar

### Decisões de projeto

**D1 — Grafo `UNDIRECTED`**
- `addEdge(u, v, w)` cria **um** `Edge(u, v, w)`, que entra em `edges()`. `numEdges()` conta **1** por aresta não-orientada.
- Também cria o espelho `Edge(v, u, w)`, usado só na estrutura interna:
  - **LA:** `adj(u)` recebe `(u,v)` e `adj(v)` recebe `(v,u)`.
  - **MA:** `matrix[u][v] = (u,v)` e `matrix[v][u] = (v,u)`.
- Assim, em qualquer `Edge e` vinda de `adj(x)`, o vizinho é sempre `e.getV()`. Os algoritmos não precisam testar a orientação.
- Kruskal usa `edges()`, sem repetição. Prim, BFS e DFS usam `adj(u)`.

**D2 — Arestas duplicadas: "a última lida vale"**
- Se `addEdge(u, v, w)` encontrar uma aresta (u,v) já existente, ela é **substituída**. A contagem não aumenta, e o mesmo vale para o espelho no caso `UNDIRECTED`.
- **MA:** acontece naturalmente (`matrix[u][v] = novaAresta`), mas `edges()` também precisa ser atualizado.
- **LA:** procura (u,v) em `adj(u)` antes de inserir.
- **Implementado:** o `GraphReader` já remove as duplicatas na leitura (com um `LinkedHashMap`, a última lida vale) e entrega à LA e à MA exatamente as mesmas arestas, na mesma ordem. A regra dentro de `addEdge` continua valendo para quem monta grafos à mão.
- **Importante:** nos arquivos do professor, muitas duplicatas têm **pesos diferentes** (de 203 casos em `sample100-1980` a 90.724 em `sample500-249500`). A regra precisa ser idêntica na LA e na MA, senão os caminhos mínimos dão resultados diferentes. "A última lida vale" depende da ordem das linhas, então o leitor **sempre lê o arquivo na ordem original**.
- Laços (u == v) são aceitos pela estrutura (o UML não proíbe), mas os arquivos do GTgraph não têm nenhum.
- Resultado: LA e MA guardam **exatamente o mesmo grafo**, condição necessária para o experimento.

**D3 — Estrutura do projeto**

```
Trabalho_2/
├── src/
│   ├── graph/        GraphType, IGraph, Vertex, Edge, AdjListGraph, AdjMatrixGraph,
│   │                 GraphReader (.gr → IGraph), GraphPrinter (impressão do grafo)
│   ├── algorithms/   BFS, DFS, TopologicalSort, Kruskal, Prim, BellmanFord, Dijkstra,
│   │                 FloydWarshall, FordFulkerson, DisjointSet (conjuntos disjuntos)
│   ├── examples/     ExampleGraphs (DAG, não-orientado ponderado, rede de fluxo, etc.)
│   ├── experiment/   ExperimentRunner (15 grafos × 2 alg. × 2 repr. × ≥10 execuções → CSV)
│   └── app/          Main (menu interativo)
├── RandomGraphSamples/  os 15 arquivos .gr do professor (sample<V>-<E>.gr)
├── resultados/       CSV gerado pelo experimento
├── analise/          scripts Python (boxplots + testes estatísticos)
└── artigo/           fontes LaTeX
```

Os nomes de classes e pacotes ficam em inglês, para seguir o UML.

**D4 — Esparso (Ɛ) × Denso (Ɗ)**

| Grupo | D nominal | D real (após remover duplicatas) |
|---|---|---|
| **Ɛ — esparso** | 0,2 e 0,4 | ~0,18 e ~0,33 |
| intermediário | 0,6 | ~0,45 |
| **Ɗ — denso** | 0,8 e 1,0 | ~0,55 e ~0,63 |

- O teste das hipóteses H0'/H0'' compara **Ɛ = {0,2; 0,4}** com **Ɗ = {0,8; 1,0}**. Cada grupo fica com 2 densidades × 3 tamanhos × ≥10 execuções.
- O grupo de 0,6 é executado e aparece nos boxplots, mas fica de fora do teste. Isso mantém os grupos equilibrados e separados.
- O artigo deve declarar essa divisão e citar a densidade real (ameaça à validade de construção).

**D5 — Ford-Fulkerson**
- Capacidades numa **MA** (`AdjMatrixGraph`, `DIRECTED`), com o fluxo numa matriz separada `double[][] f`, porque o peso de `Edge` é `final`.
- A capacidade residual é `c(u,v) − f(u,v)` para as arestas diretas e `f(v,u)` para as reversas.
- O caminho aumentador é encontrado com **BFS** (variante Edmonds-Karp, O(V·E²)), o que garante término.
- Entrada: rede de exemplo pequena (por exemplo, a do slide da Lucky Puck), com `s` e `t` informados.

### Representação de cada algoritmo (segundo os slides da disciplina)

| # | Algoritmo | Representação | Grafo exigido | O que os slides dizem |
|---|---|---|---|---|
| 1 | Busca em Largura (BFS) | **LA** | Dirigido ou não, sem peso | "supõe que o grafo de entrada é representado com a utilização de listas de adjacências"; percorre `G.Adj[u]`; O(V + E) |
| 2 | Busca em Profundidade (DFS) | **LA** | Dirigido ou não, sem peso | Percorre `G.Adj[u]`; Θ(V + E) |
| 3 | Ordenação Topológica | **LA** (via DFS) | Dirigido acíclico (DAG) | DFS + inserção no início de lista encadeada; Θ(V + E) |
| 4 | Kruskal | **LA** + lista única de arestas ordenada + conjuntos disjuntos | Não-orientado, ponderado, conexo | "Os dois algoritmos [Kruskal e Prim] consideram que o grafo de entrada é conexo e representado por listas de adjacências"; floresta de conjuntos disjuntos com união pelo posto e compressão de caminho |
| 5 | Prim | **LA** + fila de prioridade mínima | Não-orientado, ponderado, conexo | Percorre `G.Adj[u]`; usa INSERE, EXTRAI-MIN e DIMINUI-CHAVE |
| 6 | Bellman-Ford | **LA** em aula; o trabalho exige **LA e MA** | Dirigido, ponderado | Percorre cada aresta (u, v) ∈ G.E; O(V² + VE). A versão em MA percorre a matriz inteira a cada passada: O(V³) |
| 7 | Dijkstra | **LA** em aula; o trabalho exige **LA e MA** | Dirigido, ponderado, pesos ≥ 0 | Percorre `G.Adj[u]` com fila de prioridade mínima; O((V+E) lg V). A versão em MA usa EXTRAI-MIN linear: O(V²) |
| 8 | Floyd-Warshall | **MA** | Dirigido, ponderado | "W — matriz n×n de pesos (W[i,j] = ∞ se não existir aresta)"; Θ(V³) |
| 9 | Ford-Fulkerson | **Não explícito** → decidido: **MA** de capacidades + BFS (ver D5) | Dirigido, com capacidades ≥ 0, fonte s e sumidouro t, sem laços | Trabalha com `(u,v).f` e `c(u,v)` sobre G.E e com a rede residual Gf; define c(u,v) = 0 se (u,v) ∉ E, que é natural numa MA. Caminho aumentador por BFS (Edmonds-Karp) ou DFS |

> As versões em MA de Bellman-Ford e Dijkstra não aparecem nos slides; a implementação escolhida deve ser descrita no artigo.

### Modelo de classes (UML — `Graph.pdf`)

| Classe | Conteúdo |
|---|---|
| `enum GraphType` | `DIRECTED` ((u,v) ≠ (v,u)) e `UNDIRECTED` ((u,v) == (v,u)) |
| `interface IGraph` | `GraphType type()`, `List<Vertex> vertices()`, `List<Edge> edges()`, `void addEdge(int u, int v)` (peso padrão = 1), `void addEdge(int u, int v, double weight)`, `Edge getEdge(int u, int v)`, `List<Edge> adj(int u)`, `int numVertices()`, `int numEdges()` |
| `AdjListGraph implements IGraph` | `List<Vertex> vertices`, `List<Edge> edges`, `List<List<Edge>> adj`, `GraphType type`; `validateVertex(int u)` privado |
| `AdjMatrixGraph implements IGraph` | `List<Vertex> vertices`, `List<Edge> edges`, `Edge[][] matrix` (`null` = sem aresta), `GraphType type`; `validateVertex(int u)` privado |
| `Vertex` | `final int id`, `String label`; construtores `Vertex(id)` e `Vertex(id, label)` |
| `Edge` | `final Vertex u`, `final Vertex v`, `final double weight`; construtores `Edge(u, v)` e `Edge(u, v, weight)` |
| Exceções | Os construtores lançam `IllegalArgumentException` se `numVertices < 0`; `validateVertex` lança `IndexOutOfBoundsException` se `u < 0 \|\| u >= numVertices()` |

**Consequências para a implementação:**
- **Os vértices começam em 0** no UML, e o arquivo `.gr` começa em 1. O leitor precisa subtrair 1 de cada vértice.
- **`adj(u)` retorna `List<Edge>` nas duas classes.** Na MA, isso varre a linha `matrix[u]`, O(V). Assim, Dijkstra e Bellman-Ford podem ter **um único código** que recebe um `IGraph`. O experimento compara só a representação, o que deixa a comparação justa. **Recomendado.**
- **O peso de `Edge` é `final`.** O Ford-Fulkerson precisa guardar o fluxo fora da aresta, por exemplo numa matriz `double[][] f`. Isso reforça usar MA para ele.
- **Os algoritmos ficam em classes separadas:** `BFS`, `DFS`, `TopologicalSort`, `Kruskal`, `Prim`, `BellmanFord`, `Dijkstra`, `FloydWarshall`, `FordFulkerson` (o PDF de especificações sugere uma classe por algoritmo).

### Cuidados com o GTgraph

O gerador `GTgraph/random`, modelo G(n, m), sorteia `u` e `v` ao acaso **m** vezes (`random/graph.c`, linhas 75–88). Ele **evita laços** (`SELF_LOOPS 0`), mas **não evita arestas repetidas**. A fração esperada de pares distintos é ≈ 1 − e^(−D).

**Verificação feita nos 15 arquivos do professor (`RandomGraphSamples/`).** Todos têm vértices de 1 a V, nenhum laço e pesos inteiros de **0 a 99** (o cabeçalho diz "Max. weight: 100", mas o valor 100 nunca aparece). Os grafos gerados localmente com o GTgraph têm **exatamente as mesmas arestas** (u, v, w), só em outra ordem.

| Arquivo | m (linhas `a`) | Arestas distintas | Duplicadas | Laços | D nominal | **D real** |
|---|---|---|---|---|---|---|
| 100 / 0,2 | 1980 | 1773 | 10,5% | 0 | 0,20 | **0,179** |
| 100 / 0,4 | 3960 | 3266 | 17,5% | 0 | 0,40 | **0,330** |
| 100 / 0,6 | 5940 | 4438 | 25,3% | 0 | 0,60 | **0,448** |
| 100 / 0,8 | 7920 | 5391 | 31,9% | 0 | 0,80 | **0,545** |
| 100 / 1,0 | 9900 | 6215 | 37,2% | 0 | 1,00 | **0,628** |
| 200 / 0,2 | 7960 | 7205 | 9,5% | 0 | 0,20 | **0,181** |
| 200 / 0,4 | 15920 | 13072 | 17,9% | 0 | 0,40 | **0,328** |
| 200 / 0,6 | 23880 | 17929 | 24,9% | 0 | 0,60 | **0,450** |
| 200 / 0,8 | 31840 | 21907 | 31,2% | 0 | 0,80 | **0,550** |
| 200 / 1,0 | 39800 | 25167 | 36,8% | 0 | 1,00 | **0,632** |
| 500 / 0,2 | 49900 | 45108 | 9,6% | 0 | 0,20 | **0,181** |
| 500 / 0,4 | 99800 | 82141 | 17,7% | 0 | 0,40 | **0,329** |
| 500 / 0,6 | 149700 | 112525 | 24,8% | 0 | 0,60 | **0,451** |
| 500 / 0,8 | 199600 | 137330 | 31,2% | 0 | 0,80 | **0,550** |
| 500 / 1,0 | 249500 | 157800 | 36,8% | 0 | 1,00 | **0,632** |

Os valores batem com a fórmula: o grafo "D = 1,0" tem só ~63% das arestas possíveis.

- [x] Confirmar que o gerador produz arestas repetidas (verificado acima)
- [x] Repetir a contagem nos arquivos do professor (resultados idênticos aos da tabela acima)
- [x] Regra única para duplicatas na LA e na MA: **D2**
- [x] Densidade real de cada grafo calculada (tabela acima). Citar no artigo é tarefa do Juan
- [x] **Plano B testado:** o GTgraph compila e gera os 15 grafos. Problemas encontrados e soluções:
  - O `Makefile.var` usa `CC = icc`; trocar para `CC = gcc`.
  - O gcc atual falha na ligação ("múltiplas definições de `primes`"); ligar com `-z muldefs`.
  - Compilar com `-D_FORTIFY_SOURCE=0`.
  - O nome do arquivo de saída (`-o`) deve ter **no máximo 29 caracteres** (`char OUTFILE[30]`); caso contrário, o programa aborta.
  - Uso: `./GTgraph-random -t 1 -n 100 -m 1980 -o g_100_0.2.gr` (não combinar `-c` com as outras opções)

---

## Miguel — Estruturas + Caminho Mínimo + Experimento

### Estruturas
- [x] `GraphType`, `IGraph`, `Vertex`, `Edge` (conforme o UML) — `src/graph/`
- [x] `AdjListGraph` — **Lista de Adjacência (LA)**, `DIRECTED` e `UNDIRECTED`
- [x] `AdjMatrixGraph` — **Matriz de Adjacência (MA)**, `DIRECTED` e `UNDIRECTED`
- [x] Exceções do UML (`IllegalArgumentException`, `IndexOutOfBoundsException`)
- [x] `GraphReader`: lê o `.gr` e devolve um `IGraph` (converte os vértices de 1..V para 0..V−1, mantendo a numeração original no label; aplica a regra de duplicatas D2)
- [x] Teste do leitor: `numEdges()` bate com a coluna "Arestas distintas" nos 15 arquivos
- [x] `GraphPrinter`: resumo, listas de adjacência e matriz de pesos (a matriz só para grafos pequenos)

### Algoritmos
- [x] `BellmanFord`: um único código sobre `IGraph`, executado com `AdjListGraph` e com `AdjMatrixGraph`. Faz sempre as |V|−1 passadas, sem parar antes, fiel ao pseudocódigo
- [x] `Dijkstra`: um único código sobre `IGraph`, com fila de prioridade mínima **indexada** (`IndexedMinPQ`: INSERE, EXTRAI-MIN e DIMINUI-CHAVE, como no slide). LA: O((V+E) lg V); MA: O(V² + E lg V)
- [x] `FloydWarshall` (MA): FLOYD-WARSHALL-COM-PREDECESSOR + IMPRIME-CAMINHO, como no slide
- [x] `IndexedMinPQ` e `ShortestPathResult` (d, π, caminho), reaproveitáveis pelo **Prim** do Juan

### Experimento
- [x] `ExperimentRunner`: carrega os 15 grafos e mede com `System.nanoTime()`, sem contar a leitura do arquivo
- [x] **Cada bloco (arquivo × algoritmo × representação) roda numa JVM nova.** Motivo medido: com LA e MA na mesma JVM, a JIT deixa de especializar `g.adj(u)` e a LA fica ~2× mais lenta (contaminação entre tratamentos). **Citar na metodologia e nas ameaças à validade**
- [x] `AdjListGraph.adj(u)` devolve a lista interna (somente leitura), sem `Collections.unmodifiableList`. Motivo medido: o iterador desse invólucro nem sempre é otimizado pela JIT, e o Bellman-Ford em LA variava até 3× entre JVMs (ex.: `sample200-23880` com 31 ms → ~11 ms após a mudança)
- [x] Aquecimento da JIT em cada bloco: no mínimo 5 execuções e 1 s, descartadas
- [x] 10 execuções medidas por bloco (15 grafos × 2 algoritmos × 2 representações = 60 blocos = 600 medições)
- [x] Blocos em ordem aleatória, com semente fixa 2026 (reduz o efeito de variações de carga da máquina)
- [x] CSV gravado linha a linha em `resultados/resultados_experimento.csv`; log em `resultados/log_experimento.txt`
- [x] Testes de corretude (`tests.CorrectnessTest`, 69 testes): exemplos do Cormen dos slides, ciclo negativo, 200 grafos aleatórios (Dijkstra = Bellman-Ford = Floyd-Warshall, LA = MA) e os 15 arquivos do professor
- [ ] (Bônus) Medir a ocupação de memória
- [ ] Rodar o experimento final com a máquina ociosa (fechar navegador e IDE), para reduzir ruído

### Artigo / Apresentação
- [ ] Introdução
- [ ] Fundamentação teórica (representações e complexidades)
- [ ] Metodologia e planejamento do experimento (escopo Wohlin, fatores A/B, hipóteses, ambiente de execução)
- [ ] Slides de implementação e metodologia

---

## Juan — Demais Algoritmos + Menu + Análise

### Grafos de exemplo
- [ ] DAG (para a Ordenação Topológica)
- [ ] Grafo não-orientado ponderado (para Kruskal e Prim)
- [ ] Rede de fluxo com fonte e sumidouro (para Ford-Fulkerson)
- [ ] Exemplos pequenos para BFS e DFS

### Algoritmos
- [ ] `BFS` — Busca em Largura (LA)
- [ ] `DFS` — Busca em Profundidade (LA)
- [ ] `TopologicalSort` — Ordenação Topológica (LA, via DFS)
- [ ] `Kruskal` (LA + floresta de conjuntos disjuntos com união pelo posto e compressão de caminho)
- [ ] `Prim` (LA + fila de prioridade mínima; pode usar a `algorithms.IndexedMinPQ` já pronta, com INSERE / EXTRAI-MIN / DIMINUI-CHAVE)
- [ ] `FordFulkerson` (MA de capacidades + matriz de fluxo `f[][]` separada, porque o peso de `Edge` é `final`)

### Programa
- [ ] Menu interativo: para cada opção, mostra o grafo de entrada, o nome do algoritmo, a saída e o tempo de execução

### Análise
- [ ] Script Python com os boxplots por algoritmo × representação × configuração (dá para reaproveitar o `graficos.py` do TP1)
- [ ] Tabela com a densidade nominal × densidade real de cada grafo (os valores já estão em "Cuidados com o GTgraph"), citada no artigo na metodologia e nas ameaças à validade
- [ ] Teste de normalidade (Shapiro-Wilk)
- [ ] Teste estatístico (ANOVA de 2 fatores / t ou Mann-Whitney / Wilcoxon)
- [ ] Decisão sobre H0' (Fator A: LA × MA) e H0'' (Fator B: Ɛ × Ɗ)

### Artigo / Apresentação
- [ ] Resultados (boxplots e tabelas)
- [ ] Análise estatística e discussão das hipóteses
- [ ] Ameaças à validade
- [ ] Conclusão
- [ ] Slides de resultados e conclusões

---

## Como compilar e executar

A partir da pasta `Trabalho_2`:

```bash
# compilar
javac -encoding UTF-8 -d out $(find src -name '*.java')

# testes de corretude
java -cp out tests.CorrectnessTest

# experimento (cerca de 3 min; feche outros programas antes)
java -Xms2g -Xmx2g -cp out experiment.ExperimentRunner
#   opções: --runs 10 --warmup 5 --warmup-ms 1000 --source 1 --seed 2026
#           --dir RandomGraphSamples --out resultados/resultados_experimento.csv
```

Colunas do CSV: `arquivo, vertices, arestas_arquivo, arestas_distintas, densidade_nominal, densidade_real, grupo (esparso/intermediario/denso), algoritmo, representacao (LA/MA), ordem_bloco, execucao, tempo_ns, tempo_ms`.

---

## Miguel e Juan (fim)

- [ ] Revisão cruzada do código (cada um revisa o código do outro)
- [ ] Revisão final do artigo (LaTeX/Overleaf) e das referências
- [ ] Ensaio da apresentação
- [ ] (Opcional) Experimentos extras: Kruskal × Prim, Floyd-Warshall em LA × MA, Ford-Fulkerson

---

## Dependências

```
Etapa 0 (UML/IGraph) ──┬──> Miguel: estruturas + leitor ──> Miguel: Bellman-Ford/Dijkstra ──> Miguel: executor + CSV ──┐
                       │                                                                                               ├──> Juan: boxplots + estatística ──> Artigo
                       └──> Juan: demais algoritmos + menu ────────────────────────────────────────────────────────────┘
```

O caminho crítico está com o **Miguel** (estruturas → experimento). Enquanto isso, o **Juan** trabalha em paralelo usando a interface `IGraph` do UML. Para não ficar parado, Juan pode começar com uma versão mínima de `AdjListGraph` combinada com o Miguel.
