# Mini autorizador — VR Benefícios

API REST em **Java 25** e **Spring Boot 4.1.1** para criar cartões de benefício, consultar saldo e autorizar transações.

## Como executar

```bash
cd docker
docker compose up -d mysql
cd ..
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Autenticação **HTTP Basic**: usuário `username`, senha `password`.

Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) (OpenAPI em `/v3/api-docs`). A documentação é pública; **Try it out** nos endpoints de negócio continua exigindo Basic Auth (`Authorize` no Swagger).

```bash
./mvnw test
```

Relatório de cobertura JaCoCo: `target/site/jacoco/index.html`.

## Contratos

Os endpoints seguem exatamente o briefing do teste:

| Método | URL | Sucesso | Falha de negócio |
| --- | --- | --- | --- |
| `POST` | `/cartoes` | `201` + JSON `{ senha, numeroCartao }` | `422` com o mesmo JSON se o cartão já existe |
| `GET` | `/cartoes/{numeroCartao}` | `200` + saldo numérico | `404` sem body |
| `POST` | `/transacoes` | `201` + `OK` | `422` + `SALDO_INSUFICIENTE` \| `SENHA_INVALIDA` \| `CARTAO_INEXISTENTE` |

Qualquer chamada sem Basic Auth válido responde `401`.

`POST /transacoes` usa `text/plain` de propósito. O briefing pede body `OK` ou `SALDO_INSUFICIENTE` (texto cru). Com `application/json` o Jackson serializaria `"OK"` (com aspas) e o teste da avaliação falharia.

## Exemplos de request e response

Autenticação em todos os exemplos: `Authorization: Basic` (`username` / `password`). Sem isso, a API responde `401` sem body de negócio.

### Criar cartão — `POST /cartoes`

Request:

```http
POST /cartoes HTTP/1.1
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=
Content-Type: application/json

{
  "numeroCartao": "6549873025634501",
  "senha": "1234"
}
```

Sucesso (`201`):

```json
{
  "senha": "1234",
  "numeroCartao": "6549873025634501"
}
```

Cartão já existente (`422`) — o mesmo JSON.

### Consultar saldo — `GET /cartoes/{numeroCartao}`

Request:

```http
GET /cartoes/6549873025634501 HTTP/1.1
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=
```

Sucesso (`200`) — número, não objeto JSON:

```
500.00
```

Depois de um débito de R$ 10,00:

```
490.00
```

Cartão inexistente (`404`) — sem body.

### Autorizar transação — `POST /transacoes`

Request:

```http
POST /transacoes HTTP/1.1
Authorization: Basic dXNlcm5hbWU6cGFzc3dvcmQ=
Content-Type: application/json

{
  "numeroCartao": "6549873025634501",
  "senhaCartao": "1234",
  "valor": 10.00
}
```

Sucesso (`201`, `Content-Type: text/plain`):

```
OK
```

Recusas (`422`, `text/plain`) — um destes textos, sem aspas:

```
SALDO_INSUFICIENTE
SENHA_INVALIDA
CARTAO_INEXISTENTE
```

### Sem autenticação

```http
GET /cartoes/6549873025634501 HTTP/1.1
```

Resposta: `401`.

### Os mesmos fluxos com `curl`

```bash
curl -u username:password -H 'Content-Type: application/json' \
  -d '{"numeroCartao":"6549873025634501","senha":"1234"}' \
  http://localhost:8080/cartoes

curl -u username:password http://localhost:8080/cartoes/6549873025634501

curl -u username:password -H 'Content-Type: application/json' \
  -d '{"numeroCartao":"6549873025634501","senhaCartao":"1234","valor":10.00}' \
  http://localhost:8080/transacoes

