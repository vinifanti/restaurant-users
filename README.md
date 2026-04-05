# 🍽️ Restaurant Users API

Sistema de gestão de usuários para restaurantes desenvolvido como **Tech Challenge — Fase 01** do curso de Pós-Graduação em Arquitetura e Desenvolvimento Java pela **POSTECH**.

---

## 📋 Sobre o Projeto

Um grupo de restaurantes contratou o desenvolvimento de um sistema de gestão compartilhado. Esta primeira fase implementa o módulo de usuários, permitindo o cadastro e gerenciamento de **Donos de Restaurante** e **Clientes**.

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.4 | Framework principal |
| Spring Data JPA | - | Persistência de dados |
| Spring Validation | - | Validação de entrada |
| PostgreSQL | 17 | Banco de dados relacional |
| Docker / Docker Compose | - | Containerização |
| Lombok | - | Redução de código boilerplate |
| SpringDoc OpenAPI | - | Documentação Swagger |
| JUnit 5 + Mockito | - | Testes unitários |
| Maven | - | Gerenciamento de dependências |

---

## 🏗️ Arquitetura

O projeto segue o padrão de **Arquitetura em Camadas**, garantindo separação de responsabilidades:

```
Cliente / Postman / Swagger UI
           ↓
     [Controller]        → Recebe requisições HTTP, valida entrada com @Valid
           ↓
      [Service]          → Regras de negócio (e-mail único, validação de senha, tipo de usuário)
           ↓
    [Repository]         → Acesso ao banco via Spring Data JPA (Query Methods)
           ↓
   [PostgreSQL 17]       → Banco de dados relacional em container Docker
```

### Estrutura de Pastas

```
src/main/java/com/fiap/restaurant_users/
├── config/              # Configurações (Swagger)
├── controller/          # Endpoints REST
├── dto/
│   ├── request/         # DTOs de entrada (CreateUserRequest, UpdateUserRequest, etc.)
│   └── response/        # DTOs de saída (UserResponse)
├── exception/           # Tratamento global de erros (ProblemDetail RFC 7807)
├── model/               # Entidades JPA (User, Customer, RestaurantOwner, Address)
├── repository/          # Interfaces Spring Data JPA
└── service/             # Regras de negócio
```

---

## 🗄️ Modelagem do Banco de Dados

O sistema utiliza **herança JPA com estratégia JOINED** — os campos comuns ficam na tabela `users` e cada tipo de usuário possui sua própria tabela com chave estrangeira.

```
users (tabela pai)
├── id             BIGINT        PK, AUTO_INCREMENT
├── name           VARCHAR(255)  NOT NULL
├── email          VARCHAR(255)  NOT NULL, UNIQUE
├── login          VARCHAR(255)  NOT NULL
├── password       VARCHAR(255)  NOT NULL
├── last_modified_at TIMESTAMP
├── street         VARCHAR(255)  (@Embeddable Address)
├── number         VARCHAR(255)  (@Embeddable Address)
├── city           VARCHAR(255)  (@Embeddable Address)
├── zip_code       VARCHAR(255)  (@Embeddable Address)
└── dtype          VARCHAR(31)   NOT NULL (discriminador JPA)

customers (tabela filha)
└── id             BIGINT        PK, FK → users.id

restaurant_owners (tabela filha)
└── id             BIGINT        PK, FK → users.id
```

---

## 🚀 Como Executar

### Pré-requisitos

