# 🚀 Microsserviço de Usuários - Sistema de Votação

Microsserviço responsável pelo cadastro, validação de credenciais e gerenciamento de usuários dentro do ecossistema do Sistema de Votação.

Este serviço é projetado para operar de forma independente, interagindo com outros componentes da arquitetura através de um Service Discovery (Eureka). Ele está implantado na plataforma **Render**, com banco de dados e monitoramento totalmente gerenciados na nuvem.

## 📝 Status do Projeto

| Funcionalidade | Status | Detalhes |
| :--- | :--- | :--- |
| **Cadastro de Usuários** | ✅ Concluído | Permite registro com e-mail único e whitelist. |
| **Login (Validação)** | ✅ Concluído | Valida credenciais e retorna dados do usuário. |
| **Gerenciamento (CRUD)** | ✅ Concluído | Operações de busca e exclusão com controle de acesso. |
| **Segurança** | ✅ Concluído | Modelo de pré-autenticação via API Gateway. |
| **Monitoramento** | ✅ Concluído | Métricas customizadas enviadas ao Grafana Cloud. |
| **Testes (Unitários/Integração)** | 🟡 **Pendente** | A cobertura de testes ainda não foi implementada. |

-----

## 🏗️ Arquitetura e Conceitos Chave

### Modelo de Segurança: Pré-Autenticação

Diferente de uma abordagem tradicional com JWT, este serviço **não gera ou gerencia tokens de autenticação**. Ele opera em um modelo de **pré-autenticação**, esperando que um componente upstream (como um API Gateway) valide o usuário e repasse as informações de identidade através de headers HTTP.

  - **`X-User-Id`**: O ID do usuário já autenticado.
  - **`X-User-Role`**: A `Role` (ex: `USER`, `ADMIN`) do usuário.

O `Spring Security` é configurado para ler esses cabeçalhos, criar um contexto de segurança para a requisição e autorizar o acesso aos endpoints com base na `Role` recebida.

### Service Discovery

O serviço se registra no **Spring Cloud Eureka** para ser descoberto por outras aplicações do sistema, como o API Gateway e outros microsserviços.

### Monitoramento Centralizado com Grafana Cloud

As métricas da aplicação são coletadas pelo **Micrometer** e expostas via **Spring Actuator**. Além disso, o serviço está configurado com `remote-write` para enviar todas as métricas diretamente para uma instância do **Grafana Cloud**, permitindo a criação de dashboards e alertas centralizados.

-----

## ⚙️ Tecnologias Utilizadas

| Categoria | Tecnologias |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.2.5, Spring Security, Spring Data JPA |
| **Banco de Dados** | PostgreSQL (Hospedado na **Render**) |
| **Monitoramento** | Micrometer, Prometheus, Spring Actuator, **Grafana Cloud (Remote-Write)** |
| **Infra & Deploy** | Docker, **Render** |
| **Service Discovery**| Spring Cloud Netflix Eureka |
| **Documentação** | Springdoc (Swagger/OpenAPI) |

-----

## 🔌 Endpoints da API

A documentação completa e interativa está disponível via Swagger em `/swagger-ui.html`.

| Método | Endpoint | Descrição | Acesso |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/users/register` | Registra um novo usuário. | **Público** |
| `POST` | `/api/users/login` | Valida as credenciais. Retorna o ID e a Role do usuário. | **Público** |
| `POST` | `/api/users/logout` | Endpoint para processo de logout (lógica no frontend/gateway).| **Autenticado**|
| `GET` | `/api/users` | Lista todos os usuários cadastrados. | **ADMIN** |
| `GET` | `/api/users/{id}` | Busca um usuário específico pelo seu ID. | **USER**, **ADMIN**|
| `GET` | `/api/users/userName/{userName}`| Busca um usuário específico pelo seu nome de usuário. | **ADMIN** |
| `DELETE`| `/api/users/{id}` | Deleta um usuário pelo seu ID. | **ADMIN** |
| `DELETE`| `/api/users/userName/{userName}`| Deleta um usuário pelo seu nome de usuário. | **ADMIN** |

**Exemplo de Resposta do Login:**
O endpoint de login **NÃO retorna um token JWT**. Ele serve para validar as credenciais e retornar os dados que o API Gateway usará para gerar o token.

```json
POST /api/users/login

// Resposta em caso de sucesso
{
    "message": "Login successful",
    "userId": 123,
    "role": "ADMIN"
}
```

-----

## 📊 Monitoramento e Métricas

O serviço expõe métricas para o Prometheus no endpoint `/actuator/prometheus`.

#### Métricas Customizadas Coletadas:

  - `usuario_registro_total` (Contador): Registros de usuários, com tags para `status` (sucesso/falha) e `reason`.
  - `usuario_login_total` (Contador): Tentativas de login, com tags para `status` e `reason`.
  - `usuario_registro_tempo` (Timer): Tempo de execução para registrar um novo usuário.
  - `usuario_login_tempo` (Timer): Tempo de execução para validar um login.
  - `usuarios_operacoes_chamadas` (Contador): Chamadas para operações de CRUD, com tag `operacao`.
  - `usuarios_operacoes_tempo` (Timer): Duração das operações de CRUD.

-----

## 🛠️ Configuração e Variáveis de Ambiente

Para rodar a aplicação, as seguintes variáveis de ambiente devem ser configuradas:

| Variável | Descrição | Exemplo |
| :--- | :--- | :--- |
| `PORT` | Porta em que o serviço irá rodar. | `8083` |
| `SPRING_DATASOURCE_URL`| URL de conexão com o banco de dados PostgreSQL. | `jdbc:postgresql://host:port/dbname`|
| `SPRING_DATASOURCE_USERNAME`| Usuário do banco de dados. | `user_eleicoes` |
| `SPRING_DATASOURCE_PASSWORD`| Senha do banco de dados. | `senha_segura` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`| URL do servidor Eureka. | `https://voting-system-discovery.onrender.com/eureka/` |
| `PROMETHEUS_REMOTE_URL` | URL do `remote-write` do Grafana Cloud. | `https://prometheus-prod-XXX.grafana.net/api/prom/push`|
| `PROMETHEUS_REMOTE_USER`| Usuário para autenticação no Grafana Cloud. | `123456` |
| `PROMETHEUS_REMOTE_PASSWORD`| Senha/Token da API do Grafana Cloud. | `API_KEY_GRAFANA` |
| `REGISTRATION_WHITELIST_EMAILS`| Lista de e-mails (separados por vírgula) autorizados a se registrar.| `admin@test.com,user1@test.com`|

-----

## 🐳 Como Executar (Docker)

O projeto inclui um `Dockerfile` multi-stage para criar uma imagem otimizada.

**1. Pré-requisitos:**

  * Docker instalado.
  * Java 21 e Maven (apenas para build local fora do Docker).

**2. Construindo a Imagem Docker:**
Navegue até a raiz do projeto e execute o comando:

```bash
docker build -t voting-system/user-service .
```

**3. Rodando o Container:**
Execute a imagem criada, passando as variáveis de ambiente necessárias.

```bash
docker run -p 8083:8083 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://..." \
  -e SPRING_DATASOURCE_USERNAME="user" \
  -e SPRING_DATASOURCE_PASSWORD="pass" \
  -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="..." \
  --name user-service \
  voting-system/user-service
```

A aplicação estará disponível em `http://localhost:8083`.

-----

## 📜 Licença

Distribuído sob a licença MIT. Veja `LICENSE` para mais informações.