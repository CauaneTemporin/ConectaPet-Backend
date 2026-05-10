# Documentação de Endpoints - Conecta PET API

## Informações Gerais

**Base URL:** `http://localhost:8080/api`  
**Versão:** 1.0.0  
**Autenticação:** JWT Bearer Token

---

## 🔐 AUTENTICAÇÃO

### 1. Registrar Novo Usuário
**POST** `/auth/register`

**Descrição:** Cria uma nova conta de usuário

**Request Body:**
```json
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "phone": "(11) 98765-4321",
  "cpf": "123.456.789-00",
  "address": "Rua das Flores, 123"
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "João Silva",
    "email": "joao@example.com",
    "role": "USER"
  }
}
```

**Possíveis Erros:**
- 400: Dados inválidos ou usuário já existe

---

### 2. Fazer Login
**POST** `/auth/login`

**Descrição:** Autentica um usuário e retorna token JWT

**Request Body:**
```json
{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "name": "João Silva",
    "email": "joao@example.com",
    "role": "USER"
  }
}
```

**Possíveis Erros:**
- 401: Email ou senha inválidos

---

### 3. Obter Perfil do Usuário
**GET** `/auth/me`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@example.com",
  "phone": "(11) 98765-4321",
  "cpf": "123.456.789-00",
  "address": "Rua das Flores, 123",
  "role": "USER"
}
```

**Possíveis Erros:**
- 401: Token inválido ou expirado

---

### 4. Atualizar Perfil do Usuário
**PUT** `/auth/me`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "João Silva Atualizado",
  "phone": "(11) 99999-9999",
  "address": "Rua Nova, 456"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "João Silva Atualizado",
  "email": "joao@example.com",
  "phone": "(11) 99999-9999",
  "role": "USER"
}
```

---

### 5. Fazer Logout
**POST** `/auth/logout`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (204 No Content)**

> **Nota:** Sendo JWT stateless, o logout ocorre descartando o token no cliente

---

## 🐾 ANIMAIS

### 6. Listar Animais
**GET** `/animals`

**Query Parameters (Opcionais):**
- `species`: cachorro, gato, etc
- `size`: pequeno, medio, grande
- `status`: disponivel, adotado, apadrinhado
- `q`: busca textual (nome, raça, descrição)

**Exemplos:**
- `/animals?species=cachorro&status=disponivel`
- `/animals?q=labrador`
- `/animals?size=grande`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Rex",
    "species": "cachorro",
    "breed": "Labrador",
    "gender": "macho",
    "ageYears": 3,
    "size": "grande",
    "description": "Cão amigável e dócil",
    "temperament": "Calmo",
    "vaccinated": true,
    "neutered": true,
    "status": "disponivel",
    "photoUrl": "https://example.com/rex.jpg",
    "shelterId": 1,
    "createdAt": "2024-01-15T10:30:00Z"
  }
]
```

---

### 7. Obter Animal por ID
**GET** `/animals/{id}`

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Rex",
  "species": "cachorro",
  "breed": "Labrador",
  "gender": "macho",
  "ageYears": 3,
  "size": "grande",
  "description": "Cão amigável e dócil",
  "temperament": "Calmo",
  "vaccinated": true,
  "neutered": true,
  "status": "disponivel",
  "photoUrl": "https://example.com/rex.jpg",
  "shelterId": 1,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

**Possíveis Erros:**
- 404: Animal não encontrado

---

### 8. Criar Animal (ADMIN)
**POST** `/animals`

**Headers:**
```
Authorization: Bearer {token_admin}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Luna",
  "species": "gato",
  "breed": "Siamês",
  "gender": "fêmea",
  "ageYears": 2,
  "size": "pequeno",
  "description": "Gata meiga e carinhosa",
  "temperament": "Brincalhona",
  "vaccinated": true,
  "neutered": true,
  "status": "disponivel",
  "photoUrl": "https://example.com/luna.jpg",
  "shelterId": 1
}
```

**Response (201 Created):**
```json
{
  "id": 2,
  "name": "Luna",
  "species": "gato",
  "breed": "Siamês",
  "gender": "fêmea",
  "ageYears": 2,
  "size": "pequeno",
  "description": "Gata meiga e carinhosa",
  "temperament": "Brincalhona",
  "vaccinated": true,
  "neutered": true,
  "status": "disponivel",
  "photoUrl": "https://example.com/luna.jpg",
  "shelterId": 1,
  "createdAt": "2024-01-20T14:00:00Z"
}
```

**Possíveis Erros:**
- 401: Não autenticado
- 403: Acesso negado (requer role ADMIN)
- 400: Dados inválidos

---

### 9. Atualizar Animal Completamente (ADMIN)
**PUT** `/animals/{id}`

**Headers:**
```
Authorization: Bearer {token_admin}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Luna Updated",
  "species": "gato",
  "breed": "Siamês",
  "gender": "fêmea",
  "ageYears": 3,
  "size": "pequeno",
  "description": "Gata meiga, carinhosa e ativa",
  "temperament": "Brincalhona",
  "vaccinated": true,
  "neutered": true,
  "status": "adotado",
  "photoUrl": "https://example.com/luna-updated.jpg"
}
```

**Response (200 OK):**
```json
{
  "id": 2,
  "name": "Luna Updated",
  "status": "adotado",
  ...
}
```

**Possíveis Erros:**
- 404: Animal não encontrado
- 403: Acesso negado (requer role ADMIN)

---

### 10. Atualizar Animal Parcialmente (ADMIN)
**PATCH** `/animals/{id}`

**Headers:**
```
Authorization: Bearer {token_admin}
Content-Type: application/json
```

**Request Body (apenas campos a alterar):**
```json
{
  "status": "adotado",
  "description": "Recém adotado - muito feliz!"
}
```

**Response (200 OK):**
```json
{
  "id": 2,
  "name": "Luna",
  "status": "adotado",
  "description": "Recém adotado - muito feliz!",
  ...
}
```

---

### 11. Deletar Animal (ADMIN)
**DELETE** `/animals/{id}`

**Headers:**
```
Authorization: Bearer {token_admin}
```

**Response (204 No Content)**

**Possíveis Erros:**
- 404: Animal não encontrado
- 403: Acesso negado (requer role ADMIN)

---

## 🏘️ ABRIGOS

### 12. Listar Abrigos
**GET** `/shelters`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Abrigo Central",
    "address": "Rua das Flores, 123",
    "phone": "(11) 3333-3333",
    "email": "contato@abrigocentral.com.br",
    "description": "Maior abrigo da cidade",
    "capacity": 100
  }
]
```

