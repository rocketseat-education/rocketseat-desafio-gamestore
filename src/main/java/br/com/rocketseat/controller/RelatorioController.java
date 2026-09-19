package br.com.rocketseat.controller;

import br.com.rocketseat.enums.Genero;
import br.com.rocketseat.service.RelatorioService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {
    private final RelatorioService service;

    public RelatorioController(RelatorioService service) { this.service = service; }

    @GetMapping("/generos")
    public Set<Genero> generos() { return service.generos(); }

    @GetMapping("/vendas-por-genero")
    public Map<Genero, Long> vendasPorGenero() { return service.vendasPorGenero(); }
}