curl -i http://localhost:8080/cartoes/6549873025634501
```

## Arquitetura hexagonal

O domínio não conhece Spring Web, JPA nem BCrypt.

```
adapter/in/web          → REST (controllers, DTOs, exception handler)
adapter/in/security     → HTTP Basic
application             → casos de uso
domain                  → modelo, regras, ports
adapter/out/persistence → MySQL / JPA
adapter/out/crypto      → BCrypt da senha do cartão
```

Dependências apontam para dentro: adapters implementam `CartaoRepository` e `CodificadorSenha`; a aplicação orquestra os ports de entrada.

### Diagrama de classes

```mermaid
classDiagram
  direction TB

  class CartaoController {
    +criar(CartaoRequest)
    +saldo(numero)
  }
  class TransacaoController {
    +autorizar(TransacaoRequest)
  }
  class CriarCartaoUseCase {
    <<interface>>
    +criar(numero, senha)
  }
  class ConsultarSaldoUseCase {
    <<interface>>
    +saldo(numero)
  }
  class AutorizarTransacaoUseCase {
    <<interface>>
    +autorizar(Transacao)
  }
  class CriarCartaoService {
    +criar(numero, senha)
  }
  class ConsultarSaldoService {
    +saldo(numero)
  }
  class AutorizarTransacaoService {
    +autorizar(Transacao)
  }
  class CartaoRepository {
    <<interface>>
    +existe(numero)
    +salvar(Cartao)
    +buscarPorNumero(numero)
    +debitarSeHouverSaldo(numero, valor)
  }
  class CodificadorSenha {
    <<interface>>
    +codificar(senha)
    +confere(senha, hash)
  }
  class Cartao {
    <<record>>
    +numero
    +senhaHash
    +saldo
    +novo()
    +senhaConfere()
    +possuiSaldo()
  }
  class Transacao {
    <<record>>
    +numeroCartao
    +senhaCartao
    +valor
  }
  class Dinheiro {
    <<record>>
    +cobre(outro)
  }
  class NumeroCartao {
    <<record>>
  }
  class ResultadoAutorizacao {
    <<sealed>>
  }
  class Aprovada
  class Negada {
    +motivo
  }
  class MotivoNegacao {
    <<enum>>
    SALDO_INSUFICIENTE
    SENHA_INVALIDA
    CARTAO_INEXISTENTE
  }
  class CadeiaRegrasAutorizacao {
    +avaliar(contexto)
  }
  class RegraAutorizacao {
    <<interface>>
    +avaliar(contexto)
  }
  class CartaoExistenteRegra
  class SenhaCorretaRegra
  class SaldoDisponivelRegra
  class CartaoPersistenceAdapter
  class BcryptCodificadorSenha

  CartaoController --> CriarCartaoUseCase
  CartaoController --> ConsultarSaldoUseCase
  TransacaoController --> AutorizarTransacaoUseCase
  CriarCartaoUseCase <|.. CriarCartaoService
  ConsultarSaldoUseCase <|.. ConsultarSaldoService
  AutorizarTransacaoUseCase <|.. AutorizarTransacaoService
  CriarCartaoService --> CartaoRepository
  CriarCartaoService --> CodificadorSenha
  ConsultarSaldoService --> CartaoRepository
  AutorizarTransacaoService --> CartaoRepository
  AutorizarTransacaoService --> CodificadorSenha
  AutorizarTransacaoService --> CadeiaRegrasAutorizacao
  CartaoRepository <|.. CartaoPersistenceAdapter
  CodificadorSenha <|.. BcryptCodificadorSenha
  Cartao *-- NumeroCartao
  Cartao *-- Dinheiro
  Transacao *-- NumeroCartao
  Transacao *-- Dinheiro
  ResultadoAutorizacao <|-- Aprovada
  ResultadoAutorizacao <|-- Negada
  Negada --> MotivoNegacao
  CadeiaRegrasAutorizacao --> RegraAutorizacao
  RegraAutorizacao <|.. CartaoExistenteRegra
  RegraAutorizacao <|.. SenhaCorretaRegra
  RegraAutorizacao <|.. SaldoDisponivelRegra
```

### Diagrama de sequência — autorizar transação

```mermaid
sequenceDiagram
  autonumber
  actor Cliente
  participant REST as TransacaoController
  participant UC as AutorizarTransacaoService
  participant Repo as CartaoRepository
  participant Cadeia as CadeiaRegrasAutorizacao
  participant Regras as CartaoExistente / Senha / Saldo

  Cliente->>REST: POST /transacoes (Basic Auth)
  REST->>UC: autorizar(Transacao)
  UC->>Repo: buscarPorNumero
  Repo-->>UC: Optional Cartao
  UC->>Cadeia: avaliar(contexto)
  Cadeia->>Regras: existência → senha → saldo
  Regras-->>Cadeia: Optional MotivoNegacao
  alt alguma regra recusa
    Cadeia-->>UC: Negada(motivo)
    UC-->>REST: Negada
    REST-->>Cliente: 422 + SALDO_INSUFICIENTE | SENHA_INVALIDA | CARTAO_INEXISTENTE
  else cadeia aprova
    Cadeia-->>UC: Aprovada
    UC->>Repo: UPDATE saldo WHERE saldo >= valor
    alt uma linha atualizada
      Repo-->>UC: 1
      UC-->>REST: Aprovada
      REST-->>Cliente: 201 + OK
    else corrida de saldo
      Repo-->>UC: 0
      UC-->>REST: Negada(SALDO_INSUFICIENTE)
      REST-->>Cliente: 422 + SALDO_INSUFICIENTE
    end
  end
