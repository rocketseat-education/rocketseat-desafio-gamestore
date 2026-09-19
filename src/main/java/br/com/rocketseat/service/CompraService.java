package br.com.rocketseat.service;

import br.com.rocketseat.dto.CompraRequest;
import br.com.rocketseat.dto.CompraResponse;
import br.com.rocketseat.dto.TotalGastoResponse;
import br.com.rocketseat.entity.Compra;
import br.com.rocketseat.exception.ConflitoException;
import br.com.rocketseat.exception.DadosInvalidosException;
import br.com.rocketseat.repository.CompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CompraService {
    private final CompraRepository repository;
    private final ClienteService clientes;
    private final JogoService jogos;

    public CompraService(CompraRepository repository, ClienteService clientes, JogoService jogos) {
        this.repository = repository;
        this.clientes = clientes;
        this.jogos = jogos;
    }

    @Transactional
    public CompraResponse comprar(CompraRequest request) {
        if (request == null) {
            throw new DadosInvalidosException("Informe clienteId e jogoId.");
        }
        Validacao.id(request.clienteId(), "ID do cliente");
        Validacao.id(request.jogoId(), "ID do jogo");
        var cliente = clientes.buscarPorId(request.clienteId());
        var jogo = jogos.buscarPorId(request.jogoId());
        if (repository.existsByClienteIdAndJogoId(request.clienteId(), request.jogoId())) {
            throw new ConflitoException("Cliente já comprou este jogo.");
        }
        return CompraResponse.de(repository.save(new Compra(cliente, jogo)));
    }

    public List<CompraResponse> listar() {
        return repository.findAll().stream().map(CompraResponse::de).toList();
    }

    public List<CompraResponse> historico(Long clienteId) {
        clientes.buscarPorId(clienteId);
        return repository.findByClienteIdOrderByDataCompraDesc(clienteId).stream()
                .map(CompraResponse::de).toList();
    }

    public TotalGastoResponse totalGasto(Long clienteId) {
        clientes.buscarPorId(clienteId);
        BigDecimal total = repository.findByClienteIdOrderByDataCompraDesc(clienteId).stream()
                .map(Compra::getValorPago).reduce(new BigDecimal("0.00"), BigDecimal::add);
        return new TotalGastoResponse(clienteId, total);
    }
}
