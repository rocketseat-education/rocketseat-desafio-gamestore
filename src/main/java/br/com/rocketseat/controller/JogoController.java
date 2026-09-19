package br.com.rocketseat.controller;

import br.com.rocketseat.dto.JogoRequest;
import br.com.rocketseat.dto.JogoResponse;
import br.com.rocketseat.enums.Genero;
import br.com.rocketseat.service.JogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/jogos")
public class JogoController {
    private final JogoService service;

    public JogoController(JogoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<JogoResponse> cadastrar(@RequestBody JogoRequest request) {
        var jogo = JogoResponse.de(service.cadastrar(request));
        return ResponseEntity.created(URI.create("/jogos/" + jogo.id())).body(jogo);
    }

    @GetMapping
    public List<JogoResponse> listar() {
        return service.listar().stream().map(JogoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public JogoResponse buscarPorId(@PathVariable Long id) {
        return JogoResponse.de(service.buscarPorId(id));
    }

    @GetMapping("/busca")
    public List<JogoResponse> buscarPorTitulo(@RequestParam String titulo) {
        return service.buscarPorTitulo(titulo).stream().map(JogoResponse::de).toList();
    }

    @GetMapping("/genero/{genero}")
    public List<JogoResponse> porGenero(@PathVariable Genero genero) {
        return service.porGenero(genero).stream().map(JogoResponse::de).toList();
    }

    @GetMapping("/ordenados-por-preco")
    public List<JogoResponse> ordenadosPorPreco() {
        return service.ordenadosPorPreco().stream().map(JogoResponse::de).toList();
    }

    @GetMapping("/mais-caro")
    public JogoResponse maisCaro() { return JogoResponse.de(service.maisCaro()); }

    @GetMapping("/cadastrados-no-ano-atual")
    public List<JogoResponse> cadastradosNoAnoAtual() {
        return service.cadastradosNoAnoAtual().stream().map(JogoResponse::de).toList();
    }
}
