# GameStore API

API REST do desafio em `plan.md`: jogos, clientes, compras e relatórios.

Java 25, Spring Boot 4.1.1, Maven Wrapper, JPA/Hibernate, PostgreSQL 17,
springdoc 3.1.1 e JUnit 5. Os services concentram as regras; os controllers
recebem e devolvem DTOs com records.

## Executar

Pré-requisitos: JDK 25 no `JAVA_HOME` e Docker com Compose em execução.
Não é necessário instalar Maven. O wrapper baixa a versão configurada.

Na raiz do projeto, em PowerShell:

```powershell
docker compose up -d --wait
.\mvnw.cmd spring-boot:run
```

Swagger (página inicial): <http://localhost:8080/>

OpenAPI: <http://localhost:8080/v3/api-docs>

No Linux/macOS, use `./mvnw` no lugar de `.\mvnw.cmd`.

O `compose.yaml` inicia o container `gamestore-local-postgres` na porta 5432,
com banco, usuário e senha **gamestore**. São credenciais de desenvolvimento local.
As tabelas são criadas/atualizadas pelo Hibernate.

```powershell
docker compose ps                 # Estado do banco
docker compose logs postgres     # Logs
docker compose stop              # Parar sem remover dados
docker compose up -d --wait       # Subir novamente
```

O volume `postgres-data` mantém os dados entre reinícios. `docker compose down`
também preserva o volume; adicionar `-v` apaga os dados.

Para outro banco ou porta da API, configure as variáveis antes de iniciar:

| Variável | Padrão |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/gamestore` |
| `DB_USER` | `gamestore` |
| `DB_PASSWORD` | `gamestore` |
| `PORT` | `8080` |

## Massa de dados pronta

O arquivo [sql/seed.sql](sql/seed.sql) adiciona **24 jogos, 12 clientes e 54 compras**
em um banco vazio, para explorar a API sem cadastrar tudo manualmente.

- Quatro jogos por gênero: ação, aventura, RPG, estratégia, esporte e corrida.
- Preços fictícios entre **R$ 24,90 e R$ 299,90**.
- Doze jogos do ano atual e doze do ano anterior.
- Compras em datas variadas e vendas com quantidades diferentes por gênero.
- Dois clientes sem compras (Larissa e Marcos) e cinco jogos sem vendas.
- Dezenove compras com valor pago diferente do preço atual do jogo.

Inicie a API pelo menos uma vez para o Hibernate criar as tabelas. Depois,
execute na raiz do projeto:

```powershell
docker compose up -d --wait
docker cp sql/seed.sql gamestore-local-postgres:/tmp/gamestore-seed.sql
docker exec gamestore-local-postgres psql -v ON_ERROR_STOP=1 -U gamestore -d gamestore -f /tmp/gamestore-seed.sql
```

A carga preserva os dados existentes e pode ser repetida sem duplicar registros.
Ela usa títulos e e-mails para localizar os cadastros, sem depender de IDs fixos.
Não atualiza preços ou datas de registros existentes. Mais detalhes em [sql/README.md](sql/README.md).

Na carga validada em **19/09/2026**, o banco já tinha um jogo, um cliente e uma
compra. Após a execução, ficou com **25 jogos, 13 clientes e 55 compras**:

| Gênero | Compras |
|---|---:|
| RPG | 14 |
| Ação | 9 |
| Aventura | 9 |
| Corrida | 9 |
| Esporte | 7 |
| Estratégia | 7 |

A segunda execução não inseriu novos registros. Esses totais representam a carga
validada; novos cadastros e compras feitos pela API alterarão os resultados.

Para explorar a massa, consulte os relatórios, jogos ordenados por preço,
cadastros do ano atual e histórico/total gasto de cada cliente. Use `GET /clientes`
para descobrir os IDs, inclusive os de Larissa e Marcos, que têm histórico vazio
e total gasto zero.

## Fluxo pelo Swagger

Abra cada operação, clique em **Try it out**, preencha o JSON e clique em **Execute**.

1. `POST /clientes`: `{"nome":"Ana","email":"ana@example.com"}`
2. `POST /jogos`: `{"titulo":"Chrono Trigger","genero":"RPG","preco":99.90}`
3. `POST /compras`: `{"clienteId":1,"jogoId":1}` — use os IDs retornados nos cadastros.
4. Consulte `GET /clientes/{id}/compras` e `GET /clientes/{id}/total-gasto`.

## Operações

| Método | Rota | Resultado |
|---|---|---|
| POST | `/jogos` | Cadastra jogo |
| GET | `/jogos` | Lista jogos |
| GET | `/jogos/{id}` | Busca por ID |
| GET | `/jogos/busca?titulo=...` | Busca por trecho do título, sem diferenciar maiúsculas |
| GET | `/jogos/genero/{genero}` | Filtra gênero |
| GET | `/jogos/ordenados-por-preco` | Ordena do menor para o maior preço |
| GET | `/jogos/mais-caro` | Retorna o jogo mais caro |
| GET | `/jogos/cadastrados-no-ano-atual` | Filtra ano atual |
| POST | `/clientes` | Cadastra cliente |
| GET | `/clientes` | Lista clientes |
| GET | `/clientes/{id}` | Busca por ID |
| GET | `/clientes/{id}/compras` | Histórico, mais recente primeiro |
| GET | `/clientes/{id}/total-gasto` | Soma de `valorPago` |
| POST | `/compras` | Registra compra |
| GET | `/compras` | Lista compras |
| GET | `/relatorios/generos` | Gêneros com jogos, sem repetição |
| GET | `/relatorios/vendas-por-genero` | Quantidade de compras por gênero |

Gêneros: `ACAO`, `AVENTURA`, `RPG`, `ESTRATEGIA`, `ESPORTE`, `CORRIDA`.
Preços aceitam até duas casas decimais. E-mails são normalizados para minúsculas.
Datas são geradas pelo servidor. O valor pago fica armazenado na compra.

Cadastros retornam `201`; consultas retornam `200`. Dados inválidos retornam `400`,
recursos inexistentes `404` e duplicidades `409`, com corpo `ProblemDetail`.
Buscas/listagens sem resultados retornam coleções vazias; sem jogos, `/mais-caro`
retorna `404`. Cliente existente sem compras tem total `0.00`.

## Testar e empacotar

```powershell
.\mvnw.cmd test                  # 26 testes unitários, sem banco
docker compose up -d --wait
.\mvnw.cmd verify                # Unitários + 9 testes de integração no PostgreSQL
java -jar target/GameStore-1.0.0.jar
```

`verify` gera o JAR e executa testes HTTP de todas as rotas, erros, concorrência,
Swagger/OpenAPI e persistência após reiniciar a aplicação. A integração cria e
remove um schema exclusivo, sem limpar os dados da aplicação. O usuário do banco
precisa ter permissão para criar schemas. As mesmas variáveis `DB_*` são utilizadas.

O Spring Boot 4 usa JUnit 6 por padrão; o POM fixa JUnit 5.13.4 para atender ao desafio.
O Mockito é carregado como agente apenas durante os testes unitários.
