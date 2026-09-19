Instruções
Estrutura, regras e requisitos do projeto

🎮 Desafio - GameStore API
Sistema de Loja de Games com Spring Boot
Objetivo
Desenvolver uma API para uma loja digital de games, permitindo cadastrar jogos e clientes, registrar compras e consultar informações da loja.

O objetivo é consolidar os principais conceitos estudados no curso utilizando um projeto pequeno e organizado, agora aplicado em uma API REST.

A proposta não é construir uma Steam completa. O desafio deve permanecer simples, com poucas entidades e regras de negócio claras.

Tecnologias
O projeto deverá utilizar:

Java 25 LTS
Spring Boot 4.1.1
Maven
Spring Web
Spring Data JPA
Hibernate
PostgreSQL
Swagger / OpenAPI com springdoc-openapi 3.x
JUnit 5
Spring Boot e a criação da API REST representam um pequeno passo além do conteúdo principal do curso. Não é necessário utilizar Spring Security, autenticação, paginação, arquitetura hexagonal, mensageria, Docker ou outros recursos avançados.

O projeto deverá utilizar JPA diretamente desde o início. Não é necessário criar uma versão anterior utilizando JDBC.

Cenário
Uma loja digital deseja criar uma API simples para controlar seus jogos, clientes e compras.

A loja precisa conseguir:

cadastrar jogos;
cadastrar clientes;
registrar compras;
consultar jogos disponíveis;
buscar jogos;
consultar o histórico de compras de um cliente;
calcular quanto um cliente já gastou;
produzir algumas informações simples sobre as vendas.
Os dados deverão ser armazenados em um banco PostgreSQL utilizando JPA/Hibernate.

1. Jogo
   Cada jogo deverá possuir, no mínimo:

id
titulo
genero
preco
dataCadastro
O gênero deverá ser representado por um enum.

Exemplo:

ACAO
AVENTURA
RPG
ESTRATEGIA
ESPORTE
CORRIDA
Você pode adicionar outros gêneros.

Regras
O título não pode ser vazio.
O preço deve ser maior que zero.
A data de cadastro deve ser definida automaticamente.
O gênero deve ser armazenado no banco de forma legível.
2. Cliente
   Cada cliente deverá possuir:

id
nome
email
dataCadastro
Regras
O nome não pode ser vazio.
O e-mail não pode ser vazio.
Dois clientes não podem possuir o mesmo e-mail.
A data de cadastro deve ser definida automaticamente.
3. Compra
   Cada compra deverá possuir:

id
cliente
jogo
dataCompra
valorPago
Uma compra relaciona:

Cliente → Compra ← Jogo
Utilize os relacionamentos JPA adequados.

Regras
Ao realizar uma compra:

O cliente precisa existir.
O jogo precisa existir.
O cliente não pode comprar o mesmo jogo duas vezes.
A data da compra deve ser registrada automaticamente.
O valor pago deve receber o preço atual do jogo.
O valor da compra deve ficar armazenado na própria compra.

Isso significa que, se o preço do jogo mudar posteriormente, uma compra antiga continuará mostrando o valor pago no momento em que foi realizada.

API REST
A aplicação não utilizará menu no terminal.

As funcionalidades serão acessadas através de endpoints HTTP.

Jogos
Cadastrar um jogo
POST /jogos
Listar todos os jogos
GET /jogos
Buscar jogo pelo ID
GET /jogos/{id}
Caso o jogo não exista, a aplicação deverá informar o erro adequadamente.

Buscar jogo pelo título
GET /jogos/busca?titulo=...
A busca poderá utilizar Optional para representar a possibilidade de o jogo não ser encontrado.

Listar jogos por gênero
GET /jogos/genero/{genero}
Listar jogos ordenados pelo preço
GET /jogos/ordenados-por-preco
Do menor para o maior preço.

