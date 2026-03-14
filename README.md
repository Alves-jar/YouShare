# YouShare 🎬

API REST para gerenciamento colaborativo de projetos de vídeo. Permite que criadores e editores organizem projetos, façam upload de vídeos, gerenciem versões e se comuniquem por comentários e notificações — tudo protegido por autenticação JWT.

---

## Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Início Rápido com Docker](#início-rápido-com-docker)
- [Configuração de Ambiente](#configuração-de-ambiente)
- [Executando Localmente (sem Docker)](#executando-localmente-sem-docker)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Endpoints da API](#endpoints-da-api)
- [Modelo de Dados](#modelo-de-dados)
- [Logs](#logs)
- [Variáveis de Ambiente](#variáveis-de-ambiente)

---

## Visão Geral

O YouShare é uma plataforma colaborativa onde **Creators** criam projetos de vídeo e convidam **Editors** para colaborar. Os principais recursos incluem:

- Autenticação via JWT
- CRUD completo de projetos com controle de status
- Upload e download de vídeos por projeto (até 100 MB)
- Versionamento de vídeos
- Sistema de comentários por vídeo
- Notificações de convites e atividades
- Documentação interativa via Swagger UI

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 4.0.3 | Framework principal |
| Spring Security | — | Autenticação e autorização |
| Spring Data JPA | — | Persistência |
| PostgreSQL | 16 | Banco de dados |
| Flyway | — | Migrations de banco |
| Auth0 Java JWT | 4.4.0 | Geração e validação de tokens |
| Lombok | — | Redução de boilerplate |
| SpringDoc OpenAPI | 2.8.6 | Documentação Swagger |
| Docker / Compose | — | Containerização |

---

## Pré-requisitos

- [Docker](https://docs.docker.com/get-docker/) >= 24 e [Docker Compose](https://docs.docker.com/compose/) >= 2.20
- **OU** JDK 21 + PostgreSQL 16 para execução local sem Docker

---

## Início Rápido com Docker

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/youshare.git
cd youshare

# 2. Copie e ajuste as variáveis de ambiente
cp .env.example .env
# edite .env conforme necessário (senhas, segredo JWT, etc.)

# 3. Suba todos os serviços
docker compose up --build -d

# 4. Acompanhe os logs
docker compose logs -f api

# 5. Acesse a documentação interativa
open http://localhost:8080/swagger-ui/index.html
```

Para parar:

```bash
docker compose down
```

Para parar e remover volumes (apaga dados do banco):

```bash
docker compose down -v
```

---

## Configuração de Ambiente

Todas as variáveis são definidas no arquivo `.env` na raiz do projeto. Copie o template:

```bash
cp .env.example .env
```

| Variável | Padrão     | Descrição |
|---|------------|---|
| `DB_NAME` | `youshare` | Nome do banco de dados |
| `DB_USER` | `postgres` | Usuário do PostgreSQL |
| `DB_PASSWORD` | `postgres` | Senha do PostgreSQL |
| `DB_PORT` | `5432`     | Porta exposta do PostgreSQL |
| `API_PORT` | `8080`     | Porta exposta da API |
| `JWT_SECRET` | `mysecret` | Segredo para assinar tokens JWT |
| `LOG_LEVEL_ROOT` | `INFO`     | Nível de log global |
| `LOG_LEVEL_APP` | `DEBUG`    | Nível de log da aplicação |
| `LOG_LEVEL_SECURITY` | `INFO`     | Nível de log do Spring Security |

> ⚠️ **Produção:** troque `JWT_SECRET`, `DB_PASSWORD` e use um gerenciador de segredos. Nunca comite o arquivo `.env`.

---

## Executando Localmente (sem Docker)

Certifique-se de ter um PostgreSQL rodando na porta 5432 com o banco `youshare` criado.

```bash
# Sobe apenas o banco via Docker
docker compose up postgres -d

# Executa a aplicação via Maven Wrapper
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.

---

## Estrutura do Projeto

```
youshare/
├── src/
│   └── main/
│       ├── java/com/noxus/youshare/
│       │   ├── config/           # OpenAPI config
│       │   ├── controller/       # Camada REST (AuthController, ProjectController, …)
│       │   ├── dto/              # Objetos de transferência de dados
│       │   ├── entity/           # Entidades JPA + enums
│       │   ├── exception/        # Exceções customizadas e handler global
│       │   ├── repository/       # Interfaces Spring Data JPA
│       │   ├── security/         # JWT + SecurityConfig + SecurityFilter
│       │   └── service/          # Lógica de negócio
│       └── resources/
│           ├── application.yaml  # Configuração principal
│           └── db/migration/     # Scripts Flyway (V1…V4)
├── Dockerfile                    # Build multi-stage
├── docker-compose.yml            # API + PostgreSQL
├── .env.example                  # Template de variáveis
├── .dockerignore
└── pom.xml
```

---

## Endpoints da API

A documentação completa e interativa está disponível em:

```
http://localhost:8080/swagger-ui/index.html
```

### Autenticação

| Método | Rota | Descrição | Auth |
|---|---|---|---|
| `POST` | `/auth/register` | Cria novo usuário | ✗ |
| `POST` | `/auth/login` | Autentica e retorna JWT | ✗ |

Todos os demais endpoints requerem o header:

```
Authorization: Bearer <token>
```

### Projetos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/projects` | Cria um projeto |
| `GET` | `/projects` | Lista projetos do usuário |
| `GET` | `/projects/{id}` | Detalha um projeto |
| `PUT` | `/projects/{id}` | Atualiza um projeto |
| `DELETE` | `/projects/{id}` | Remove um projeto |

### Membros do Projeto

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/projects/{id}/members/invite` | Convida um usuário |
| `GET` | `/projects/{id}/members` | Lista membros |
| `DELETE` | `/projects/{id}/members/{userId}` | Remove membro |

### Vídeos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/videos/projects/{projectId}` | Faz upload de vídeo |
| `GET` | `/api/videos/projects/{projectId}` | Lista vídeos do projeto |
| `GET` | `/api/videos/{videoId}` | Detalha um vídeo |
| `GET` | `/api/videos/{videoId}/download` | Baixa o arquivo de vídeo |
| `DELETE` | `/api/videos/{videoId}` | Remove um vídeo |

### Versões de Vídeo, Comentários e Notificações

Consulte o Swagger UI para os endpoints completos de `/api/videos/{id}/versions`, `/api/videos/{id}/comments` e `/notifications`.

---

## Modelo de Dados

```
users
 └─< projects (creator_id)
      └─< project_members (user_id, project_id)
      └─< videos (project_id, uploaded_by)
           └─< video_versions
           └─< comments (user_id, video_id)

notifications (user_id, related_project_id)
```

### Enums relevantes

| Enum | Valores |
|---|---|
| `UserRole` | `CREATOR`, `EDITOR` |
| `ProjectStatus` | `OPEN`, `IN_EDITING`, `REVIEW`, `COMPLETED` |
| `MemberRole` | `OWNER`, `EDITOR` |
| `InviteStatus` | `PENDING`, `ACCEPTED`, `DECLINED` |
| `VersionStatus` | `DRAFT`, `APPROVED`, `REJECTED` |
| `NotificationType` | `EDITOR_INVITE`, `NEW_VERSION`, `NEW_COMMENT` |

---

## Logs

### Configuração

Os logs seguem a configuração definida em `application.yaml` e sobrescrevível por variáveis de ambiente:

| Nível | Pacote/Variável |
|---|---|
| `LOG_LEVEL_ROOT` | Log global (default `INFO`) |
| `LOG_LEVEL_APP` | `com.noxus.youshare` (default `DEBUG`) |
| `LOG_LEVEL_SECURITY` | Spring Security (default `INFO`) |

### Rotação de arquivos

- **Arquivo ativo:** `logs/youshare.log`
- **Rotação:** diária + quando atingir 10 MB
- **Histórico:** 30 arquivos compactados em `.log.gz`
- **Limite total:** 300 MB

### Comandos úteis

```bash
# Logs em tempo real do container da API
docker compose logs -f api

# Últimas 100 linhas
docker compose logs --tail=100 api

# Inspecionar arquivo de log dentro do container
docker exec youshare-api cat /app/logs/youshare.log

# Logs do banco
docker compose logs -f postgres
```

### Formato

**Console** (colorido):
```
2025-01-15 10:23:45.123  INFO 1 --- [main] c.n.youshare.YoushareApplication : Started YoushareApplication in 4.321 seconds
```

**Arquivo** (plain text):
```
2025-01-15 10:23:45.123 [main] INFO  c.n.youshare.YoushareApplication - Started YoushareApplication in 4.321 seconds
```

---

## Variáveis de Ambiente

Resumo completo de todas as variáveis suportadas via `application.yaml` com fallback para valores locais:

| Variável de Ambiente | Property Spring | Padrão |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `spring.datasource.url` | `jdbc:postgresql://localhost:5432/youshare` |
| `SPRING_DATASOURCE_USERNAME` | `spring.datasource.username` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | `spring.datasource.password` | `postgres` |
| `API_SECURITY_TOKEN_SECRET` | `api.security.token.secret` | *(valor padrão)* |
| `LOGGING_LEVEL_ROOT` | `logging.level.root` | `INFO` |
| `LOGGING_LEVEL_COM_NOXUS_YOUSHARE` | `logging.level.com.noxus.youshare` | `DEBUG` |
| `LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_SECURITY` | `logging.level.org.springframework.security` | `INFO` |
| `LOGGING_FILE_NAME` | `logging.file.name` | `logs/youshare.log` |
| `SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE` | `spring.servlet.multipart.max-file-size` | `100MB` |