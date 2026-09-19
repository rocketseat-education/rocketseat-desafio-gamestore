package br.com.rocketseat.controller;

import br.com.rocketseat.dto.CompraRequest;
import br.com.rocketseat.dto.CompraResponse;
import br.com.rocketseat.service.CompraService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/compras")
public class CompraController {
    private final CompraService service;

    public CompraController(CompraService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompraResponse comprar(@RequestBody CompraRequest request) {
        return service.comprar(request);
    }

    @GetMapping
    public List<CompraResponse> listar() { return service.listar(); }
}
