# 📊 Sistema de Monitoramento do PGD na ANTT

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?logo=openjdk" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen?logo=springboot" alt="Spring Boot 3.5.14">
  <img src="https://img.shields.io/badge/Build-Maven-blue?logo=apachemaven" alt="Maven">
</p>

Sistema web desenvolvido para apoiar o monitoramento de Planos de Trabalho no âmbito do **PGD (Programa de Gestão e Desempenho)**, consolidando dados de servidores, unidades e planos em uma interface unificada de consulta, acompanhamento de pendências e exportação de relatórios.

> Projeto desenvolvido para uso interno da Coordenação de Frequência e PGD (CFPGD), com foco em automatizar consultas que hoje são feitas manualmente.

---

## 📋 Sobre o projeto

O PGD permite que servidores executem suas atividades de forma remota, mediante planos de trabalho com metas e prazos definidos. O sistema de PGD atualmente utilizado pela ANTT não conta com mecanismos efetivos de extração de relatórios e visualização consolidada desses dados — é justamente essa lacuna que este projeto resolve.

O sistema centraliza essas informações, extraídas da própria API do sistema de PGD da ANTT, aplicando regras de negócio para classificar automaticamente o status de execução de cada plano, identificar pendências de avaliação (por parte da chefia) e pendências de execução (por parte do servidor), além de permitir consultas por servidor, por período e exportação dos dados em CSV.

---

## ⚙️ Funcionalidades

- 🔄 **Importação automática** de usuários e planos a partir da API do sistema de PGD da ANTT (executada na inicialização, em ambiente de produção)
- 🔍 **Consulta de planos por servidor** (via matrícula SIAPE)
- 📅 **Consulta de planos por período** (mês/ano de vigência)
- ⏳ **Pendências do servidor** — planos com execução incompleta
- 👔 **Pendências da chefia** — planos completos aguardando avaliação
- 📤 **Exportação em CSV** dos relatórios de pendência
- 📄 **Paginação** dos resultados agregados
- 🌐 **Endpoints REST complementares** para as mesmas consultas de dados, expostos em JSON (exportação CSV disponível apenas nas telas)

---

## 🏗️ Arquitetura

O projeto segue separação por camadas, com responsabilidades isoladas:

```
controllers/
  ├── view/        → Controllers que retornam páginas Thymeleaf
  └── rest/        → Controllers que retornam JSON (API REST)
services/
  ├── imports/     → Sincronização com a API do sistema de PGD da ANTT
  └── responses/   → Regras de negócio e agregação dos dados para exibição
repositories/      → Acesso a dados via Spring Data JPA
entities/          → Entidades JPA (Usuario, Plano)
dtos/
  ├── imports/     → Mapeiam o JSON externo, tratando datas inválidas, campos vazios e tipos inconsistentes vindos da fonte
  ├── responses/   → DTOs agregados para as telas, combinando unidades, servidores e planos em diferentes hierarquias conforme a consulta
  └── reports/     → DTO específico para exportação CSV
mappers/           → Conversão Entity → DTO
enums/             → Status de execução e tipos de perfil
utils/             → Regras de data/período centralizadas
```

**Decisões técnicas relevantes:**
- `JOIN FETCH` nas queries de relacionamento, evitando o problema de N+1 nas consultas agregadas.
- `equals`/`hashCode` das entidades restrito ao `id` (`@EqualsAndHashCode(onlyExplicitlyIncluded = true)`), evitando os efeitos colaterais clássicos de identidade mutável em entidades JPA.
- DTOs de importação com setters customizados, tratando datas inválidas, campos vazios e tipos heterogêneos vindos da API de origem (ex: campo que ora retorna array, ora retorna boolean).
- Toda a camada de visualização usa `th:text` (nunca `th:utext`), prevenindo XSS por padrão.

---

## 🧰 Tech Stack

| Categoria | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.5.14 |
| Persistência | Spring Data JPA + H2 Database |
| Front-end | Thymeleaf + AdminLTE 3 (Bootstrap 4) |
| Exportação | OpenCSV 5.12.0 |
| Boilerplate | Lombok 1.18.34 |
| Utilitários | Apache Commons Lang3 3.12.0 |
| Build | Maven |

---

## 🌐 Endpoints

### Telas (Thymeleaf)

| Método | Rota | Descrição |
|---|---|---|
| GET | `/view/painel` | Página inicial (futura construção) |
| GET | `/view/planos/por-servidor` | Busca de planos por matrícula |
| GET | `/view/planos/filtro-periodo` | Tela de filtro (redireciona para período/pendências) |
| GET | `/view/planos/por-periodo` | Planos filtrados por mês/ano de vigência |
| GET | `/view/planos/pendencia-servidor` | Planos com execução pendente (paginado) |
| GET | `/view/planos/pendencia-chefia` | Planos aguardando avaliação da chefia (paginado) |
| GET | `/view/planos/pendencia-chefia?format=csv` | Exporta as pendências da chefia em CSV |

### API REST

| Método | Rota | Descrição |
|---|---|---|
| GET | `/rest/planos/por-servidor/{matricula}` | Planos de um servidor específico |
| GET | `/rest/planos/por-periodo` | Planos filtrados por período |
| GET | `/rest/planos/pendencia-chefia` | Pendências da chefia (paginado, JSON) |
| GET | `/rest/planos/pendencia-servidor` | Pendências do servidor (paginado, JSON) |

---

## 🔑 Variáveis de ambiente

O perfil `prod` depende das seguintes variáveis (nenhuma possui valor hardcoded no projeto): `DB_URL`, `DB_USER`, `DB_PASSWORD`, `URL_USUARIOS`, `URL_PLANOS_CORRENTES` e `URL_PLANOS_ARQUIVADOS`. Esse perfil é usado apenas internamente na ANTT, já que depende de endpoints internos não acessíveis externamente.

---

## ✅ Pré-requisitos

- **JDK 17** instalado, com a variável de ambiente `JAVA_HOME` configurada corretamente.

---

## 🚀 Como executar localmente

```bash
git clone https://github.com/marcosmaiacarvalho/pgd-monitoramento.git
cd pgd-monitoramento
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

O perfil `test` popula o banco H2 em memória com dados fictícios (`data-test.sql`), sem precisar de nenhuma variável de ambiente.

---

## 🗺️ Roadmap / Próximos passos

Itens já identificados como melhoria futura, documentados aqui para rastreabilidade:

- [ ] Migrar a paginação de listas agregadas para o padrão de duas etapas (buscar IDs paginados no banco → buscar dados completos com `JOIN FETCH` via `WHERE id IN`), eliminando a paginação em memória atual.
- [ ] Aplicar `@CsvNumber` com locale `pt_BR` nos campos decimais do relatório CSV.
- [ ] Criar exceptions customizadas + `@ControllerAdvice` para tratamento global de erros (hoje há uso pontual de `RuntimeException` genérica).
- [ ] Adicionar suíte de testes automatizados (JUnit 5 + Mockito).
- [ ] Avaliar uso de uma business key (matrícula SIAPE) no `equals`/`hashCode`, quando a fonte de dados externa garantir consistência desse campo.

---

## 👤 Autor

**Marcos Vinícius Maia Carvalho**<br>
Desenvolvido como parte das atividades de modernização de processos da CFPGD/ANTT.

[LinkedIn](https://www.linkedin.com/in/marcosmaiacv) · [GitHub](https://github.com/marcosmaiacarvalho)
