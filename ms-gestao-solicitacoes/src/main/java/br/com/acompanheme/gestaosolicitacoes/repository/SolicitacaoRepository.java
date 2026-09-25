package br.com.acompanheme.gestaosolicitacoes.repository;

import br.com.acompanheme.gestaosolicitacoes.model.domain.Solicitacao;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Modalidade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Status;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, UUID> {

    List<Solicitacao> findByTipoSolicitacaoAndStatusOrderByDataSolicitacaoAsc(
            TipoSolicitacao tipoSolicitacao, Status status);

    @Query("""
            SELECT s FROM Solicitacao s
            WHERE s.status = br.com.acompanheme.gestaosolicitacoes.model.enums.Status.EM_FILA
              AND s.tipoSolicitacao = :tipoSolicitacao
              AND (:modalidade IS NULL AND s.modalidade IS NULL OR s.modalidade = :modalidade)
            ORDER BY
                CASE s.prioridade
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.URGENTE THEN 0
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.ALTA THEN 1
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.MEDIA THEN 2
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.BAIXA THEN 3
                END ASC,
                s.dataEntradaFila ASC
            """)
    List<Solicitacao> buscarFilaDeEsperaPorTipoEModalidade(
            @Param("tipoSolicitacao") TipoSolicitacao tipoSolicitacao,
            @Param("modalidade") Modalidade modalidade);
}
