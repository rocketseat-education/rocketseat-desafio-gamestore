package br.com.rocketseat;

import org.junit.jupiter.api.*;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.context.WebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class GameStoreApiIT {
    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final String SCHEMA = "gamestore_test_" + UUID.randomUUID().toString().replace("-", "");
    private static final String DB_URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/gamestore");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "gamestore");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "gamestore");
    private static ConfigurableApplicationContext context;
    private static Connection database;
    private static String baseUrl;

    @BeforeAll
    static void iniciar() throws Exception {
        database = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        try (var statement = database.createStatement()) {
            statement.execute("CREATE SCHEMA " + SCHEMA);
            statement.execute("SET search_path TO " + SCHEMA);
        }
        iniciarAplicacao();
    }

    private static void iniciarAplicacao() {
        context = new SpringApplicationBuilder(GameStoreApplication.class).run(
                "--server.port=0",
                "--spring.datasource.url=" + DB_URL,
                "--spring.datasource.username=" + DB_USER,
                "--spring.datasource.password=" + DB_PASSWORD,
                "--spring.jpa.properties.hibernate.default_schema=" + SCHEMA,
                "--spring.jpa.hibernate.ddl-auto=update",
                "--logging.level.root=WARN");
        baseUrl = "http://localhost:" + ((WebServerApplicationContext) context).getWebServer().getPort();
    }

    @BeforeEach
    void limparSchemaDeTeste() throws Exception {
        try (var statement = database.createStatement()) {
            statement.execute("TRUNCATE TABLE compras, clientes, jogos RESTART IDENTITY");
        }
    }

    @AfterAll
    static void encerrar() throws Exception {
        if (context != null) context.close();
        if (database != null) {
            try (var connection = database; var statement = connection.createStatement()) {
                statement.execute("DROP SCHEMA " + SCHEMA + " CASCADE");
            }
        }
    }

    @Test
    void fluxoCompletoEConsultas() throws Exception {
        var cliente = post("/clientes", """
                {"nome":" Ana ","email":" ANA@example.com "}
                """, 201);
        assertEquals("ana@example.com", cliente.get("email").asString());
        assertEquals(LocalDate.now().toString(), cliente.get("dataCadastro").asString());
        var rpg = post("/jogos", """
                {"titulo":"Chrono Trigger","genero":"RPG","preco":99.90}
                """, 201);
        var corrida = post("/jogos", """
                {"titulo":"Corrida","genero":"CORRIDA","preco":29.90}
                """, 201);
        assertEquals(2, get("/jogos", 200).size());
        assertEquals("Chrono Trigger", get("/jogos/" + rpg.get("id").asLong(), 200).get("titulo").asString());
        assertEquals(1, get("/jogos/busca?titulo=CHRONO", 200).size());
        assertEquals(0, get("/jogos/busca?titulo=inexistente", 200).size());
        assertEquals(1, get("/jogos/genero/RPG", 200).size());
        assertEquals("Corrida", get("/jogos/ordenados-por-preco", 200).get(0).get("titulo").asString());
        assertEquals("Chrono Trigger", get("/jogos/mais-caro", 200).get("titulo").asString());
        assertEquals(2, get("/jogos/cadastrados-no-ano-atual", 200).size());
        assertEquals(1, get("/clientes", 200).size());
        assertEquals("Ana", get("/clientes/1", 200).get("nome").asString());
        assertEquals(0, get("/clientes/1/compras", 200).size());
        assertEquals(0.0, get("/clientes/1/total-gasto", 200).get("totalGasto").asDouble());

        var compra = post("/compras", compra(1, rpg.get("id").asLong()), 201);
        assertEquals(99.90, compra.get("valorPago").asDouble());
        assertNotNull(compra.get("dataCompra"));
        post("/compras", compra(1, corrida.get("id").asLong()), 201);
        assertEquals(2, get("/compras", 200).size());
        var historico = get("/clientes/1/compras", 200);
        assertEquals(2, historico.size());
        assertEquals("Corrida", historico.get(0).get("tituloJogo").asString());
        assertEquals(129.80, get("/clientes/1/total-gasto", 200).get("totalGasto").asDouble());
        assertEquals(2, get("/relatorios/generos", 200).size());
        assertEquals(1, get("/relatorios/vendas-por-genero", 200).get("RPG").asInt());
        assertEquals(1, get("/relatorios/vendas-por-genero", 200).get("CORRIDA").asInt());
    }

    @Test
    void rejeitaEntradasInvalidasERecursosInexistentes() throws Exception {
        for (String corpo : List.of("{}", "null", "{", """
                {"titulo":"","genero":"RPG","preco":10}
                """, """
                {"titulo":"Jogo","genero":"RPG","preco":-1}
                """, """
                {"titulo":"Jogo","genero":"RPG","preco":0.001}
                """, """
                {"titulo":"Jogo","genero":"INVALIDO","preco":10}
                """)) {
            assertEquals(400, post("/jogos", corpo, 400).get("status").asInt());
        }
        post("/clientes", "{\"nome\":\"Ana\",\"email\":\"\"}", 400);
        post("/clientes", "{\"nome\":\" \",\"email\":\"ana@example.com\"}", 400);
        post("/compras", "{}", 400);
        post("/compras", compra(0, 1), 400);
        post("/compras", compra(99, 99), 404);
        get("/jogos/999", 404);
        get("/clientes/999", 404);
        get("/clientes/999/compras", 404);
        get("/clientes/999/total-gasto", 404);
        get("/jogos/abc", 400);
        get("/jogos/genero/INVALIDO", 400);
        get("/jogos/busca", 400);
        get("/jogos/busca?titulo=%20", 400);
        assertEquals(0, get("/jogos", 200).size());
    }

    @Test
    void duplicidadesRetornamConflito() throws Exception {
        cadastrarDados();
        post("/clientes", "{\"nome\":\"Outra\",\"email\":\" ANA@EXAMPLE.COM \"}", 409);
        post("/compras", compra(1, 999), 404);
        post("/compras", compra(1, 1), 201);
        post("/compras", compra(1, 1), 409);
        assertEquals(1, get("/compras", 200).size());
    }

    @Test
    void bancoVazioRetornaColecoesVazias() throws Exception {
        for (String path : List.of("/jogos", "/clientes", "/compras", "/relatorios/generos",
                "/relatorios/vendas-por-genero", "/jogos/ordenados-por-preco",
                "/jogos/cadastrados-no-ano-atual", "/jogos/genero/RPG")) {
            assertEquals(0, get(path, 200).size(), path);
        }
        get("/jogos/mais-caro", 404);
    }

    @Test
    void filtroDeAnoExcluiCadastrosAntigos() throws Exception {
        cadastrarDados();
        try (var statement = database.prepareStatement("UPDATE jogos SET data_cadastro = ? WHERE id = 1")) {
            statement.setObject(1, LocalDate.now().minusYears(1));
            statement.executeUpdate();
        }
        assertEquals(0, get("/jogos/cadastrados-no-ano-atual", 200).size());
        assertEquals(1, get("/jogos", 200).size());
    }

    @Test
    void valorPagoEPersistenciaSobrevivemAoReinicio() throws Exception {
        cadastrarDados();
        post("/compras", compra(1, 1), 201);
        try (var statement = database.createStatement()) {
            statement.executeUpdate("UPDATE jogos SET preco = 199.90 WHERE id = 1");
        }
        context.close();
        iniciarAplicacao();
        assertEquals(199.90, get("/jogos/1", 200).get("preco").asDouble());
        assertEquals(49.90, get("/clientes/1/compras", 200).get(0).get("valorPago").asDouble());
        assertEquals(49.90, get("/clientes/1/total-gasto", 200).get("totalGasto").asDouble());
        assertEquals("Ana", get("/clientes/1", 200).get("nome").asString());
    }

    @Test
    void comprasConcorrentesNaoDuplicamRegistro() throws Exception {
        cadastrarDados();
        verificarConcorrencia("/compras", compra(1, 1));
        assertEquals(1, get("/compras", 200).size());
    }

    @Test
    void clientesConcorrentesNaoDuplicamEmail() throws Exception {
        verificarConcorrencia("/clientes", "{\"nome\":\"Ana\",\"email\":\"ana@example.com\"}");
        assertEquals(1, get("/clientes", 200).size());
    }

    @Test
    void swaggerDocumentaTodasAsOperacoes() throws Exception {
        var index = HTTP.send(HttpRequest.newBuilder(URI.create(baseUrl + "/"))
                .GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(302, index.statusCode());
        assertTrue(index.headers().firstValue("Location").orElseThrow().endsWith("/swagger-ui/index.html"));
        var docs = get("/v3/api-docs", 200);
        assertTrue(docs.get("openapi").asString().startsWith("3."));
        var paths = docs.get("paths");
        assertEquals(14, paths.size());
        assertTrue(paths.get("/jogos").has("post"));
        assertTrue(paths.get("/clientes").has("post"));
        assertTrue(paths.get("/compras").has("post"));
        assertTrue(paths.has("/relatorios/vendas-por-genero"));
        var ui = HTTP.send(HttpRequest.newBuilder(URI.create(baseUrl + "/swagger-ui/index.html"))
                .GET().build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, ui.statusCode());
        assertTrue(ui.body().contains("swagger-ui"));
    }

    private static void verificarConcorrencia(String path, String body) {
        List<CompletableFuture<HttpResponse<String>>> requests = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            requests.add(HTTP.sendAsync(request(path, body), HttpResponse.BodyHandlers.ofString()));
        }
        var statuses = requests.stream().map(CompletableFuture::join).map(HttpResponse::statusCode).toList();
        assertEquals(1, statuses.stream().filter(status -> status == 201).count(), statuses.toString());
        assertEquals(5, statuses.stream().filter(status -> status == 409).count(), statuses.toString());
    }

    private static void cadastrarDados() throws Exception {
        post("/clientes", "{\"nome\":\"Ana\",\"email\":\"ana@example.com\"}", 201);
        post("/jogos", "{\"titulo\":\"Jogo\",\"genero\":\"RPG\",\"preco\":49.90}", 201);
    }

    private static String compra(long clienteId, long jogoId) {
        return "{\"clienteId\":" + clienteId + ",\"jogoId\":" + jogoId + "}";
    }

    private static HttpRequest request(String path, String body) {
        var builder = HttpRequest.newBuilder(URI.create(baseUrl + path));
        return body == null ? builder.GET().build()
                : builder.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
    }

    private static JsonNode get(String path, int status) throws Exception { return send(path, null, status); }
    private static JsonNode post(String path, String body, int status) throws Exception { return send(path, body, status); }

    private static JsonNode send(String path, String body, int expectedStatus) throws Exception {
        var response = HTTP.send(request(path, body), HttpResponse.BodyHandlers.ofString());
        assertEquals(expectedStatus, response.statusCode(), path + ": " + response.body());
        return JSON.readTree(response.body());
    }
}