Mostrar o jogo mais caro
GET /jogos/mais-caro
Listar jogos cadastrados no ano atual
GET /jogos/cadastrados-no-ano-atual
Clientes
Cadastrar cliente
POST /clientes
Listar clientes
GET /clientes
Buscar cliente pelo ID
GET /clientes/{id}
Mostrar histórico de compras de um cliente
GET /clientes/{id}/compras
Mostrar total gasto por um cliente
GET /clientes/{id}/total-gasto
O resultado deve considerar o campo valorPago de cada compra.

Compras
Realizar uma compra
POST /compras
Uma requisição poderá informar, por exemplo:

{
"clienteId": 1,
"jogoId": 3
}
A aplicação deverá validar as regras antes de salvar a compra.

Listar todas as compras
GET /compras
Relatórios
Além das operações principais, crie algumas consultas simples utilizando os recursos estudados durante o curso.

Gêneros disponíveis na loja
GET /relatorios/generos
Retorne apenas os gêneros que possuem jogos cadastrados.

Utilize um Set para representar valores sem repetição.

Quantidade de jogos vendidos por gênero
GET /relatorios/vendas-por-genero
Exemplo de resultado:

{
"RPG": 8,
"ACAO": 5,
"ESPORTE": 3
}
Um Map pode ser utilizado para representar essa informação.

Streams e Lambdas
Utilize Streams nas funcionalidades em que fizer sentido.

Alguns exemplos do próprio desafio:

ordenar jogos por preço;
encontrar o jogo mais caro;
filtrar jogos de um gênero;
filtrar jogos cadastrados no ano atual;
calcular o total gasto por um cliente;
descobrir os gêneros existentes;
contar vendas por gênero.
Não é necessário transformar toda operação em Stream.

O objetivo é utilizá-las quando tornarem o código mais claro.

Optional
Utilize Optional principalmente nas operações de busca.

Exemplo:

buscar jogo por ID
buscar cliente por ID
buscar jogo pelo título
Quando o valor não existir, trate a situação adequadamente em vez de espalhar verificações de null pelo projeto.

Datas
Utilize as classes modernas de data do Java.

Sugestão:

LocalDate     → cadastro de jogo e cliente
LocalDateTime → realização da compra
Também deverá existir pelo menos uma consulta relacionada a datas:

Listar jogos cadastrados no ano atual.

Exceptions
Crie exceções para situações importantes da regra de negócio.

Exemplos:

JogoNaoEncontradoException
ClienteNaoEncontradoException
EmailJaCadastradoException
JogoJaCompradoException
PrecoInvalidoException
Não é necessário criar dezenas de exceções.

Crie apenas aquelas que ajudam a representar erros reais da aplicação.

JPA e Hibernate
As três classes principais deverão ser persistidas utilizando JPA:

Jogo
Cliente
Compra
Utilize:

@Entity
identificadores das entidades;
relacionamentos;
enum persistido adequadamente;
datas;
repositories.
A entidade Compra deverá possuir relacionamento com Cliente e Jogo.

Não é necessário criar relacionamentos bidirecionais se eles não forem necessários para resolver o desafio.

Repositories
Utilize Spring Data JPA para acesso aos dados.

Exemplo conceitual:

JpaRepository<Jogo, Long>
JpaRepository<Cliente, Long>
JpaRepository<Compra, Long>
Aqui os Generics estudados no curso aparecem naturalmente através de tipos como:

List<Jogo>
Optional<Jogo>
Set<Genero>
Map<Genero, Long>
JpaRepository<Jogo, Long>
Não é necessário criar um repository genérico próprio.

Organização do Projeto
Uma possível organização é:

src/main/java
└── ...
├── controller
├── entity
├── enums
├── exception
├── repository
└── service
Responsabilidades sugeridas:

Controller
Recebe as requisições HTTP e devolve as respostas.

Service
Contém as regras de negócio.

Repository
Realiza o acesso aos dados através do JPA.

Entity
Representa os dados persistidos no banco.

Evite colocar todas as regras dentro dos Controllers.

Swagger / OpenAPI
A API deverá possuir documentação através do Swagger.

Utilize o springdoc-openapi.

Ao executar o projeto, o aluno deverá conseguir abrir o Swagger UI e testar os endpoints da aplicação.

