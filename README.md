# Acompanhe-me

> 📂 Repositório: [https://github.com/evandrosxavier/acompanhe-me](https://github.com/evandrosxavier/acompanhe-me)

Plataforma backend de rastreabilidade das solicitações reguladas do SUS (consultas com especialista, exames de alta complexidade e cirurgias), composta por microsserviços com comunicação assíncrona via Apache Kafka.

Hackathon Fase 5 — PosTech FIAP, Arquitetura e Desenvolvimento Java.

---

## O problema

Quando um médico do SUS conclui, durante uma consulta, que o paciente precisa de uma cirurgia, de um exame de alta complexidade ou de um especialista, essa solicitação sai da sala de atendimento e entra em um espaço que o paciente não enxerga.

Ele não sabe se o pedido foi registrado, em que posição está na fila, se foi autorizado ou por que foi negado. Para descobrir, precisa se deslocar até uma central de regulação.

A opacidade não é só um inconveniente. Ela é a condição que permite que pedidos em papel sejam esquecidos em gavetas antes da digitação, que solicitações sejam registradas com atraso deliberado, e que a ordem da fila seja alterada sem deixar rastro.

Alguns números que dimensionam o problema:

- **24%** da população aponta o tempo de espera por uma resposta como a principal razão do mau atendimento no SUS — mais que falta de recursos (15%) ou de médicos (10%). *(Datafolha/CFM)*
- **1,3 milhão** de pessoas aguardam cirurgia eletiva, com espera média de dois anos e quatro meses.
- O Enunciado nº 98 do Fórum Nacional de Saúde do CNJ considera excessiva a espera acima de **100 dias** para consultas e exames e **180 dias** para cirurgias. Não há instrumento que torne esse prazo visível ao paciente ou ao gestor.
- **657.473** novos processos judiciais de saúde em 2024, com **73%** das liminares deferidas. *(CNJ)*
- A fila de transplantes é hoje a **única** fila do SUS rastreável pelo cidadão.

---

## A proposta

Uma plataforma de backend que digitaliza a solicitação no momento em que ela nasce — dentro da consulta, sem etapa intermediária de digitação — e acompanha seu ciclo de vida até a conclusão.

- **Para o paciente:** sai da consulta com a solicitação já protocolada, consulta sua posição na fila e recebe notificação por e-mail a cada mudança de estado.
- **Para o profissional:** registra a solicitação de forma estruturada, vinculada à consulta de origem.
- **Para o gestor:** enxerga as filas por tipo e modalidade e audita toda alteração de prioridade, com autor e justificativa.

### Conceito central

Consulta com especialista, exame e cirurgia não são três funcionalidades. São três tipos de um mesmo conceito: a **solicitação regulada**. Todas nascem de uma decisão clínica, passam por avaliação da regulação, entram em fila e precisam de visibilidade. O sistema modela isso uma vez, então incluir um novo tipo é uma extensão, e não um novo subsistema.

---

## Arquitetura

```
┌──────────────────────────┐
│   ms-gestao-consultas    │  Consulta onde a solicitação nasce
│       (porta 8081)       │  REST + JWT
└──────────────────────────┘
             │ consultaId
             ▼
┌──────────────────────────┐   tópico: solicitacao-eventos   ┌──────────────────────────┐
│  ms-gestao-solicitacoes  │ ──────────────────────────────► │      ms-notificacao      │
│       (porta 8082)       │                                 │       (porta 8084)       │
│   REST API + Swagger UI  │                                 │   E-mail ao paciente     │
└──────────────────────────┘                                 └──────────────────────────┘
             │
             │ tópico: solicitacao-eventos
             ▼
┌──────────────────────────┐
│   ms-hist-solicitacoes   │
│       (porta 8083)       │
│    GraphQL + GraphiQL    │
└──────────────────────────┘
```

Cada microsserviço possui seu **próprio banco PostgreSQL**. O Kafka é compartilhado e sobe pelo `docker-compose.yml` da raiz. O `ms-notificacao` e o `ms-hist-solicitacoes` usam **grupos de consumo diferentes**, então os dois recebem todos os eventos.

---

## Microsserviços

### ms-gestao-solicitacoes — porta 8082
Serviço central. Registra as solicitações, controla o ciclo de vida (análise, avaliação, fila, oferta de local, realização) e publica um evento no tópico `solicitacao-eventos` a cada mudança.

### ms-hist-solicitacoes — porta 8083
Consome os eventos e grava um registro de auditoria por evento. Os registros **nunca são alterados**. Expõe a linha do tempo via GraphQL.

### ms-notificacao — porta 8084
Consome os eventos e envia e-mail ao paciente a cada mudança de estado. Em ambiente local, os e-mails são capturados pelo **Mailpit** (`http://localhost:8025`).

### ms-gestao-consultas — porta 8081
Cadastro de pacientes, profissionais e consultas, com autenticação JWT. Representa a consulta onde a solicitação nasce.

---

## Ciclo de vida da solicitação

```
REGISTRADA ──► EM_ANALISE ──► EM_FILA ──► LOCAL_DEFINIDO ──► CONFIRMADA ──► CONCLUIDA
                  │  ▲                         │
                  │  │ complemento             │ paciente recusa o local
                  ▼  │                         ▼
         PENDENTE_DOCUMENTACAO             EM_FILA (mantém a posição)

EM_ANALISE ──► NEGADA
Qualquer estado antes de CONFIRMADA ──► CANCELADA
```

| Status | Significado |
|---|---|
| `REGISTRADA` | Solicitação criada na consulta, aguardando a regulação |
| `EM_ANALISE` | Um analista da regulação assumiu a solicitação |
| `PENDENTE_DOCUMENTACAO` | Devolvida para complemento; volta para análise quando complementada |
| `NEGADA` | Negada pela regulação, com motivo obrigatório |
| `EM_FILA` | Aprovada. Só a partir daqui existe posição na fila |
| `LOCAL_DEFINIDO` | Unidade de execução ofertada ao paciente |
| `CONFIRMADA` | Paciente aceitou o local ofertado |
| `CONCLUIDA` | Atendimento realizado |
| `CANCELADA` | Cancelada, com o histórico preservado |

### Regras de fila

- **Tipos:** `CONSULTA`, `EXAME`, `CIRURGIA`.
- **Modalidade:** obrigatória para `CIRURGIA` (`AMBULATORIAL`, `DAY_CLINIC`, `INTERNACAO`) e cada modalidade é uma fila própria. Não se aplica a `CONSULTA` e `EXAME`.
- **Ordenação:** prioridade (`URGENTE` → `ALTA` → `MEDIA` → `BAIXA`) e, em empate, data de entrada na fila.
- **Recusa de local:** a solicitação volta para `EM_FILA` **sem perder a posição**.
- **Alteração de prioridade:** exige justificativa e gera o evento `SOLICITACAO_PRIORIDADE_ALTERADA`, com prioridade anterior, nova e autor, para auditoria.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.16 |
| Spring Data JPA | Hibernate |
| Spring for Apache Kafka | Producer / Consumer |
| Spring GraphQL | GraphiQL habilitado |
| Spring Security + java-jwt | ms-gestao-consultas |
| Spring Mail + Mailpit | ms-notificacao |
| PostgreSQL | 16 |
| Apache Kafka (Confluent) | 7.5.0 |
| Docker / Docker Compose | — |
| Lombok / MapStruct | — |
| SpringDoc OpenAPI (Swagger) | ms-gestao-solicitacoes, ms-gestao-consultas |

---

## Decisões Técnicas

| Tema | Decisão | Justificativa |
|---|---|---|
| Modelo de domínio | Uma entidade `Solicitacao` com `TipoSolicitacao` | Consulta, exame e cirurgia compartilham o mesmo ciclo de vida; novo tipo é extensão |
| Transições de estado | Validadas na própria entidade | Transição inválida retorna **409** e nunca chega ao banco |
| Posição na fila | Calculada só a partir de `EM_FILA` | Antes disso a solicitação está sob avaliação e não tem ordenação |
| Recusa de oferta | Mantém a data de entrada na fila | Recusar um local distante não pune o paciente |
| Mensageria | Kafka, um tópico `solicitacao-eventos` | Vários consumidores independentes para o mesmo evento; chave = id da solicitação preserva a ordem |
| Histórico | Registros imutáveis, um por evento | Trilha de auditoria: nenhuma alteração de fila ou prioridade some |
| API de histórico | GraphQL | Cada cliente busca só os campos de que precisa |
| Banco de dados | PostgreSQL por serviço | Isolamento e disponibilidade independente |
| Identificador | UUID | Não expõe sequência nem volume de solicitações |

---

## Tópico Kafka

| Tópico | Publicado por | Consumidores |
|---|---|---|
| `solicitacao-eventos` | ms-gestao-solicitacoes | ms-hist-solicitacoes, ms-notificacao |

Tipos de evento (`tipoEvento`):

`SOLICITACAO_REGISTRADA`, `SOLICITACAO_EM_ANALISE`, `SOLICITACAO_DEVOLVIDA`, `SOLICITACAO_NEGADA`, `SOLICITACAO_APROVADA`, `SOLICITACAO_PRIORIDADE_ALTERADA`, `SOLICITACAO_LOCAL_DEFINIDO`, `SOLICITACAO_OFERTA_ACEITA`, `SOLICITACAO_OFERTA_RECUSADA`, `SOLICITACAO_CANCELADA`, `SOLICITACAO_CONCLUIDA`.

---

## Endpoints — ms-gestao-solicitacoes (porta 8082)

> Documentação interativa: `http://localhost:8082/swagger-ui.html`

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/consultas/{consultaId}/solicitacoes` | Criar solicitação vinculada à consulta (status `REGISTRADA`) |
| GET | `/solicitacoes/{id}` | Buscar solicitação por ID |
| GET | `/solicitacoes/fila-trabalho?tipo=&modalidade=` | Solicitações `REGISTRADA` aguardando a regulação |
| GET | `/solicitacoes/fila-espera?tipo=&modalidade=` | Fila de espera (`EM_FILA`) na ordem de atendimento |
| GET | `/solicitacoes/{id}/posicao` | Posição na fila e tamanho total da fila |
| POST | `/solicitacoes/{id}/analise` | Iniciar análise |
| POST | `/solicitacoes/{id}/avaliacao` | Avaliar: aprovada, negada ou pendente de documentação |
| PATCH | `/solicitacoes/{id}/complemento` | Documentação complementada, volta para análise |
| PATCH | `/solicitacoes/{id}/prioridade` | Alterar prioridade (justificativa obrigatória) |
| POST | `/solicitacoes/{id}/oferta` | Definir a unidade de execução ofertada |
| POST | `/solicitacoes/{id}/resposta` | Paciente aceita ou recusa o local |
| POST | `/solicitacoes/{id}/cancelamento` | Cancelar solicitação |
| POST | `/solicitacoes/{id}/realizacao` | Registrar realização (data do atendimento opcional) |

### Respostas de erro

| Situação | Status |
|---|---|
| Dados inválidos ou enum inválido | 400 |
| Solicitação não encontrada | 404 |
| Transição inválida para o status atual | 409 |
| Consulta de posição fora de `EM_FILA` | 409 |

---

## Queries GraphQL — ms-hist-solicitacoes (porta 8083)

> Interface interativa: `http://localhost:8083/graphiql`

| Query | Parâmetros | Descrição |
|---|---|---|
| `historicoDaSolicitacao` | `solicitacaoId!` | Linha do tempo completa, do evento mais antigo ao mais recente |
| `historicoDoPaciente` | `cpf!`, `page`, `size` | Eventos de todas as solicitações do paciente (CPF com ou sem máscara) |

**Exemplo — linha do tempo de uma solicitação:**
```graphql
query {
  historicoDaSolicitacao(solicitacaoId: "<uuid-da-solicitacao>") {
    tipoEvento
    statusAtual
    prioridadeAnterior
    prioridadeNova
    autor
    motivo
    unidadeExecucaoNome
    dataEvento
  }
}
```

**Exemplo — histórico de um paciente:**
```graphql
query {
  historicoDoPaciente(cpf: "123.456.789-01", page: 0, size: 10) {
    solicitacaoId
    tipoEvento
    statusAtual
    dataEvento
  }
}
```

---

## Configuração e Execução

### Pré-requisitos
- Docker e Docker Compose
- Java 21
- Maven 3.9+
- (Opcional) [Postman](https://www.postman.com/)

### Collection Postman

A collection com os endpoints de `ms-gestao-solicitacoes` e `ms-hist-solicitacoes` está em [postman/acompanhe-me.postman_collection.json](postman/acompanhe-me.postman_collection.json).

No Postman: **Import** → selecione o arquivo.

### 1. Subir a infraestrutura

**Primeiro o Kafka**, na raiz do projeto:
```bash
docker compose up -d
```

Depois, os bancos de cada serviço (e o Mailpit, junto com o banco do ms-notificacao):
```bash
cd ms-gestao-solicitacoes && docker compose up -d && cd ..
cd ms-hist-solicitacoes   && docker compose up -d && cd ..
cd ms-notificacao         && docker compose up -d && cd ..
cd ms-gestao-consultas    && docker compose up -d && cd ..
```

### 2. Executar os microsserviços

Em terminais separados, a partir da raiz:

```bash
./mvnw -pl ms-gestao-solicitacoes spring-boot:run
./mvnw -pl ms-hist-solicitacoes spring-boot:run
./mvnw -pl ms-notificacao spring-boot:run
./mvnw -pl ms-gestao-consultas spring-boot:run
```

### 3. Verificar os serviços

| Serviço | URL |
|---|---|
| ms-gestao-solicitacoes (Swagger) | http://localhost:8082/swagger-ui.html |
| ms-hist-solicitacoes (GraphiQL) | http://localhost:8083/graphiql |
| Mailpit (e-mails enviados) | http://localhost:8025 |
| ms-gestao-consultas (Swagger) | http://localhost:8081/swagger-ui.html |

> O `ms-gestao-consultas` cria um usuário admin na primeira execução: login `admin`, senha `Admin@123`.

---

## Bancos de Dados

| Serviço | Container | Porta | Banco |
|---|---|---|---|
| ms-gestao-consultas | agende-me-db | 5432 | agendeme_db |
| ms-notificacao | agende-me-notificacoes-db | 5433 | agendeme_notificacoes_db |
| ms-gestao-solicitacoes | acompanhemesolicitacoes-db | 5434 | solicitacoes_db |
| ms-hist-solicitacoes | acompanheme-historico-db | 5435 | historico_solicitacoes_db |

Credenciais padrão: `admin` / `admin`

---

## Estrutura do Repositório

```
acompanhe-me/
├── README.md
├── docker-compose.yml          # Zookeeper + Kafka (compartilhado)
├── pom.xml                     # POM pai (multi-módulo)
├── postman/                    # Collection de testes
├── ms-gestao-solicitacoes/     # Serviço central (REST + Kafka Producer)
├── ms-hist-solicitacoes/       # Histórico/auditoria (GraphQL + Kafka Consumer)
├── ms-notificacao/             # E-mails ao paciente (Kafka Consumer)
└── ms-gestao-consultas/        # Consultas de origem (REST + JWT)
```

---

## Padrões e referências

- **SIGTAP** — Tabela de Procedimentos, Medicamentos e OPM do SUS (Portaria GM 321/2007).
- **CID-10** — codificação de diagnóstico.
- **CNS/CPF** — identificação do paciente.

## Posicionamento

O Meu SUS Digital oferece agendamento de consultas em municípios habilitados, histórico de exames e vacinas e acompanhamento da fila de transplantes. Esta proposta não substitui o aplicativo oficial: entrega o motor de backend para a etapa que hoje não existe para o cidadão — a solicitação regulada rastreável — expondo APIs consumíveis por qualquer front-end.

## Próximos passos

- Estender a autenticação JWT do `ms-gestao-consultas` para `ms-gestao-solicitacoes` e `ms-hist-solicitacoes`, com perfis (médico, regulador, paciente, gestor).
- Codificação SIGTAP e CID-10 na criação da solicitação.
- Alerta de prazo excedido (100 dias para consultas/exames, 180 dias para cirurgias).

---

**Autor:** Evandro Santos Xavier
