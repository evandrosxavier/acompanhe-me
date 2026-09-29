package br.com.acompanheme.gestaosolicitacoes.controller;

import br.com.acompanheme.gestaosolicitacoes.repository.SolicitacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import com.jayway.jsonpath.JsonPath;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SolicitacaoControllerTest {

    private static final String URL_CRIACAO = "/consultas/42/solicitacoes";

    private static final String BODY_VALIDO = """
            {
              "paciente": {
                "nome": "Maria Clara Santos", "cpf": "12345678901", "cns": "898000000000000",
                "dataNascimento": "1990-05-20", "telefone": "19987654321", "email": "maria@email.com",
                "endereco": {"municipio": "Campinas", "bairro": "Cambuí"}
              },
              "medico": {"nome": "Dr. João", "crm": "123456-SP", "especialidade": "Cardiologia"},
              "unidadeSolicitante": {"codigo": "2079798", "nome": "UBS Central", "municipio": "Campinas", "bairro": "Cambuí"},
              "tipoSolicitacao": "CIRURGIA",
              "procedimentos": [{"descricaoProcedimento": "Troca valvar"}, {"codigoProcedimento": "0406", "descricaoProcedimento": "Ponte"}],
              "modalidade": "INTERNACAO",
              "prioridade": "ALTA",
              "diagnostico": "Estenose aórtica"
            }
            """;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SolicitacaoRepository repository;

    @Test
    void criaSolicitacaoVinculadaAConsultaDoPath() throws Exception {
        long antes = repository.count();

        String resposta = mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(resposta, "$.id");

        assertThat(repository.count()).isEqualTo(antes + 1);

        mockMvc.perform(get("/solicitacoes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultaId").value(42))
                .andExpect(jsonPath("$.status").value("REGISTRADA"))
                .andExpect(jsonPath("$.paciente.endereco.municipio").value("Campinas"))
                .andExpect(jsonPath("$.procedimentos.length()").value(2))
                .andExpect(jsonPath("$.unidadeExecucao").isEmpty())
                .andExpect(jsonPath("$.dataSolicitacao").isNotEmpty());
    }

    @Test
    void aceitaConsultaIdNoCorpoQuandoIgualAoPath() throws Exception {
        mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO.replace("{\n  \"paciente\"", "{\"consultaId\": 42, \"paciente\"")))
                .andExpect(status().isCreated());
    }

    @Test
    void rejeitaConsultaIdDoCorpoDiferenteDoPath() throws Exception {
        long antes = repository.count();

        mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO.replace("{\n  \"paciente\"", "{\"consultaId\": 99, \"paciente\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Negócio"));

        assertThat(repository.count()).isEqualTo(antes);
    }

    @Test
    void rejeitaCirurgiaSemModalidade() throws Exception {
        mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO.replace("\"modalidade\": \"INTERNACAO\",", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Negócio"));
    }

    @Test
    void rejeitaModalidadeEmSolicitacaoQueNaoECirurgia() throws Exception {
        mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO.replace("\"CIRURGIA\"", "\"CONSULTA\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Negócio"));
    }

    @Test
    void aceitaConsultaSemModalidade() throws Exception {
        mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO.replace("\"CIRURGIA\"", "\"CONSULTA\"")
                                .replace("\"modalidade\": \"INTERNACAO\",", "")))
                .andExpect(status().isCreated());
    }

    @Test
    void rejeitaUrgenteSemMotivoDaUrgencia() throws Exception {
        mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO.replace("\"ALTA\"", "\"URGENTE\"")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de Negócio"));
    }

    @Test
    void aceitaUrgenteComMotivoDaUrgencia() throws Exception {
        mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO.replace("\"ALTA\"", "\"URGENTE\", \"motivoDaUrgencia\": \"Risco de morte\"")))
                .andExpect(status().isCreated());
    }

    @Test
    void negarSemMotivoRetorna400() throws Exception {
        String id = criarERetornarId();
        mockMvc.perform(post("/solicitacoes/{id}/analise", id)).andExpect(status().isOk());

        mockMvc.perform(post("/solicitacoes/{id}/avaliacao", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decisao\": \"NEGADA\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Motivo é obrigatório para negar a solicitação."));
    }

    @Test
    void filaDeEsperaDeCirurgiaExigeModalidade() throws Exception {
        mockMvc.perform(get("/solicitacoes/fila-espera").param("tipo", "CIRURGIA"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/solicitacoes/fila-espera").param("tipo", "CIRURGIA").param("modalidade", "INTERNACAO"))
                .andExpect(status().isOk());
    }

    @Test
    void filaDeEsperaDeConsultaNaoAceitaModalidade() throws Exception {
        mockMvc.perform(get("/solicitacoes/fila-espera").param("tipo", "CONSULTA").param("modalidade", "AMBULATORIAL"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/solicitacoes/fila-espera").param("tipo", "CONSULTA"))
                .andExpect(status().isOk());
    }

    @Test
    void rejeitaCorpoInvalidoComErrosDeValidacao() throws Exception {
        String invalido = BODY_VALIDO.replace("\"cpf\": \"12345678901\"", "\"cpf\": \"123\"")
                .replace("\"procedimentos\": [{\"descricaoProcedimento\": \"Troca valvar\"}, {\"codigoProcedimento\": \"0406\", \"descricaoProcedimento\": \"Ponte\"}],", "\"procedimentos\": [],");

        mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation_errors.length()").value(2));
    }

    @Test
    void rejeitaEnumInvalido() throws Exception {
        mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO.replace("\"ALTA\"", "\"MUITO_ALTA\"")))
                .andExpect(status().isBadRequest());
    }

    private String criarERetornarId() throws Exception {
        String resposta = mockMvc.perform(post(URL_CRIACAO).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    @Test
    void buscaSolicitacaoPorId() throws Exception {
        String id = criarERetornarId();

        mockMvc.perform(get("/solicitacoes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.paciente.nome").value("Maria Clara Santos"))
                .andExpect(jsonPath("$.procedimentos.length()").value(2));
    }

    @Test
    void buscaInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/solicitacoes/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void canceladaUmaVezEConflitoNaSegunda() throws Exception {
        String id = criarERetornarId();

        mockMvc.perform(post("/solicitacoes/{id}/cancelamento", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADA"));

        mockMvc.perform(post("/solicitacoes/{id}/cancelamento", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").isNotEmpty());
    }

    @Test
    void cancelarInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/solicitacoes/{id}/cancelamento", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