O Swagger deverá permitir visualizar e executar, pelo menos:

cadastro de jogo;
cadastro de cliente;
realização de compra;
consultas de jogos;
consultas de clientes;
consultas de compras;
relatórios.
Não é necessário criar uma configuração avançada do OpenAPI.

A documentação automática dos endpoints já é suficiente para o desafio.

Maven
O projeto deverá ser criado e executado utilizando Maven.

O pom.xml deverá conter apenas as dependências necessárias ao projeto, como:

Spring Web
Spring Data JPA
PostgreSQL Driver
springdoc-openapi
Spring Boot Test
Não utilize arquivos .jar adicionados manualmente ao projeto.

Testes com JUnit 5
Crie testes unitários para algumas das principais regras de negócio.

Não é necessário testar todos os métodos do projeto.

Crie pelo menos 5 testes.

Alguns exemplos:

1. Não permitir jogo com preço inválido
   Dado um jogo com preço negativo
   Quando tentar cadastrá-lo
   Então uma exceção deverá ser lançada
2. Não permitir e-mail duplicado
   Dado um cliente já cadastrado
   Quando outro cliente utilizar o mesmo e-mail
   Então o cadastro deverá ser rejeitado
3. Não permitir compra com cliente inexistente
   Dado um ID de cliente inexistente
   Quando tentar realizar uma compra
   Então uma exceção deverá ser lançada
4. Não permitir comprar o mesmo jogo duas vezes
   Dado um cliente que já possui determinado jogo
   Quando tentar comprá-lo novamente
   Então a compra deverá ser rejeitada
5. Calcular corretamente o total gasto
   Dado um cliente com várias compras
   Quando consultar o total gasto
   Então a soma dos valores pagos deverá estar correta
   Os testes devem focar principalmente na camada de serviço e nas regras de negócio.

Conceitos trabalhados
Durante o desafio você terá oportunidade de utilizar, de forma natural:

classes e objetos;
encapsulamento;
enums;
exceptions;
List;
Set;
Map;
equals, hashCode e toString;
composição;
Streams;
lambdas;
method references;
Optional;
Generics;
LocalDate;
LocalDateTime;
JPA;
Hibernate;
Maven;
JUnit 5.
Além disso, o projeto introduz:

Spring Boot;
API REST;
Controllers;
Spring Data JPA;
Swagger / OpenAPI.
O que não é necessário
Para manter o desafio dentro do nível do curso, não é necessário implementar:

login;
autenticação;
Spring Security;
JWT;
carrinho de compras;
formas de pagamento;
cupons;
promoções;
estoque;
avaliações;
sistema de recomendação;
upload de imagens;
paginação;
Docker;
mensageria;
microsserviços;
arquitetura hexagonal;
cache;
deploy em nuvem.
Esses recursos podem ser estudados posteriormente.

Extras
Caso finalize o desafio principal, escolha algumas funcionalidades extras.

Não é necessário implementar todas.

Extra 1: Jogos mais vendidos
Mostre os três jogos com maior quantidade de compras.

Extra 2: Cliente que mais gastou
Descubra qual cliente possui o maior valor total em compras.

Extra 3: Ticket médio
Calcule o valor médio das compras realizadas na loja.

Extra 4: Resumo com Record
Crie um record para representar um resumo de compra.

Exemplo conceitual:

ResumoCompra(
nomeCliente,
tituloJogo,
valorPago,
dataCompra
)
Resultado esperado
Ao final do desafio, deverá existir uma API capaz de executar o seguinte fluxo:

Cadastrar cliente
↓
Cadastrar jogo
↓
Realizar compra
↓
Salvar utilizando JPA
↓
Consultar compras
↓
Utilizar Streams para gerar informações
↓
Documentar e testar a API

O desafio não busca reproduzir uma plataforma real de venda de jogos.

O objetivo é construir uma aplicação pequena, organizada e completa o suficiente para consolidar os conceitos vistos no curso, utilizando Spring Boot como próximo passo na evolução do projeto.
