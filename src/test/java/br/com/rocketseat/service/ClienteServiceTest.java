package br.com.rocketseat.service;

import br.com.rocketseat.dto.ClienteRequest;
import br.com.rocketseat.entity.Cliente;
import br.com.rocketseat.exception.ConflitoException;
import br.com.rocketseat.exception.DadosInvalidosException;
import br.com.rocketseat.exception.RecursoNaoEncontradoException;
import br.com.rocketseat.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {
    private final ClienteRepository repository = mock(ClienteRepository.class);
    private final ClienteService service = new ClienteService(repository);

    @Test
    void rejeitaEmailDuplicadoMesmoComMaiusculasEEspacos() {
        when(repository.existsByEmail("ana@example.com")).thenReturn(true);
        assertThrows(ConflitoException.class,
                () -> service.cadastrar(new ClienteRequest("Ana", " ANA@example.com ")));
        verify(repository, never()).save(any());
    }

    @Test
    void normalizaEmailEDefineData() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Cliente cliente = service.cadastrar(new ClienteRequest(" Ana ", " ANA@example.com "));
        assertEquals("Ana", cliente.getNome());
        assertEquals("ana@example.com", cliente.getEmail());
        assertEquals(LocalDate.now(), cliente.getDataCadastro());
    }

    @Test
    void rejeitaNomeOuEmailInvalido() {
        assertThrows(DadosInvalidosException.class, () -> service.cadastrar(new ClienteRequest(" ", "ana@example.com")));
        assertThrows(DadosInvalidosException.class, () -> service.cadastrar(new ClienteRequest("Ana", "")));
        assertThrows(DadosInvalidosException.class, () -> service.cadastrar(new ClienteRequest("Ana", "invalido")));
        verifyNoInteractions(repository);
    }

    @Test
    void informaClienteInexistente() {
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(99L));
    }
}
