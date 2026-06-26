# zeemsv-api

Projeto Spring Boot base para ZEEMSV seguindo o modelo:

```text
Controller
   ↓
DTO
   ↓
Service / ServiceImpl
   ↓
Mapper
   ↓
Model
   ↓
Bus / BusImpl
   ↓
Mapper
   ↓
Entity
   ↓
Repository
   ↓
DB
```

## Estrutura adotada

```text
src/main/java/cv/zeemsv/api
├── application        # DTO, service e mapper da camada de aplicação
├── domain             # model e business/bus
├── infrastructure     # entity, mapper entity/model e repository
├── interfaces         # response envelope e tratamento de erro
├── web                # controllers REST
├── config
└── security
```

## Executar localmente

```bash
java -jar target/zeemsv-api-0.0.1-SNAPSHOT.jar
```

Swagger:

```text
http://localhost:8086/zeemsv-api/swagger-ui.html
```

Healthcheck:

```text
http://localhost:8086/zeemsv-api/actuator/health
```

## Nota técnica importante

As entidades foram geradas como esqueleto a partir do ficheiro `docs/zeemsv_v2.erd`. Esse ERD tem os nomes das tabelas e relações, mas não inclui as colunas completas. Antes de usar em produção, substituir os esqueletos das entidades pelo mapeamento real vindo do DDL PostgreSQL.
