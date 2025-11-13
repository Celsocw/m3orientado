@echo off
echo ====================================
echo SISTEMA BANCARIO - TESTES
echo ====================================
echo.

echo Compilando classes...
javac -cp src src/br/univali/cc/prog3/banco/dominio/*.java src/br/univali/cc/prog3/banco/excecao/*.java src/br/univali/cc/prog3/banco/utilitario/*.java src/TesteSistemaBancario.java

if %errorlevel% neq 0 (
    echo ERRO: Falha na compilacao!
    pause
    exit /b 1
)

echo.
echo Executando testes completos...
java -cp src TesteSistemaBancario

echo.
echo ====================================
echo Testes finalizados!
echo ====================================
pause
