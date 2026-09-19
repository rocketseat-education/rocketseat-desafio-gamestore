package br.com.rocketseat.service;

import br.com.rocketseat.dto.JogoRequest;
import br.com.rocketseat.entity.Jogo;
import br.com.rocketseat.enums.Genero;
import br.com.rocketseat.exception.DadosInvalidosException;
import br.com.rocketseat.exception.RecursoNaoEncontradoException;
import br.com.rocketseat.repository.JogoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class JogoService {
    private final JogoRepository repository;

    public JogoService(JogoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Jogo cadastrar(JogoRequest request) {
        if (request == null) {
            throw new DadosInvalidosException("Informe os dados do jogo.");
        }
        String titulo = Validacao.texto(request.titulo(), "Título");
        if (request.genero() == null) {
            throw new DadosInvalidosException("Gênero é obrigatório.");
        }
        BigDecimal preco = request.preco();
        if (preco == null || preco.signum() <= 0 || preco.compareTo(new BigDecimal("9999999999.99")) > 0) {
            throw new DadosInvalidosException("Preço deve ser maior que zero e no máximo 9999999999.99.");
        }
        try {
            preco = preco.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException exception) {
            throw new DadosInvalidosException("Preço deve ter no máximo duas casas decimais.");
        }
        return repository.save(new Jogo(titulo, request.genero(), preco));
    }

    public List<Jogo> listar() {
        return repository.findAll();
    }

    public Jogo buscarPorId(Long id) {
        Validacao.id(id, "ID do jogo");
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo não encontrado."));
    }

    public List<Jogo> buscarPorTitulo(String titulo) {
        return repository.findByTituloContainingIgnoreCaseOrderByTituloAsc(Validacao.texto(titulo, "Título"));
    }

    public List<Jogo> porGenero(Genero genero) {
        return listar().stream().filter(jogo -> jogo.getGenero() == genero).toList();
    }

    public List<Jogo> ordenadosPorPreco() {
        return listar().stream().sorted(Comparator.comparing(Jogo::getPreco)).toList();
    }

    public Jogo maisCaro() {
        return listar().stream().max(Comparator.comparing(Jogo::getPreco))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhum jogo cadastrado."));
    }

    public List<Jogo> cadastradosNoAnoAtual() {
        int ano = LocalDate.now().getYear();
        return listar().stream().filter(jogo -> jogo.getDataCadastro().getYear() == ano).toList();
    }
}
