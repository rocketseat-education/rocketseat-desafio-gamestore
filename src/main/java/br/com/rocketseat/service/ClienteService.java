package br.com.rocketseat.service;

import br.com.rocketseat.dto.ClienteRequest;
import br.com.rocketseat.entity.Cliente;
import br.com.rocketseat.exception.ConflitoException;
import br.com.rocketseat.exception.DadosInvalidosException;
import br.com.rocketseat.exception.RecursoNaoEncontradoException;
import br.com.rocketseat.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class ClienteService {
    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) { this.repository = repository; }

    @Transactional
    public Cliente cadastrar(ClienteRequest request) {
        if (request == null) {
            throw new DadosInvalidosException("Informe os dados do cliente.");
        }
        String nome = Validacao.texto(request.nome(), "Nome");
        String email = Validacao.texto(request.email(), "E-mail").toLowerCase(Locale.ROOT);
        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new DadosInvalidosException("Informe um e-mail válido.");
        }
        if (repository.existsByEmail(email)) {
            throw new ConflitoException("E-mail já cadastrado.");
        }
        return repository.save(new Cliente(nome, email));
    }

    public List<Cliente> listar() { return repository.findAll(); }

    public Cliente buscarPorId(Long id) {
        Validacao.id(id, "ID do cliente");
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }
}
