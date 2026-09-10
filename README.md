# acompanhe-me

Plataforma de rastreabilidade das solicitações reguladas do SUS.

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

O que muda:

**Para o paciente.** Sai da consulta com a solicitação já protocolada e visível. Consulta sua posição na fila e recebe notificação a cada mudança de estado.

**Para o profissional.** Registra a solicitação de forma estruturada e codificada, sem ambiguidade de interpretação. Acompanha o que aconteceu com os pedidos que originou.

**Para o gestor.** Enxerga demanda real por especialidade e unidade, identifica solicitações com prazo excedido e audita alterações de prioridade.

---

## Conceito central

Cirurgia, exame de alta complexidade e encaminhamento para especialista não são três funcionalidades. São três tipos de um mesmo conceito: a **solicitação regulada**.

Todas nascem de uma decisão clínica, passam por avaliação de um regulador, entram em fila e precisam de visibilidade. O sistema modela isso uma vez — o que torna a inclusão de um novo tipo uma extensão, e não um novo subsistema.

### Ciclo de vida

```
registrada → em triagem → avaliada → em fila → concluída
```

O estado terminal se desdobra em realizada, cancelada, não comparecimento ou negada — a distinção é o que gera informação de gestão.

A posição na fila só existe a partir do estado `em fila`. Antes disso a solicitação está sob avaliação e não possui ordenação.

### Fila e vaga

Fila e vaga são coisas distintas. A fila é única por município e ordenada por prioridade clínica e tempo de espera. A unidade executora entra apenas no momento da oferta de vaga, percorrendo a fila em ordem.

Recusas por distância são registradas sem perda de posição, e se tornam indicador de desequilíbrio de capacidade entre regiões.

---

## Padrões e referências

- **SIGTAP** — Tabela de Procedimentos, Medicamentos e OPM do SUS, obrigatória para os sistemas de informação da atenção à saúde (Portaria GM 321/2007).
- **CID-10** — codificação de diagnóstico.
- **CNS/CPF** — identificação do paciente, permitindo consulta do histórico em qualquer unidade da rede.

---

## Base

O projeto evolui a arquitetura do [agende-me](https://github.com/evandrosxavier/agende-me), sistema de agendamento e gestão de consultas em microsserviços com comunicação assíncrona.

---

## Posicionamento

O Meu SUS Digital oferece hoje agendamento de consultas em municípios habilitados, histórico de exames e vacinas, e acompanhamento da fila de transplantes. O prontuário unificado via RNDS está em implantação.

Esta proposta não substitui o aplicativo oficial. Entrega o motor de backend para a etapa que hoje não existe para o cidadão — a solicitação regulada rastreável — expondo APIs consumíveis por qualquer front-end.

---

## Status

Em desenvolvimento. Entrega prevista para 29/09/2026.

---

**Autor:** Evandro Santos Xavier