- [Docker](https://www.docker.com/) instalado
- [Docker Compose](https://docs.docker.com/compose/) instalado

### Passo a Passo

**1. Clone o repositório**
```bash
git clone https://github.com/seu-usuario/restaurant-users.git
cd restaurant-users
```

**2. Suba os containers**
```bash
docker compose up --build -d
```

**3. Verifique se os containers estão rodando**
```bash
docker compose ps
```

Você deve ver dois containers com status **Up**:
```
restaurant_app   → porta 8080
restaurant_db    → porta 5432
```

**4. Acesse a API**
```
http://localhost:8080/api/v1/users
```

**5. Acesse a documentação Swagger**
```
http://localhost:8080/swagger-ui/index.html
```

**6. Para parar os containers**
```bash
docker compose down
```

**7. Para parar e remover os volumes (banco limpo)**
```bash
docker compose down -v
```

### Variáveis de Ambiente

| Variável | Valor Padrão | Descrição |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/restaurant_users` | URL de conexão com o banco |
| `SPRING_DATASOURCE_USERNAME` | `admin` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | `admin123` | Senha do banco |
| `POSTGRES_DB` | `restaurant_users` | Nome do banco de dados |
| `POSTGRES_USER` | `admin` | Usuário PostgreSQL |
| `POSTGRES_PASSWORD` | `admin123` | Senha PostgreSQL |

---

## 📡 Endpoints

Todos os endpoints estão versionados sob o prefixo `/api/v1/users`.

| Método | URL | Status | Descrição |
|---|---|---|---|
| `POST` | `/api/v1/users` | 201 | Cadastrar usuário |
| `GET` | `/api/v1/users/{id}` | 200 | Buscar usuário por ID |
| `GET` | `/api/v1/users/search?name=` | 200 | Buscar usuários por nome |
| `PUT` | `/api/v1/users/{id}` | 200 | Atualizar dados do usuário |
| `PATCH` | `/api/v1/users/{id}/password` | 204 | Trocar senha (endpoint exclusivo) |
| `DELETE` | `/api/v1/users/{id}` | 204 | Excluir usuário |
| `POST` | `/api/v1/users/login` | 200 | Validar login e senha |

### Tipos de Usuário

| userType | Descrição |
|---|---|
| `CUSTOMER` | Cliente que consulta restaurantes e faz pedidos |
| `RESTAURANT_OWNER` | Dono de restaurante que gerencia seu estabelecimento |

---

## 📝 Exemplos de Uso

### Cadastrar Usuário

**Request:**
```http
POST /api/v1/users
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "login": "joao123",
  "password": "senha123",
  "userType": "CUSTOMER",
  "address": {
    "street": "Rua das Flores",
    "number": "123",
    "city": "São Paulo",
    "zipCode": "01310-100"
  }
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@email.com",
  "login": "joao123",
  "userType": "Customer",
  "address": {
    "street": "Rua das Flores",
    "number": "123",
    "city": "São Paulo",
    "zipCode": "01310-100"
  },
  "lastModifiedAt": "2026-04-05T18:00:00"
}
```

### Trocar Senha

**Request:**
```http
PATCH /api/v1/users/1/password
Content-Type: application/json

{
  "currentPassword": "senha123",
  "newPassword": "novaSenha456"
}
```

**Response (204 No Content):** sem corpo na resposta.

### Validar Login

**Request:**
```http
POST /api/v1/users/login
Content-Type: application/json

{
  "login": "joao123",
  "password": "senha123"
}
```

---

## ⚠️ Padrão de Erros

A API segue o padrão **ProblemDetail (RFC 7807)** para todas as respostas de erro:

```json
{
  "type": "https://api.restaurant.com/errors/not-found",
  "title": "Recurso não encontrado",
  "status": 404,
  "detail": "Usuário com id 999 não encontrado",
  "instance": "/api/v1/users/999"
}
```

| Status | Situação |
|---|---|
| `400` | Dados inválidos (e-mail duplicado, senha incorreta, login inválido) |
| `404` | Recurso não encontrado |
| `409` | Conflito de dados no banco |
| `422` | Erro de validação nos campos obrigatórios |
| `500` | Erro interno do servidor |

---

## 🧪 Testes

O projeto possui **14 testes unitários** cobrindo todos os cenários do `UserService`, utilizando **JUnit 5** e **Mockito**.

### Executar os testes

```bash
mvn test
```

### Cobertura dos Testes

| Cenário | Resultado |
|---|---|
| Criar usuário com sucesso | ✅ PASS |
| Lançar exceção com e-mail duplicado | ✅ PASS |
| Lançar exceção com tipo de usuário inválido | ✅ PASS |
| Buscar usuário por ID com sucesso | ✅ PASS |
| Lançar exceção com ID inexistente | ✅ PASS |
| Buscar usuários por nome | ✅ PASS |
| Retornar lista vazia para nome inexistente | ✅ PASS |
| Atualizar usuário com sucesso | ✅ PASS |
| Lançar exceção ao atualizar com e-mail duplicado | ✅ PASS |
| Trocar senha com sucesso | ✅ PASS |
| Lançar exceção com senha atual incorreta | ✅ PASS |
| Validar login com sucesso | ✅ PASS |
| Lançar exceção com login inválido | ✅ PASS |
| Deletar usuário com sucesso | ✅ PASS |

**14/14 testes passando ✅**

---

## 📚 Documentação

A documentação completa dos endpoints está disponível via **Swagger UI** após subir a aplicação:

```
http://localhost:8080/swagger-ui/index.html
```

A coleção Postman com todos os cenários de teste está disponível em:

```
restaurant-users.postman_collection.json
```

---

## 📁 Estrutura do Projeto

```
restaurant-users/
├── src/
│   ├── main/
│   │   ├── java/com/fiap/restaurant_users/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/fiap/restaurant_users/
│           └── service/
│               └── UserServiceTest.java
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── restaurant-users.postman_collection.json
└── README.md
```

---

## 👨‍💻 Autores

**RM370614 - Vinicius Campos Fanti**  
**RM373960 - Alex Sousa**  
**RM373739 - Lucas Gonçalves Prado das Neves**  
**RM372334 - Higo Otaviano da Rocha**  
**RM371904 - Marcos Sanches Polido Junior**


Desenvolvido como entregável do **Tech Challenge — Fase 01**
Curso: Arquitetura e Desenvolvimento Java — POSTECH
