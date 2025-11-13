# Projeto de Processamento de Dados com Strategy Pattern

Este projeto implementa um sistema de processamento de dados utilizando o padrão Strategy e princípios SOLID para validação de documentos, processamento de CSV e consumo de APIs.

## Estrutura do Projeto

```
src/
├── interfaces/           # Interfaces e contratos
├── strategies/           # Implementações das estratégias
│   ├── validation/       # Estratégias de validação
│   ├── csv/              # Estratégias de processamento CSV
│   └── api/              # Estratégias de cliente API
├── services/             # Classes de serviço
├── utils/                # Utilitários
├── exceptions/           # Exceções customizadas
└── tests/               # Testes unitários
```

## Principais Features

- ✅ Validação de CPF e CNPJ
- ✅ Processamento de CSV com diferentes estratégias
- ✅ Clientes API com retry e fallback
- ✅ Injeção de dependências
- ✅ Tratamento robusto de erros
- ✅ Logging estruturado
- ✅ Testes unitários abrangentes

## Como Usar

```python
from services import DataProcessingService
from strategies import CPFValidator, CNPJValidator, CSVProcessor, APIClient

# Criar serviço com injeção de dependências
service = DataProcessingService(
    validators=[CPFValidator(), CNPJValidator()],
    csv_processor=CSVProcessor(),
    api_client=APIClient()
)

# Processar dados
result = service.process_data("arquivo.csv")
```