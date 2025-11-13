Sistema Bancário Orientado a Objetos

Este projeto implementa um sistema bancário simples em Java, demonstrando conceitos de orientação a objetos, tratamento de exceções e uso da Collections API.

Funcionalidades

Regras de Negócio Implementadas
- Contas únicas: Não permite contas com números duplicados
- Saldo insuficiente: Valida saldo antes de saques e transferências
- Contas inexistentes: Verifica existência da conta em todas operações
- Valores positivos: Impede operações com valores negativos ou zero

Operações Bancárias
- Criar contas correntes (com/sem limite)
- Depósitos
- Saques
- Transferências entre contas
- Emissão de extratos com movimentações ordenadas

Arquitetura

Classes Principais
- Banco: Gerencia contas e operações
- ContaBancaria: Classe abstrata base
- ContaCorrente: Implementação concreta
- Cliente: Dados do titular

Tratamento de Exceções
- BancoException: Classe base
- ContaDuplicadaException: Conta já existe
- SaldoInsuficienteException: Saldo insuficiente
- ContaInexistenteException: Conta não encontrada
- ValorNegativoException: Valores inválidos

Collections API
- HashMap<Integer, ContaBancaria>: Armazenamento eficiente de contas
- List<ExtratoEntry> com ordenação: Movimentações organizadas por data

Como Testar

Pré-requisitos
- JDK 17 ou superior instalado
- Ambiente configurado (JAVA_HOME e PATH)

Execução Local

javac -cp src src/br/univali/cc/prog3/banco/dominio/*.java \
           src/br/univali/cc/prog3/banco/excecao/*.java \
           src/br/univali/cc/prog3/banco/utilitario/*.java \
           src/TesteSistemaBancario.java

java -cp src TesteSistemaBancario

javac -cp src src/br/univali/cc/prog3/banco/dominio/*.java \
           src/br/univali/cc/prog3/banco/excecao/*.java \
           src/br/univali/cc/prog3/banco/utilitario/*.java \
           src/ExemploSimples.java
java -cp src ExemploSimples

Testes Automáticos (GitHub Actions)
O projeto inclui CI/CD que executa automaticamente:
- Compilação do código Java
- Execução dos testes de validação
- Verificação das regras de negócio

Estrutura do Projeto

src/
├── br/univali/cc/prog3/banco/
│   ├── dominio/
│   │   ├── Banco.java              # Classe principal
│   │   ├── ContaBancaria.java      # Classe abstrata
│   │   ├── ContaCorrente.java      # Implementação
│   │   ├── Cliente.java            # Dados do cliente
│   │   └── ExtratoEntry.java       # Movimentações
│   └── excecao/
│       ├── BancoException.java
│       ├── ContaDuplicadaException.java    # Nova
│       ├── ValorNegativoException.java     # Nova
│       └── ...
├── TesteSistemaBancario.java       # Testes completos
└── ExemploSimples.java             # Exemplo básico

Demonstração das Regras

O sistema demonstra as 3 etapas do tratamento de exceções:

1. Descobrir o erro: Validações condicionais
2. Lançar a exceção: throw new TipoExcecao(mensagem)
3. Tratar a exceção: Documentação com @throws

Exemplo de Tratamento

if (contas.containsKey(numero_conta)) {
    throw new ContaDuplicadaException("Já existe uma conta com o número " + numero_conta);
}

Resultado dos Testes

Ao executar os testes, você verá:
- Operações válidas funcionando
- Tentativas de operações inválidas → Exceções apropriadas
- Extratos com movimentações ordenadas por data
- Validação robusta de todas as regras de negócio

Requisitos Atendidos

- Tratamento de exceções para regras de negócio
- Uso da Collections API em vez de arrays
- Validação de contas duplicadas
- Controle de saldos insuficientes
- Verificação de contas inexistentes
- Bloqueio de valores negativos
- Movimentações ordenadas por data

Status: Projeto completo e funcional
Testes: Implementados e passando
CI/CD: Configurado no GitHub Actions