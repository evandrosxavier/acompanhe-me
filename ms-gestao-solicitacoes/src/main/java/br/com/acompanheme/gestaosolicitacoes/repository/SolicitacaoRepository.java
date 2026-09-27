package br.com.acompanheme.gestaosolicitacoes.repository;

import br.com.acompanheme.gestaosolicitacoes.model.domain.Solicitacao;
import br.com.acompanheme.gestaosolicitacoes.model.enums.Modalidade;
import br.com.acompanheme.gestaosolicitacoes.model.enums.TipoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface SolicitacaoRepository extends JpaRepository<Solicitacao, UUID> {

    @Query("""
            SELECT s FROM Solicitacao s
            WHERE s.status = br.com.acompanheme.gestaosolicitacoes.model.enums.Status.REGISTRADA
              AND s.tipoSolicitacao = :tipoSolicitacao
              AND (:modalidade IS NULL AND s.modalidade IS NULL OR s.modalidade = :modalidade)
            ORDER BY
                CASE s.prioridade
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.URGENTE THEN 0
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.ALTA THEN 1
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.MEDIA THEN 2
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.BAIXA THEN 3
                END ASC,
                s.dataSolicitacao ASC
            """)
    List<Solicitacao> buscarFilaDeTrabalho(TipoSolicitacao tipoSolicitacao, Modalidade modalidade);

    @Query("""
            SELECT COUNT(s) FROM Solicitacao s
            WHERE s.status = br.com.acompanheme.gestaosolicitacoes.model.enums.Status.EM_FILA
            AND s.tipoSolicitacao = :tipoSolicitacao
            AND (:modalidade IS NULL AND s.modalidade IS NULL OR s.modalidade = :modalidade)
            AND (
                CASE s.prioridade
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.URGENTE THEN 0
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.ALTA THEN 1
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.MEDIA THEN 2
                    WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.BAIXA THEN 3
                END
                < :rankPrioridade
                OR (
                     CASE s.prioridade
                         WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.URGENTE THEN 0
                         WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.ALTA THEN 1
                         WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.MEDIA THEN 2
                         WHEN br.com.acompanheme.gestaosolicitacoes.model.enums.Prioridade.BAIXA THEN 3
                     END
                     = :rankPrioridade
                     AND s.dataEntradaFila < :dataEntradaFilaReferencia
                   )
              )
        """)
    long contarNaFrente(TipoSolicitacao tipoSolicitacao, Modalidade modalidade,
                     int rankPrioridade, LocalDateTime dataEntradaFilaReferencia);

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


    List<Solicitacao> buscarFilaDeEsperaPorTipoEModalidade(TipoSolicitacao tipoSolicitacao,Modalidade modalidade);
}
