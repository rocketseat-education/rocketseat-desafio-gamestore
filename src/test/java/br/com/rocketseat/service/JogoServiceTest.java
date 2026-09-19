package br.com.rocketseat.service;

import br.com.rocketseat.dto.JogoRequest;
import br.com.rocketseat.enums.Genero;
import br.com.rocketseat.exception.DadosInvalidosException;
import br.com.rocketseat.exception.RecursoNaoEncontradoException;
import br.com.rocketseat.repository.JogoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JogoServiceTest {
    private final JogoRepository repository = mock(JogoRepository.class);
    private final JogoService service = new JogoService(repository);

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "0.001", "10000000000"})
    void rejeitaPrecoInvalido(String preco) {
        assertThrows(DadosInvalidosException.class,
                () -> service.cadastrar(new JogoRequest("Jogo", Genero.RPG, new BigDecimal(preco))));
        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    void rejeitaTituloVazio(String titulo) {
        assertThrows(DadosInvalidosException.class,
                () -> service.cadastrar(new JogoRequest(titulo, Genero.RPG, BigDecimal.TEN)));
        verifyNoInteractions(repository);
    }

    @Test
    void rejeitaPrecoOuGeneroAusente() {
        assertThrows(DadosInvalidosException.class,
                () -> service.cadastrar(new JogoRequest("Jogo", Genero.RPG, null)));
        assertThrows(DadosInvalidosException.class,
                () -> service.cadastrar(new JogoRequest("Jogo", null, BigDecimal.TEN)));
    }

    @Test
    void informaJogoInexistente() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void informaAusenciaDeJogoMaisCaro() {
        assertThrows(RecursoNaoEncontradoException.class, service::maisCaro);
    }
}
