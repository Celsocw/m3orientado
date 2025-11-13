import re
from typing import Optional
from ..interfaces import IValidator, ValidationResult, ValidationStatus
from ..exceptions import ValidationException


class BaseValidator(IValidator):
    """Classe base para validadores com funcionalidades comuns"""
    
    def __init__(self, name: str):
        self._name = name
    
    def get_validator_name(self) -> str:
        return self._name
    
    def _clean_value(self, value: str) -> str:
        """Remove caracteres não numéricos"""
        return re.sub(r'[^\d]', '', value)
    
    def _create_result(self, status: ValidationStatus, value: str, 
                      message: Optional[str] = None, 
                      metadata: Optional[dict] = None) -> ValidationResult:
        """Cria um ValidationResult padronizado"""
        return ValidationResult(
            status=status,
            value=value,
            message=message,
            metadata=metadata or {}
        )


class CPFValidator(BaseValidator):
    """Estratégia de validação para CPF"""
    
    def __init__(self):
        super().__init__("CPF")
    
    def validate(self, value: str) -> ValidationResult:
        try:
            if not value:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "CPF não pode ser vazio"
                )
            
            clean_cpf = self._clean_value(value)
            
            if len(clean_cpf) != 11:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "CPF deve ter 11 dígitos"
                )
            
            # Verificar CPFs com dígitos repetidos
            if clean_cpf == clean_cpf[0] * 11:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "CPF com dígitos repetidos é inválido"
                )
            
            # Calcular dígitos verificadores
            if not self._calculate_verifiers(clean_cpf):
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "CPF inválido - dígitos verificadores não conferem"
                )
            
            return self._create_result(
                ValidationStatus.VALID,
                value,
                "CPF válido",
                {"clean_cpf": clean_cpf}
            )
            
        except Exception as e:
            return self._create_result(
                ValidationStatus.ERROR,
                value,
                f"Erro na validação do CPF: {str(e)}"
            )
    
    def _calculate_verifiers(self, cpf: str) -> bool:
        """Calcula e verifica os dígitos verificadores do CPF"""
        # Primeiro dígito verificador
        sum_ = 0
        for i in range(9):
            sum_ += int(cpf[i]) * (10 - i)
        remainder = sum_ % 11
        first_digit = 0 if remainder < 2 else 11 - remainder
        
        # Segundo dígito verificador
        sum_ = 0
        for i in range(10):
            sum_ += int(cpf[i]) * (11 - i)
        remainder = sum_ % 11
        second_digit = 0 if remainder < 2 else 11 - remainder
        
        return int(cpf[9]) == first_digit and int(cpf[10]) == second_digit


class CNPJValidator(BaseValidator):
    """Estratégia de validação para CNPJ"""
    
    def __init__(self):
        super().__init__("CNPJ")
    
    def validate(self, value: str) -> ValidationResult:
        try:
            if not value:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "CNPJ não pode ser vazio"
                )
            
            clean_cnpj = self._clean_value(value)
            
            if len(clean_cnpj) != 14:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "CNPJ deve ter 14 dígitos"
                )
            
            # Verificar CNPJs com dígitos repetidos
            if clean_cnpj == clean_cnpj[0] * 14:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "CNPJ com dígitos repetidos é inválido"
                )
            
            # Calcular dígitos verificadores
            if not self._calculate_verifiers(clean_cnpj):
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "CNPJ inválido - dígitos verificadores não conferem"
                )
            
            return self._create_result(
                ValidationStatus.VALID,
                value,
                "CNPJ válido",
                {"clean_cnpj": clean_cnpj}
            )
            
        except Exception as e:
            return self._create_result(
                ValidationStatus.ERROR,
                value,
                f"Erro na validação do CNPJ: {str(e)}"
            )
    
    def _calculate_verifiers(self, cnpj: str) -> bool:
        """Calcula e verifica os dígitos verificadores do CNPJ"""
        # Pesos para o primeiro dígito
        weights_first = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
        # Pesos para o segundo dígito
        weights_second = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
        
        # Calcular primeiro dígito verificador
        sum_ = 0
        for i in range(12):
            sum_ += int(cnpj[i]) * weights_first[i]
        remainder = sum_ % 11
        first_digit = 0 if remainder < 2 else 11 - remainder
        
        # Calcular segundo dígito verificador
        sum_ = 0
        for i in range(13):
            sum_ += int(cnpj[i]) * weights_second[i]
        remainder = sum_ % 11
        second_digit = 0 if remainder < 2 else 11 - remainder
        
        return int(cnpj[12]) == first_digit and int(cnpj[13]) == second_digit


