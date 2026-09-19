package br.com.rocketseat.service;

import br.com.rocketseat.dto.CompraRequest;
import br.com.rocketseat.entity.Cliente;
import br.com.rocketseat.entity.Compra;
import br.com.rocketseat.entity.Jogo;
import br.com.rocketseat.enums.Genero;
import br.com.rocketseat.exception.ConflitoException;
import br.com.rocketseat.exception.DadosInvalidosException;
import br.com.rocketseat.exception.RecursoNaoEncontradoException;
import br.com.rocketseat.repository.CompraRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompraServiceTest {
    private final CompraRepository repository = mock(CompraRepository.class);
    private final ClienteService clientes = mock(ClienteService.class);
    private final JogoService jogos = mock(JogoService.class);
    private final CompraService service = new CompraService(repository, clientes, jogos);
    private final Cliente cliente = new Cliente("Ana", "ana@example.com");

    @Test
    void rejeitaClienteInexistente() {
        when(clientes.buscarPorId(1L)).thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado."));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.comprar(new CompraRequest(1L, 2L)));
        verifyNoInteractions(repository, jogos);
    }

    @Test
    void rejeitaJogoInexistente() {
        when(clientes.buscarPorId(1L)).thenReturn(cliente);
        when(jogos.buscarPorId(2L)).thenThrow(new RecursoNaoEncontradoException("Jogo não encontrado."));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.comprar(new CompraRequest(1L, 2L)));
        verifyNoInteractions(repository);
    }

    @Test
    void rejeitaCompraRepetida() {
        when(clientes.buscarPorId(1L)).thenReturn(cliente);
        when(jogos.buscarPorId(2L)).thenReturn(new Jogo("Jogo", Genero.RPG, BigDecimal.TEN));
        when(repository.existsByClienteIdAndJogoId(1L, 2L)).thenReturn(true);
        assertThrows(ConflitoException.class, () -> service.comprar(new CompraRequest(1L, 2L)));
        verify(repository, never()).save(any());
    }

    @Test
    void registraPrecoAtualEDataDaCompra() {
        when(clientes.buscarPorId(1L)).thenReturn(cliente);
        when(jogos.buscarPorId(2L)).thenReturn(new Jogo("Jogo", Genero.RPG, new BigDecimal("19.90")));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var compra = service.comprar(new CompraRequest(1L, 2L));
        assertEquals(new BigDecimal("19.90"), compra.valorPago());
        assertNotNull(compra.dataCompra());
        assertEquals("Ana", compra.nomeCliente());
        verify(repository).save(any(Compra.class));
    }

    @Test
    void calculaTotalComValorPagoMesmoSePrecoAtualMudar() {
        Jogo jogo = mock(Jogo.class);
        when(jogo.getPreco()).thenReturn(new BigDecimal("19.90"));
        Compra primeira = new Compra(cliente, jogo);
        when(jogo.getPreco()).thenReturn(new BigDecimal("999.90"));
        Compra segunda = new Compra(cliente, new Jogo("Outro", Genero.ACAO, new BigDecimal("30.10")));
        when(repository.findByClienteIdOrderByDataCompraDesc(1L)).thenReturn(List.of(primeira, segunda));
        assertEquals(new BigDecimal("50.00"), service.totalGasto(1L).totalGasto());
        assertEquals(new BigDecimal("19.90"), primeira.getValorPago());
    }

    @Test
    void clienteSemComprasTemTotalZero() {
        assertEquals(new BigDecimal("0.00"), service.totalGasto(1L).totalGasto());
        assertTrue(service.historico(1L).isEmpty());
    }

    @Test
    void historicoETotalExigemClienteExistente() {
        when(clientes.buscarPorId(1L)).thenThrow(new RecursoNaoEncontradoException("Cliente não encontrado."));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.historico(1L));
        assertThrows(RecursoNaoEncontradoException.class, () -> service.totalGasto(1L));
        verifyNoInteractions(repository);
    }

    @Test
    void rejeitaIdsInvalidos() {
        assertThrows(DadosInvalidosException.class, () -> service.comprar(new CompraRequest(null, 2L)));
        assertThrows(DadosInvalidosException.class, () -> service.comprar(new CompraRequest(1L, 0L)));
        verifyNoInteractions(repository, clientes, jogos);
    }
}