---

### 13. Criar Abrigo (ADMIN)
**POST** `/shelters`

**Headers:**
```
Authorization: Bearer {token_admin}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Abrigo Sul",
  "address": "Avenida Paulista, 1000",
  "phone": "(11) 4444-4444",
  "email": "contato@abrigosul.com.br",
  "description": "Abrigo especializado em gatos",
  "capacity": 50
}
```

**Response (201 Created):**
```json
{
  "id": 2,
  "name": "Abrigo Sul",
  "address": "Avenida Paulista, 1000",
  ...
}
```

---

## 📝 ADOÇÕES

### 14. Solicitar Adoção
**POST** `/adoptions`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "animalId": 1,
  "motivation": "Procuro um cão amigável para minha família",
  "experience": "Tenho experiência com cães",
  "homeType": "casa"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "animal": {
    "id": 1,
    "name": "Rex"
  },
  "adopter": {
    "id": 1,
    "name": "João Silva"
  },
  "status": "PENDENTE",
  "motivation": "Procuro um cão amigável para minha família",
  "experience": "Tenho experiência com cães",
  "homeType": "casa",
  "createdAt": "2024-01-20T15:30:00Z"
}
```

**Possíveis Erros:**
- 401: Não autenticado
- 400: Dados inválidos

---

### 15. Listar Minhas Adoções
**GET** `/adoptions/mine`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "animal": {
      "id": 1,
      "name": "Rex"
    },
    "status": "PENDENTE",
    "createdAt": "2024-01-20T15:30:00Z"
  }
]
```

---

### 16. Listar Todas as Adoções (ADMIN)
**GET** `/adoptions`

**Query Parameters (Opcionais):**
- `status`: PENDENTE, APROVADO, RECUSADO, CONCLUIDO

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "animal": { "id": 1, "name": "Rex" },
    "adopter": { "id": 1, "name": "João Silva" },
    "status": "PENDENTE",
    "createdAt": "2024-01-20T15:30:00Z"
  }
]
```

---

### 17. Revisar Adoção (ADMIN)
**PATCH** `/adoptions/{id}/review`

**Headers:**
```
Authorization: Bearer {token_admin}
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "APROVADO"
}
```

**Valores de Status Aceitos:**
- `APROVADO`: Aprova a adoção
- `RECUSADO`: Recusa a adoção
- `CONCLUIDO`: Marca como concluída

**Response (200 OK):**
```json
{
  "id": 1,
  "animal": { "id": 1, "name": "Rex" },
  "adopter": { "id": 1, "name": "João Silva" },
  "status": "APROVADO",
  "updatedAt": "2024-01-21T10:00:00Z"
}
```

---

## 👶 APADRINHAMIENTOS (GODPARENTS)

### 18. Apadrinhar um Animal
**POST** `/godparents`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "animalId": 1
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "animal": {
    "id": 1,
    "name": "Rex"
  },
  "status": "ATIVO",
  "startedAt": "2024-01-20T16:00:00Z"
}
```