class EmailValidator(BaseValidator):
    """Estratégia de validação para email"""
    
    def __init__(self):
        super().__init__("Email")
    
    def validate(self, value: str) -> ValidationResult:
        try:
            if not value:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "Email não pode ser vazio"
                )
            
            email = value.strip().lower()
            
            # Regex básica para validação de email
            pattern = r'^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$'
            
            if not re.match(pattern, email):
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "Formato de email inválido"
                )
            
            # Verificações adicionais
            if email.startswith('.') or email.endswith('.'):
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "Email não pode começar ou terminar com ponto"
                )
            
            if '..' in email:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "Email não pode conter pontos consecutivos"
                )
            
            return self._create_result(
                ValidationStatus.VALID,
                value,
                "Email válido",
                {"normalized_email": email}
            )
            
        except Exception as e:
            return self._create_result(
                ValidationStatus.ERROR,
                value,
                f"Erro na validação do email: {str(e)}"
            )


class PhoneValidator(BaseValidator):
    """Estratégia de validação para telefone brasileiro"""
    
    def __init__(self):
        super().__init__("Phone")
    
    def validate(self, value: str) -> ValidationResult:
        try:
            if not value:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "Telefone não pode ser vazio"
                )
            
            clean_phone = self._clean_value(value)
            
            # Verificar comprimentos válidos (com e sem DDD)
            if len(clean_phone) not in [10, 11]:
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "Telefone deve ter 10 ou 11 dígitos (com DDD)"
                )
            
            # Verificar DDD válido
            ddd = clean_phone[:2]
            if not self._is_valid_ddd(ddd):
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    f"DDD {ddd} inválido"
                )
            
            # Verificar se não é um número com todos os dígitos iguais
            phone_number = clean_phone[2:]
            if phone_number == phone_number[0] * len(phone_number):
                return self._create_result(
                    ValidationStatus.INVALID,
                    value,
                    "Número de telefone com dígitos repetidos é inválido"
                )
            
            return self._create_result(
                ValidationStatus.VALID,
                value,
                "Telefone válido",
                {
                    "clean_phone": clean_phone,
                    "ddd": ddd,
                    "number": phone_number,
                    "is_mobile": len(clean_phone) == 11
                }
            )
            
        except Exception as e:
            return self._create_result(
                ValidationStatus.ERROR,
                value,
                f"Erro na validação do telefone: {str(e)}"
            )
    
    def _is_valid_ddd(self, ddd: str) -> bool:
        """Verifica se o DDD é válido no Brasil"""
        valid_ddds = {
            '11', '12', '13', '14', '15', '16', '17', '18', '19',  # São Paulo
            '21', '22', '24',  # Rio de Janeiro
            '27', '28',  # Espírito Santo
            '31', '32', '33', '34', '35', '37', '38',  # Minas Gerais
            '41', '42', '43', '44', '45', '46',  # Paraná
            '47', '48', '49',  # Santa Catarina
            '51', '53', '54', '55',  # Rio Grande do Sul
            '61',  # Distrito Federal
            '62', '64',  # Goiás
            '63',  # Tocantins
            '65', '66',  # Mato Grosso
            '67',  # Mato Grosso do Sul
            '68',  # Acre
            '69',  # Rondônia
            '71', '73', '74', '75', '77',  # Bahia
            '79',  # Sergipe
            '81', '82', '83', '87',  # Pernambuco
            '84',  # Rio Grande do Norte
            '85', '88',  # Ceará
            '86', '89',  # Piauí
            '91', '93', '94',  # Pará
            '92', '97', '98', '99',  # Amazonas
            '95',  # Amapá
            '96'  # Amapá
        }
        return ddd in valid_ddds