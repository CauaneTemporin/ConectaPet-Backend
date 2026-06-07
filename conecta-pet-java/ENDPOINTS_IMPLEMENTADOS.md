# ✅ Endpoints Implementados - Conecta PET API

## 📌 Base URL
```
http://localhost:8080/api
```

## 🔐 Autenticação
Todos os endpoints protegidos requerem:
```
Authorization: Bearer {token_jwt}
```

---

## 🔑 AUTH

### ✅ POST /auth/login
**Tipo:** Público  
**Body:**
```json
{
  "email": "string",
  "password": "string"
}
```
**Response (200):**
```json
{
  "token": "string",
  "type": "Bearer",
  "user": {
    "id": 1,
    "name": "string",
    "email": "string",
    "role": "USER|ADMIN"
  }
}
```

### ✅ POST /auth/register
**Tipo:** Público  
**Body:**
```json
{
  "name": "string",
  "email": "string",
  "password": "string",
  "phone": "string (opcional)",
  "cpf": "string (opcional)",
  "address": "string (opcional)"
}
```
**Response (201):** Mesmo que login

---

## 🐾 ANIMALS

### ✅ GET /animals
**Tipo:** Público  
**Query Params (opcionais):**
- `species`: cachorro, gato, etc
- `size`: pequeno, medio, grande
- `status`: disponivel, adotado, apadrinhado
- `q`: busca textual

**Response (200):**
```json
[
  {
    "id": 1,
    "name": "Rex",
    "species": "cachorro",
    "breed": "SRD",
    "gender": "macho",
    "ageYears": 2,
    "size": "medio",
    "description": "...",
    "temperament": "dócil",
    "vaccinated": true,
    "neutered": false,
    "status": "disponivel",
    "photoUrl": "https://...",
    "shelterName": "Abrigo Alegre",
    "createdAt": "2024-01-01T00:00:00Z"
  }
]
```

### ✅ GET /animals/:id
**Tipo:** Público  
**Response (200):** Objeto Animal único

### ✅ POST /animals
**Tipo:** ADMIN  
**Body:** Object AnimalRequest  
**Response (201):** Objeto Animal criado

### ✅ PATCH /animals/:id
**Tipo:** ADMIN  
**Body:** Partial<AnimalRequest>  
**Response (200):** Objeto Animal atualizado

### ✅ DELETE /animals/:id
**Tipo:** ADMIN  
**Response (204):** Sem corpo

---

## 📝 ADOPTIONS

### ✅ POST /adoptions
**Tipo:** USER (autenticado)  
**Body:**
```json
{
  "animalId": 1,
  "message": "string (opcional)"
}
```
**Response (201):**
```json
{
  "id": 1,
  "animalId": 1,
  "animalName": "Rex",
  "animalSpecies": "cachorro",
  "animalPhoto": "https://...",
  "userId": 10,
  "userName": "João",
  "userEmail": "joao@email.com",
  "status": "pendente",
  "message": "...",
  "createdAt": "2024-01-01T00:00:00Z",
  "reviewedAt": null
}
```

### ✅ GET /adoptions/mine
**Tipo:** USER (autenticado)  
**Response (200):** Array de Adoption[]

### ✅ GET /adoptions
**Tipo:** ADMIN  
**Response (200):** Array de Adoption[]

### ✅ PATCH /adoptions/:id/review
**Tipo:** ADMIN  
**Body:**
```json
{
  "status": "APROVADO|RECUSADO|CONCLUIDO"
}
```
**Response (200):** Objeto Adoption atualizado

---

## 💰 DONATIONS

