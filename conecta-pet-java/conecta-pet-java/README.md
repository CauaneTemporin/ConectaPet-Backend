# 🐾 Conecta PET — Backend Java

Backend REST completo em **Java 21 + Spring Boot 3 + Spring Security + JWT + SQLite**.

---

## 📁 Estrutura do Projeto

```
conecta-pet-java/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/br/com/conectapet/
    │   │   ├── ConectaPetApplication.java   ← Entry point
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java      ← Spring Security + CORS + JWT filter
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   └── DataSeeder.java          ← Dados iniciais (admin, animais)
    │   │   ├── security/
    │   │   │   ├── JwtUtil.java             ← Gerar e validar tokens JWT
    │   │   │   ├── JwtAuthFilter.java       ← Filtro de autenticação
    │   │   │   └── UserDetailsServiceImpl.java
    │   │   ├── model/                       ← Entidades JPA
    │   │   │   ├── User.java
    │   │   │   ├── Animal.java
    │   │   │   ├── Shelter.java
    │   │   │   ├── Adoption.java
    │   │   │   ├── Donation.java
    │   │   │   ├── Volunteer.java
    │   │   │   ├── Godparent.java
    │   │   │   └── Contact.java
    │   │   ├── repository/                  ← Spring Data JPA
    │   │   │   ├── UserRepository.java
    │   │   │   ├── AnimalRepository.java
    │   │   │   ├── ShelterRepository.java
    │   │   │   ├── AdoptionRepository.java
    │   │   │   ├── DonationRepository.java
    │   │   │   ├── VolunteerRepository.java
    │   │   │   ├── GodparentRepository.java
    │   │   │   └── ContactRepository.java
    │   │   ├── dto/                         ← Records de Request/Response
    │   │   │   ├── AuthDTOs.java
    │   │   │   ├── UserDTOs.java
    │   │   │   ├── AnimalDTOs.java
    │   │   │   ├── AdoptionDTOs.java
    │   │   │   ├── DonationDTOs.java
    │   │   │   ├── VolunteerDTOs.java
    │   │   │   ├── GodparentDTOs.java
    │   │   │   ├── ContactDTOs.java
    │   │   │   ├── ShelterDTOs.java
    │   │   │   ├── AdminDTOs.java
    │   │   │   └── ErrorResponse.java
    │   │   ├── service/                     ← Regras de negócio
    │   │   │   ├── AuthService.java
    │   │   │   ├── AnimalService.java
    │   │   │   ├── AdoptionService.java
    │   │   │   ├── DonationService.java
    │   │   │   ├── VolunteerService.java
    │   │   │   ├── GodparentService.java
    │   │   │   ├── ContactService.java
    │   │   │   ├── ShelterService.java
    │   │   │   └── AdminService.java
    │   │   └── controller/                  ← REST Controllers
    │   │       ├── AuthController.java
    │   │       ├── AnimalController.java
    │   │       ├── AdoptionController.java
    │   │       ├── DonationController.java
    │   │       ├── VolunteerController.java
    │   │       ├── GodparentController.java
    │   │       ├── ContactController.java
    │   │       ├── ShelterController.java
    │   │       └── AdminController.java
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/.../ConectaPetIntegrationTest.java
        └── resources/application-test.properties
```

---

## 🚀 Como Executar

### Pré-requisitos
- **Java 21+** — `java -version`
- **Maven 3.8+** — `mvn -version`

### 1. Clonar e entrar na pasta
```bash
cd conecta-pet-java
```

### 2. Compilar e rodar
```bash
mvn spring-boot:run
```

### 3. Rodar os testes
```bash
mvn test
```

### 4. Gerar JAR executável
```bash
mvn package -DskipTests
java -jar target/conecta-pet-1.0.0.jar
```

O servidor sobe em **http://localhost:8080**

---

## 🔑 Usuários Padrão

| E-mail                   | Senha      | Papel |
|--------------------------|------------|-------|
| `admin@conectapet.org`   | `admin123` | ADMIN |
| `demo@conectapet.org`    | `demo123`  | USER  |

---

## 📡 Endpoints da API

### Autenticação
| Método | Rota              | Auth | Descrição              |
|--------|-------------------|------|------------------------|
| POST   | `/api/auth/register` | ✗  | Cadastrar usuário      |
| POST   | `/api/auth/login`    | ✗  | Login → retorna JWT    |
| POST   | `/api/auth/logout`   | ✓  | Logout (stateless)     |
| GET    | `/api/auth/me`       | ✓  | Perfil do usuário      |
| PUT    | `/api/auth/me`       | ✓  | Atualizar perfil       |

### Animais
| Método | Rota                | Auth  | Descrição              |
|--------|---------------------|-------|------------------------|
| GET    | `/api/animals`      | ✗    | Listar com filtros     |
| GET    | `/api/animals/{id}` | ✗    | Buscar por ID          |
| POST   | `/api/animals`      | ADMIN | Cadastrar animal       |
| PUT    | `/api/animals/{id}` | ADMIN | Atualizar animal       |
| PATCH  | `/api/animals/{id}` | ADMIN | Atualizar parcialmente |
| DELETE | `/api/animals/{id}` | ADMIN | Remover animal         |

**Filtros GET /api/animals:**
```
?species=CACHORRO&size=GRANDE&status=DISPONIVEL&q=labrador
```

