package br.com.rocketseat.service;

import br.com.rocketseat.entity.Cliente;
import br.com.rocketseat.entity.Compra;
import br.com.rocketseat.entity.Jogo;
import br.com.rocketseat.enums.Genero;
import br.com.rocketseat.repository.CompraRepository;
import br.com.rocketseat.repository.JogoRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RelatorioServiceTest {
    private final JogoRepository jogos = mock(JogoRepository.class);
    private final CompraRepository compras = mock(CompraRepository.class);
    private final RelatorioService service = new RelatorioService(jogos, compras);

    @Test
    void generosNaoSeRepetem() {
        when(jogos.findAll()).thenReturn(List.of(
                new Jogo("A", Genero.RPG, BigDecimal.TEN),
                new Jogo("B", Genero.RPG, BigDecimal.ONE),
                new Jogo("C", Genero.ACAO, BigDecimal.TEN)));
        assertEquals(Set.of(Genero.RPG, Genero.ACAO), service.generos());
    }

    @Test
    void contaComprasPorGenero() {
        Cliente ana = new Cliente("Ana", "ana@example.com");
        Cliente bia = new Cliente("Bia", "bia@example.com");
        Jogo rpg = new Jogo("RPG", Genero.RPG, BigDecimal.TEN);
        Jogo acao = new Jogo("Ação", Genero.ACAO, BigDecimal.ONE);
        when(compras.findAll()).thenReturn(List.of(
                new Compra(ana, rpg), new Compra(bia, rpg), new Compra(ana, acao)));
        assertEquals(Map.of(Genero.RPG, 2L, Genero.ACAO, 1L), service.vendasPorGenero());
    }

    @Test
    void retornaColecoesVaziasSemDados() {
        assertTrue(service.generos().isEmpty());
        assertTrue(service.vendasPorGenero().isEmpty());
    }
}