---

### 19. Listar Meus Apadrinhamientos
**GET** `/godparents/mine`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "animal": {
      "id": 1,
      "name": "Rex"
    },
    "status": "ATIVO",
    "startedAt": "2024-01-20T16:00:00Z"
  }
]
```

---

### 20. Desativar Apadrinhamimiento
**PATCH** `/godparents/{id}/status`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "animal": { "id": 1, "name": "Rex" },
  "status": "INATIVO",
  "startedAt": "2024-01-20T16:00:00Z"
}
```

**Possíveis Erros:**
- 404: Apadrinhamimiento não encontrado
- 403: Acesso negado (apenas o proprietário pode desativar)

---

### 21. Deletar Apadrinhamimiento
**DELETE** `/godparents/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (204 No Content)**

**Possíveis Erros:**
- 404: Apadrinhamimiento não encontrado
- 403: Acesso negado (apenas o proprietário pode deletar)

---

## 💰 DOAÇÕES

### 22. Fazer Doação
**POST** `/donations`

**Request Body (pode ser anônimo):**
```json
{
  "amount": 100.00,
  "name": "Maria Silva",
  "email": "maria@example.com",
  "message": "Parabéns pelo excelente trabalho!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "amount": 100.00,
  "donor": {
    "id": 2,
    "name": "Maria Silva"
  },
  "message": "Parabéns pelo excelente trabalho!",
  "createdAt": "2024-01-20T17:00:00Z"
}
```

---

### 23. Listar Minhas Doações
**GET** `/donations/mine`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "amount": 100.00,
    "message": "Parabéns pelo excelente trabalho!",
    "createdAt": "2024-01-20T17:00:00Z"
  }
]
```

---

### 24. Obter Estatísticas de Doações
**GET** `/donations/stats`

**Response (200 OK):**
```json
{
  "totalAmount": 5000.00,
  "averageAmount": 250.00,
  "totalDonations": 20
}
```

---

### 25. Listar Todas as Doações (ADMIN)
**GET** `/donations`

**Headers:**
```
Authorization: Bearer {token_admin}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "amount": 100.00,
    "donor": { "id": 2, "name": "Maria Silva" },
    "createdAt": "2024-01-20T17:00:00Z"
  }
]
```

---

## 📞 CONTATO

### 26. Enviar Mensagem de Contato
**POST** `/contact`

**Request Body:**
```json
{
  "name": "João da Silva",
  "email": "joao@example.com",
  "subject": "Dúvida sobre adoção",
  "message": "Gostaria de saber mais sobre o processo de adoção"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "João da Silva",
  "email": "joao@example.com",
  "subject": "Dúvida sobre adoção",
  "message": "Gostaria de saber mais sobre o processo de adoção",
  "read": false,
  "createdAt": "2024-01-20T18:00:00Z"
}
```

---

### 27. Listar Mensagens de Contato (ADMIN)
**GET** `/contact`

**Headers:**
```
Authorization: Bearer {token_admin}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "João da Silva",
    "email": "joao@example.com",
    "subject": "Dúvida sobre adoção",
    "message": "Gostaria de saber mais sobre o processo de adoção",
    "read": false,
    "createdAt": "2024-01-20T18:00:00Z"
  }
]
```

---

### 28. Marcar Mensagem como Lida (ADMIN)
**PATCH** `/contact/{id}/read`

**Headers:**
```
Authorization: Bearer {token_admin}
```

**Response (204 No Content)**

---

## 🤝 VOLUNTÁRIOS

