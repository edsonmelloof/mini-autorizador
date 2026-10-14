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

## Padrões e SOLID

- **Ports and adapters**: casos de uso atrás de interfaces; persistência e HTTP são detalhes.
- **Chain of Responsibility**: `CartaoExistenteRegra` → `SenhaCorretaRegra` → `SaldoDisponivelRegra`. A primeira recusa encerra a cadeia (`Stream.findFirst`).
- **Strategy**: cada regra implementa `RegraAutorizacao`.
- **Factory Method**: `Cartao.novo` e `Dinheiro.de`.
- **Value objects**: `Dinheiro`, `NumeroCartao`.
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

Se duas instâncias tentarem gastar o mesmo R$ 10,00 ao mesmo tempo, só uma linha é atualizada. A outra recebe `SALDO_INSUFICIENTE`. Há também `@Version` na entidade para concorrência otimista em outras escritas.

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
7. `POST /transacoes` responde `text/plain` (`OK` ou o motivo).
8. Valor da transação deve ser ≥ `0.01`.
9. Número do cartão não é validado como PAN/Luhn — o briefing não exige.
10. MySQL 5.7 do compose é antigo; em máquinas novas a imagem pode exigir plataforma `linux/amd64`. Se o container não subir, use `docker compose up` a partir da pasta `docker/` inalterada e verifique o log do MySQL.

## Stack

- Java 25, Maven Wrapper, Spring Boot 4.1.1
- Spring Web MVC, Spring Data JPA, Spring Security, Bean Validation
- MySQL 5.7 (execução), H2 em modo MySQL (testes)
- JaCoCo