```

### Diagrama de sequência — criar cartão

```mermaid
sequenceDiagram
  autonumber
  actor Cliente
  participant REST as CartaoController
  participant UC as CriarCartaoService
  participant Repo as CartaoRepository
  participant Codec as CodificadorSenha

  Cliente->>REST: POST /cartoes (Basic Auth)
  REST->>UC: criar(numero, senha)
  UC->>Repo: existe(numero)
  alt cartão já existe
    Repo-->>UC: true
    UC-->>REST: CartaoJaExistenteException
    REST-->>Cliente: 422 + JSON senha e numeroCartao
  else cartão novo
    Repo-->>UC: false
    UC->>Codec: codificar(senha)
    Codec-->>UC: hash BCrypt
    UC->>Repo: salvar(Cartao saldo 500,00)
    UC-->>REST: ok
    REST-->>Cliente: 201 + JSON senha e numeroCartao
  end
```

## Padrões e SOLID

- **Ports and adapters**: casos de uso atrás de interfaces; persistência e HTTP são detalhes.
- **Chain of Responsibility**: `CartaoExistenteRegra` → `SenhaCorretaRegra` → `SaldoDisponivelRegra`. A primeira recusa encerra a cadeia (`Stream.findFirst`).
- **Strategy**: cada regra implementa `RegraAutorizacao`.
- **Factory Method**: `Cartao.novo` e `Dinheiro.de`.
- **Value objects / records**: `Cartao`, `Dinheiro`, `NumeroCartao`, DTOs HTTP — imutáveis, sem Lombok.
- **Lombok**: só na infraestrutura (`@RequiredArgsConstructor` em casos de uso e adapters, `@Getter` na entidade JPA). Sem `@Data` em entidade.
- **Sealed types**: `ResultadoAutorizacao` (`Aprovada` / `Negada`) — o controller usa `switch` em vez de `if`.
- **SRP / DIP / OCP**: nova regra de autorização entra na lista da cadeia, sem alterar as existentes.

Desafio “sem `if` / `break` / `continue`” no fluxo de autorização: recusas são `Optional` encadeados. Validações de value object usam `Optional.filter` + `orElseThrow`.

## Concorrência

O débito não é “ler saldo, somar na memória, salvar”. É um `UPDATE` atômico:

```sql
UPDATE cartoes
   SET saldo = saldo - :valor
 WHERE numero_cartao = :numero
   AND saldo >= :valor
```

Se duas instâncias tentarem gastar o mesmo R$ 10,00 ao mesmo tempo, só uma linha é atualizada. A outra recebe `SALDO_INSUFICIENTE`.

O `@Version` na entidade JPA **não é** o mecanismo desse débito: o `UPDATE` em massa do Hibernate não incrementa a coluna de versão. Ele só protegeria um `save` clássico (ler entidade, alterar saldo, persistir). No caminho de autorização, a garantia é o `AND saldo >= :valor`.

## Banco de dados

Foi escolhido o **MySQL** do `docker-compose.yml` enviado no teste. O serviço Mongo está comentado; a declaração do MySQL **não foi alterada** ( inclusive senha root vazia e `MYSQL_ALLOW_EMPTY_PASSWORD`).

A aplicação usa `ddl-auto=update` para criar a tabela `cartoes` na primeira subida — adequado a este exercício, não a produção.

## Suposições

1. Cartão novo sempre começa com **R$ 500,00** (escala de 2 casas, `HALF_EVEN`).
2. A senha do cartão é persistida com **BCrypt**. O JSON de resposta de criação devolve a senha em texto como no contrato (o avaliador espera o payload de entrada).
3. A senha de Basic Auth (`username`/`password`) é independente da senha do cartão.
4. Ordem das regras: existência → senha → saldo. Assim, cartão inexistente nunca vaza como senha inválida.
5. Transações **não** são persistidas (conforme o briefing).
6. `GET /cartoes/{id}` devolve o `BigDecimal` serializado (`500.00`), não um objeto JSON.
7. `POST /transacoes` responde `text/plain` (`OK` ou o motivo), não JSON, para o body bater com o briefing sem aspas.
8. Valor da transação deve ser ≥ `0.01`. O briefing não define payload inválido; Bean Validation responde **400** (não 422) para `valor` nulo ou menor que 0,01 e para campos em branco. O JSON de **resposta** de cartão segue o briefing com `senha` antes de `numeroCartao`.
9. Número do cartão não é validado como PAN/Luhn — o briefing não exige.
10. MySQL 5.7 do compose é antigo; em máquinas novas a imagem pode exigir plataforma `linux/amd64`. Se o container não subir, use `docker compose up` a partir da pasta `docker/` inalterada e verifique o log do MySQL.
11. `/swagger-ui/**` e `/v3/api-docs/**` ficam **públicos** para o avaliador abrir a documentação. Os contratos (`/cartoes`, `/transacoes`) continuam exigindo Basic Auth.

## Stack

- Java 25, Maven Wrapper, Spring Boot 4.1.1, Lombok (infraestrutura), springdoc-openapi 3.1.1
- Spring Web MVC, Spring Data JPA, Spring Security, Bean Validation
- MySQL 5.7 (execução), H2 em modo MySQL (testes)
- JaCoCo