### 29. Registrar como Voluntário
**POST** `/volunteers`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "skills": "Experiência com cães, adestramento básico",
  "availability": "Fins de semana",
  "motivation": "Quero ajudar a cuidar dos animais"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "user": {
    "id": 1,
    "name": "João Silva"
  },
  "skills": "Experiência com cães, adestramento básico",
  "availability": "Fins de semana",
  "motivation": "Quero ajudar a cuidar dos animais",
  "createdAt": "2024-01-20T19:00:00Z"
}
```

---

### 30. Obter Meu Perfil de Voluntário
**GET** `/volunteers/mine`

**Headers:**
```
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "user": { "id": 1, "name": "João Silva" },
  "skills": "Experiência com cães, adestramento básico",
  "availability": "Fins de semana",
  "motivation": "Quero ajudar a cuidar dos animais",
  "createdAt": "2024-01-20T19:00:00Z"
}
```

---

### 31. Listar Todos os Voluntários (ADMIN)
**GET** `/volunteers`

**Headers:**
```
Authorization: Bearer {token_admin}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "user": { "id": 1, "name": "João Silva" },
    "skills": "Experiência com cães, adestramento básico",
    "availability": "Fins de semana",
    "createdAt": "2024-01-20T19:00:00Z"
  }
]
```

---

### 32. Deletar Voluntário
**DELETE** `/volunteers/{id}`

**Headers:**
```
Authorization: Bearer {token}
```

**Description:** Permite que um voluntário delete seu próprio perfil

**Response (200 OK):**
```json
{
  "id": 1,
  "user": { "id": 1, "name": "João Silva" },
  "skills": "Experiência com cães, adestramento básico",
  "availability": "Fins de semana",
  "motivation": "Quero ajudar a cuidar dos animais",
  "status": "pendente",
  "createdAt": "2024-01-20T19:00:00Z"
}
```

**Possíveis Erros:**
- 401: Token inválido ou expirado
- 403: Acesso negado (apenas o proprietário pode deletar)
- 404: Voluntário não encontrado

---

### 33. Deletar Voluntários por Tipo de Habilidade (ADMIN)
**DELETE** `/volunteers/type/{skillType}`

**Headers:**
```
Authorization: Bearer {token_admin}
```

**Description:** Admin deleta todos os voluntários com um tipo específico de habilidade

**Path Parameters:**
- `skillType`: string - Tipo de habilidade a filtrar (ex: "fotografia", "transporte")

**Exemplos:**
```
DELETE /volunteers/type/fotografia
DELETE /volunteers/type/transporte
DELETE /volunteers/type/adestramento
```

**Response (204 No Content):** Sem corpo

**Possíveis Erros:**
- 401: Token inválido ou expirado
- 403: Acesso negado (requer role ADMIN)
- 404: Nenhum voluntário encontrado com o tipo especificado

---

## 📊 ADMIN

### 34. Obter Estatísticas do Sistema (ADMIN)
**GET** `/admin/stats`

**Headers:**
```
Authorization: Bearer {token_admin}
```

**Response (200 OK):**
```json
{
  "totalUsers": 150,
  "totalAnimals": 45,
  "animalsAvailable": 20,
  "animalsAdopted": 25,
  "totalAdoptions": 30,
  "adoptionsApproved": 25,
  "adoptionsPending": 5,
  "totalDonations": 5000.00,
  "totalVolunteers": 12,
  "totalGodparents": 18
}
```

---

## 🔐 Notas de Segurança

1. **Autenticação JWT:** Todos os endpoints protegidos requerem um token JWT válido no header `Authorization: Bearer {token}`

2. **Permissões ADMIN:** Os endpoints marcados com **(ADMIN)** requerem que o usuário tenha role `ADMIN`

3. **Permissões de Usuário:** Usuários regulares só podem acessar seus próprios dados (adoções, apadrinhamientos, doações, etc)

4. **Cabeçalhos Obrigatórios:**
   - `Content-Type: application/json` para POST, PUT, PATCH
   - `Authorization: Bearer {token}` para endpoints protegidos

5. **CORS:** A API aceita requisições de qualquer origem (configurável em `SecurityConfig`)

---

## 📋 Status HTTP Comuns

- **200 OK:** Requisição bem-sucedida
- **201 Created:** Recurso criado com sucesso
- **204 No Content:** Requisição bem-sucedida, sem conteúdo
- **400 Bad Request:** Dados inválidos
- **401 Unauthorized:** Token inválido ou expirado
- **403 Forbidden:** Acesso negado (falta permissão ADMIN)
- **404 Not Found:** Recurso não encontrado
- **500 Internal Server Error:** Erro no servidor

---

## 🔗 Acessar a Documentação Interativa

Quando a aplicação estiver rodando, acesse:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

---

**Gerado em:** 2024-01-20  
**Versão da API:** 1.0.0