### ✅ POST /donations
**Tipo:** Público (pode ser anônimo)  
**Body:**
```json
{
  "amount": 50.0,
  "name": "string",
  "email": "string",
  "message": "string (opcional)"
}
```
**Response (201):**
```json
{
  "id": 1,
  "amount": 50.0,
  "donor": {
    "id": 10,
    "name": "Maria",
    "email": "maria@email.com"
  },
  "message": "...",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

### ✅ GET /donations/mine
**Tipo:** USER (autenticado)  
**Response (200):** Array de Donation[]

### ✅ GET /donations
**Tipo:** ADMIN  
**Response (200):** Array de Donation[]

### ✅ GET /donations/stats
**Tipo:** Público  
**Response (200):**
```json
{
  "totalAmount": 1250.00,
  "averageAmount": 62.50,
  "totalDonations": 20
}
```

---

## 🤝 VOLUNTEERS ⬅️ NOVOS

### ✅ POST /volunteers
**Tipo:** USER (autenticado)  
**Body:**
```json
{
  "skills": "fotografia, transporte",
  "availability": "fins de semana",
  "motivation": "Amo animais (opcional)"
}
```
**Response (201):**
```json
{
  "id": 1,
  "userId": 10,
  "userName": "Maria",
  "userEmail": "maria@email.com",
  "skills": "fotografia, transporte",
  "availability": "fins de semana",
  "motivation": "Amo animais",
  "status": "pendente",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

### ✅ GET /volunteers/mine
**Tipo:** USER (autenticado)  
**Response (200):** Objeto Volunteer único  
**Response (404):** Se não está cadastrado como voluntário

### ✅ PATCH /volunteers/:id
**Tipo:** USER (dono do registro)  
**Body:**
```json
{
  "skills": "fotografia, transporte",
  "availability": "fins de semana",
  "motivation": "Amo animais"
}
```
**Response (200):** Objeto Volunteer atualizado

### ✅ PATCH /volunteers/:id/approve
**Tipo:** ADMIN  
**Body:** `{}`  
**Response (200):** Objeto Volunteer com `status: "aprovado"`

### ✅ PATCH /volunteers/:id/reject
**Tipo:** ADMIN  
**Body:** `{}`  
**Response (200):** Objeto Volunteer com `status: "rejeitado"`

### ✅ GET /volunteers
**Tipo:** ADMIN  
**Response (200):** Array de Volunteer[]

### ✅ DELETE /volunteers/:id
**Tipo:** USER (dono do registro)  
**Response (200):** Objeto Volunteer deletado  
**Response (404):** Voluntário não encontrado  
**Response (403):** Acesso negado (apenas o proprietário pode deletar)

### ✅ DELETE /volunteers/type/:skillType
**Tipo:** ADMIN  
**Description:** Deleta todos os voluntários com um tipo específico de habilidade  
**Response (204):** Sem corpo  
**Response (404):** Nenhum voluntário encontrado com o tipo especificado

**Exemplo:**
```
DELETE /volunteers/type/fotografia
```

---

## 👶 GODPARENTS

### ✅ POST /godparents
**Tipo:** USER (autenticado)  
**Body:**
```json
{
  "animalId": 1
}
```
**Response (201):**
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

### ✅ GET /godparents/mine
**Tipo:** USER (autenticado)  
**Response (200):** Array de Godparent[]

### ✅ PATCH /godparents/:id/status
**Tipo:** USER (proprietário)  
**Comportamento:** Muda status para INATIVO e **cancela doações**  
**Response (200):** Objeto Godparent com `status: "INATIVO"`

### ✅ DELETE /godparents/:id
**Tipo:** USER (proprietário)  
**Comportamento:** Deleta apadrinhamimiento e **cancela doações**  
**Response (204):** Sem corpo

---

## 📧 CONTACTS

### ✅ POST /contacts
**Tipo:** Público  
**Body:**
```json
{
  "name": "João",
  "email": "joao@email.com",
  "phone": "string (opcional)",
  "subject": "string (opcional)",
  "message": "..."
}
```
**Response (201):**
```json
{
  "id": 1,
  "name": "João",
  "email": "joao@email.com",
  "phone": "...",
  "subject": "...",
  "message": "...",
  "read": false,
  "createdAt": "2024-01-01T00:00:00Z"
}
```

### ✅ GET /contacts
**Tipo:** ADMIN  
**Response (200):** Array de Contact[]

### ✅ PATCH /contacts/:id/read
**Tipo:** ADMIN  
**Body:** `{}`  
**Response (200):** Objeto Contact com `read: true`

---

## 📊 ADMIN

### ✅ GET /admin/stats
**Tipo:** ADMIN  
**Response (200):**
```json
{
  "totalUsers": 50,
  "totalAnimals": 30,
  "animalsAvailable": 18,
  "animalsAdopted": 10,
  "totalAdoptions": 25,
  "adoptionsPending": 5,
  "totalDonations": 1250.00,
  "totalVolunteers": 12,
  "totalGodparents": 8,
  "unreadContacts": 3
}
```

---

## 🎯 Resumo de Permissões

| Endpoint | Público | User | Admin |
|----------|---------|------|-------|
| GET /animals | ✅ | ✅ | ✅ |
| POST /animals | ❌ | ❌ | ✅ |
| PATCH /animals | ❌ | ❌ | ✅ |
| DELETE /animals | ❌ | ❌ | ✅ |
| POST /adoptions | ❌ | ✅ | ✅ |
| GET /adoptions/mine | ❌ | ✅ | ✅ |
| PATCH /adoptions/:id/review | ❌ | ❌ | ✅ |
| POST /godparents | ❌ | ✅ | ✅ |
| POST /volunteers | ❌ | ✅ | ✅ |
| PATCH /volunteers/:id/approve | ❌ | ❌ | ✅ |
| POST /donations | ✅ | ✅ | ✅ |
| POST /contacts | ✅ | ✅ | ✅ |
| GET /admin/stats | ❌ | ❌ | ✅ |

---

## 📌 Notas Importantes

1. **Volunteer Status:** `pendente` → `aprovado` ou `rejeitado`
2. **Godparent Cancelamento:** Ao deletar ou desativar um apadrinhamimiento, **todas as doações do usuário são canceladas**
3. **Animal Status:** `disponivel`, `adotado`, `apadrinhado`
4. **Adoption Status:** `PENDENTE`, `APROVADO`, `RECUSADO`, `CONCLUIDO`
5. **Erros Comuns:**
   - 401: Token inválido ou expirado
   - 403: Acesso negado (falta permissão)
   - 404: Recurso não encontrado
   - 409: Conflito (ex: já apadrinhou este animal)

---

**Data:** 2024-01-27  
**Versão:** 1.0.0  
✅ Todos os endpoints esperados pelo frontend foram implementados!

