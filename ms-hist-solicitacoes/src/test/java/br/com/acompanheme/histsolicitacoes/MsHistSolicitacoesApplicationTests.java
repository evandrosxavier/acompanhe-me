package br.com.acompanheme.histsolicitacoes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Listener desligado: o teste não deve consumir (nem marcar como lidas) as mensagens reais do tópico.
@SpringBootTest(properties = "hist.kafka.listener.auto-startup=false")
class MsHistSolicitacoesApplicationTests {

	@Test
	void contextLoads() {
	}

}
