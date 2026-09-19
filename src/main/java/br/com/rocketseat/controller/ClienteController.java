package br.com.rocketseat.controller;

import br.com.rocketseat.dto.ClienteRequest;
import br.com.rocketseat.dto.ClienteResponse;
import br.com.rocketseat.dto.CompraResponse;
import br.com.rocketseat.dto.TotalGastoResponse;
import br.com.rocketseat.service.ClienteService;
import br.com.rocketseat.service.CompraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService service;
    private final CompraService compras;

    public ClienteController(ClienteService service, CompraService compras) {
        this.service = service;
        this.compras = compras;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> cadastrar(@RequestBody ClienteRequest request) {
        var cliente = ClienteResponse.de(service.cadastrar(request));
        return ResponseEntity.created(URI.create("/clientes/" + cliente.id())).body(cliente);
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return service.listar().stream().map(ClienteResponse::de).toList();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@PathVariable Long id) {
        return ClienteResponse.de(service.buscarPorId(id));
    }

    @GetMapping("/{id}/compras")
    public List<CompraResponse> historico(@PathVariable Long id) { return compras.historico(id); }

    @GetMapping("/{id}/total-gasto")
    public TotalGastoResponse totalGasto(@PathVariable Long id) { return compras.totalGasto(id); }
}
