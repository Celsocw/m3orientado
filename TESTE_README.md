# Como Testar o Sistema Bancário

## Pré-requisitos
- JDK (Java Development Kit) instalado
- Ambiente configurado para compilar e executar Java

## Arquivos de Teste

### 1. Teste Completo (`TesteSistemaBancario.java`)
Testa todas as regras de negócio implementadas:
- Criação de contas com validações
- Operações bancárias (depósito, saque, transferência)
- Tratamento de exceções
- Emissão de extratos

### 2. Exemplo Simples (`ExemploSimples.java`)
Demonstra o uso básico do sistema com casos de sucesso e erro.

## Como Executar

### Compilação
```bash
# Compilar todas as classes
javac -cp src src/br/univali/cc/prog3/banco/dominio/*.java \
           src/br/univali/cc/prog3/banco/excecao/*.java \
           src/br/univali/cc/prog3/banco/utilitario/*.java \
           src/TesteSistemaBancario.java

# Ou para o exemplo simples:
javac -cp src src/br/univali/cc/prog3/banco/dominio/*.java \
           src/br/univali/cc/prog3/banco/excecao/*.java \
           src/br/univali/cc/prog3/banco/utilitario/*.java \
           src/ExemploSimples.java
```

### Execução

```bash
# Executar teste completo
java -cp src TesteSistemaBancario

# Executar exemplo simples
java -cp src ExemploSimples
```

## Regras de Negócio Testadas

### ✅ Conta Duplicada
- Sistema impede criação de contas com mesmo número
- Lança `ContaDuplicadaException`

### ✅ Saldo Insuficiente
- Sistema verifica saldo antes de saques/transferências
- Lança `SaldoInsuficienteException`

### ✅ Conta Inexistente
- Sistema valida existência da conta em todas operações
- Lança `ContaInexistenteException`

### ✅ Valores Negativos
- Sistema impede operações com valores negativos/zero
- Lança `ValorNegativoException`

## Estrutura das Classes

```
src/
├── br/univali/cc/prog3/banco/
│   ├── dominio/
│   │   ├── Banco.java              # Classe principal do banco
│   │   ├── ContaBancaria.java      # Classe abstrata de conta
│   │   ├── ContaCorrente.java      # Implementação de conta corrente
│   │   ├── Cliente.java            # Classe do cliente
│   │   └── *.java                  # Outras classes do domínio
│   └── excecao/
│       ├── BancoException.java     # Exceção base
│       ├── ContaDuplicadaException.java    # Nova exceção
│       ├── ValorNegativoException.java     # Nova exceção
│       └── *.java                  # Outras exceções
├── TesteSistemaBancario.java       # Teste completo
└── ExemploSimples.java             # Exemplo básico
```

## Saída Esperada

O teste completo deve mostrar:
- ✅ Operações válidas funcionando
- ✅ Exceções sendo lançadas para operações inválidas
- ✅ Extratos sendo emitidos corretamente
- ✅ Movimentações ordenadas por data

## Troubleshooting

### Erro de compilação
- Verifique se todas as dependências estão no classpath
- Certifique-se de que o JDK está instalado

### Erro de execução
- Use o classpath correto: `java -cp src NomeDaClasse`
- Verifique se não há conflitos de nomes de classe

### Classes não encontradas
- Todas as classes devem estar no diretório `src/`
- O comando `javac` deve incluir o caminho correto