### Adoções
| Método | Rota                       | Auth  | Descrição              |
|--------|----------------------------|-------|------------------------|
| POST   | `/api/adoptions`           | USER  | Solicitar adoção       |
| GET    | `/api/adoptions/mine`      | USER  | Minhas solicitações    |
| GET    | `/api/adoptions`           | ADMIN | Todas as solicitações  |
| PATCH  | `/api/adoptions/{id}/review` | ADMIN | Revisar solicitação  |

**Body review:**
```json
{ "status": "APROVADO" }
```
Status válidos: `APROVADO`, `RECUSADO`, `CONCLUIDO`

### Doações
| Método | Rota                   | Auth  | Descrição             |
|--------|------------------------|-------|-----------------------|
| POST   | `/api/donations`       | ✗    | Fazer doação          |
| GET    | `/api/donations/mine`  | USER  | Minhas doações        |
| GET    | `/api/donations/stats` | ✗    | Estatísticas públicas |
| GET    | `/api/donations`       | ADMIN | Todas as doações      |

### Voluntários
| Método | Rota                    | Auth  | Descrição           |
|--------|-------------------------|-------|---------------------|
| POST   | `/api/volunteers`       | USER  | Cadastrar-se        |
| GET    | `/api/volunteers/mine`  | USER  | Meu cadastro        |
| GET    | `/api/volunteers`       | ADMIN | Listar todos        |

### Padrinhos
| Método | Rota                   | Auth | Descrição           |
|--------|------------------------|------|---------------------|
| POST   | `/api/godparents`      | USER | Apadrinhar animal   |
| GET    | `/api/godparents/mine` | USER | Meus afilhados      |

### Contato
| Método | Rota                      | Auth  | Descrição           |
|--------|---------------------------|-------|---------------------|
| POST   | `/api/contact`            | ✗    | Enviar mensagem     |
| GET    | `/api/contact`            | ADMIN | Listar mensagens    |
| PATCH  | `/api/contact/{id}/read`  | ADMIN | Marcar como lida    |

### Abrigos
| Método | Rota             | Auth  | Descrição           |
|--------|------------------|-------|---------------------|
| GET    | `/api/shelters`  | ✗    | Listar abrigos      |
| POST   | `/api/shelters`  | ADMIN | Cadastrar abrigo    |

### Admin
| Método | Rota                | Auth  | Descrição             |
|--------|---------------------|-------|-----------------------|
| GET    | `/api/admin/stats`  | ADMIN | Dashboard estatísticas|

---

## 🔒 Segurança

- **BCrypt** para hash de senhas
- **JWT** (JJWT 0.12) com expiração de 7 dias
- **Spring Security** com configuração stateless
- **@EnableMethodSecurity** para controle por role
- **CORS** configurável via `application.properties`

---

## 🛠 Tecnologias

| Tecnologia          | Versão  | Uso                          |
|---------------------|---------|------------------------------|
| Java                | 21      | Linguagem                    |
| Spring Boot         | 3.2.5   | Framework                    |
| Spring Security     | 6.x     | Autenticação e autorização   |
| Spring Data JPA     | 3.x     | ORM                          |
| Hibernate           | 6.x     | Implementação JPA            |
| SQLite              | 3.45    | Banco de dados               |
| JJWT                | 0.12.5  | JSON Web Tokens              |
| Lombok              | latest  | Redução de boilerplate       |
| Bean Validation     | 3.x     | Validação de DTOs            |
| JUnit 5             | 5.x     | Testes                       |
| MockMvc             | 6.x     | Testes de integração         |

---

## 📋 Exemplo de Uso (curl)

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@conectapet.org","password":"admin123"}'
```

### Listar animais disponíveis
```bash
curl "http://localhost:8080/api/animals?status=DISPONIVEL&species=CACHORRO"
```

### Fazer doação
```bash
curl -X POST http://localhost:8080/api/donations \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 50.00,
    "frequency": "MENSAL",
    "method": "PIX",
    "donorName": "Maria Silva",
    "donorEmail": "maria@email.com"
  }'
```

### Solicitar adoção (autenticado)
```bash
TOKEN="seu_jwt_aqui"
curl -X POST http://localhost:8080/api/adoptions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"animalId": 1, "message": "Tenho quintal grande!"}'
```

### Aprovar adoção (admin)
```bash
ADMIN_TOKEN="jwt_do_admin"
curl -X PATCH http://localhost:8080/api/adoptions/1/review \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "APROVADO"}'
```

---

## 🗄 Modelo de Dados

```
users           ── sessions implícitas (JWT stateless)
  └── adoptions       (user → animal)
  └── donations       (user → nullable)
  └── volunteers      (user 1:1)
  └── godparents      (user → animal)

animals
  └── shelter (FK)
  └── createdBy (FK → user)

contacts        ── mensagens independentes
shelters        ── abrigos parceiros
```

---

## ⚙️ Configuração

Edite `src/main/resources/application.properties`:

```properties
# Porta do servidor
server.port=8080

# Banco de dados (SQLite por padrão)
spring.datasource.url=jdbc:sqlite:conecta_pet.db

# Chave JWT (gere com: openssl rand -base64 64)
jwt.secret=SUA_CHAVE_BASE64_AQUI
jwt.expiration-ms=604800000   # 7 dias

# Origens CORS permitidas
app.cors.allowed-origins=http://localhost:3000
```
