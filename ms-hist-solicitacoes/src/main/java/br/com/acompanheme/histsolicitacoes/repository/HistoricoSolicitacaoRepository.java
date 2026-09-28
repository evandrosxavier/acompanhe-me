package br.com.acompanheme.histsolicitacoes.repository;

import br.com.acompanheme.histsolicitacoes.model.HistoricoSolicitacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Estende Repository (e não JpaRepository) de propósito: só expõe inserção e leitura,
 * sem métodos de update ou delete.
 */
public interface HistoricoSolicitacaoRepository extends Repository<HistoricoSolicitacao, UUID> {

    HistoricoSolicitacao save(HistoricoSolicitacao historico);

    List<HistoricoSolicitacao> findBySolicitacaoIdOrderByDataEventoAscDataRegistroAsc(UUID solicitacaoId);

    Page<HistoricoSolicitacao> findByPacienteCpf(String pacienteCpf, Pageable pageable);
}
