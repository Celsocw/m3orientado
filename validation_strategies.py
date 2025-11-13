import re
from typing import Optional
from interfaces import IValidationStrategy, ValidationResult, DocumentType


class BaseValidationStrategy(IValidationStrategy):
    """Base class for validation strategies following Template Method pattern"""
    
    def validate(self, document: str) -> ValidationResult:
        """Template method defining validation algorithm"""
        cleaned_document = self._clean_document(document)
        
        if not self._validate_format(cleaned_document):
            return ValidationResult(
                is_valid=False,
                document_type=self.get_document_type(),
                error_message=f"Formato inválido para {self.get_document_type().value}"
            )
        
        if not self._validate_digits(cleaned_document):
            return ValidationResult(
                is_valid=False,
                document_type=self.get_document_type(),
                error_message=f"Dígitos verificadores inválidos para {self.get_document_type().value}"
            )
        
        return ValidationResult(
            is_valid=True,
            document_type=self.get_document_type()
        )
    
    def _clean_document(self, document: str) -> str:
        """Remove non-digit characters"""
        return re.sub(r'\D', '', document)
    
    @abstractmethod
    def _validate_format(self, document: str) -> bool:
        """Validate document format and length"""
        pass
    
    @abstractmethod
    def _validate_digits(self, document: str) -> bool:
        """Validate check digits"""
        pass


class CPFValidationStrategy(BaseValidationStrategy):
    """Concrete strategy for CPF validation"""
    
    def get_document_type(self) -> DocumentType:
        return DocumentType.CPF
    
    def _validate_format(self, document: str) -> bool:
        """Validate CPF format (11 digits)"""
        return len(document) == 11 and not self._has_repeated_digits(document)
    
    def _validate_digits(self, document: str) -> bool:
        """Validate CPF check digits"""
        if len(document) != 11:
            return False
        
        # Calculate first check digit
        first_digit = self._calculate_check_digit(document[:9], [10, 9, 8, 7, 6, 5, 4, 3, 2])
        if first_digit != int(document[9]):
            return False
        
        # Calculate second check digit
        second_digit = self._calculate_check_digit(document[:10], [11, 10, 9, 8, 7, 6, 5, 4, 3, 2])
        return second_digit == int(document[10])
    
    def _calculate_check_digit(self, digits: str, weights: list) -> int:
        """Calculate check digit using modulo 11"""
        total = sum(int(digit) * weight for digit, weight in zip(digits, weights))
        remainder = total % 11
        return 0 if remainder < 2 else 11 - remainder
    
    def _has_repeated_digits(self, document: str) -> bool:
        """Check if all digits are the same"""
        return len(set(document)) == 1


class CNPJValidationStrategy(BaseValidationStrategy):
    """Concrete strategy for CNPJ validation"""
    
    def get_document_type(self) -> DocumentType:
        return DocumentType.CNPJ
    
    def _validate_format(self, document: str) -> bool:
        """Validate CNPJ format (14 digits)"""
        return len(document) == 14 and not self._has_repeated_digits(document)
    
    def _validate_digits(self, document: str) -> bool:
        """Validate CNPJ check digits"""
        if len(document) != 14:
            return False
        
        # Calculate first check digit
        first_digit = self._calculate_check_digit(document[:12], [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2])
        if first_digit != int(document[12]):
            return False
        
        # Calculate second check digit
        second_digit = self._calculate_check_digit(document[:13], [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2])
        return second_digit == int(document[13])
    
    def _calculate_check_digit(self, digits: str, weights: list) -> int:
        """Calculate check digit using modulo 11"""
        total = sum(int(digit) * weight for digit, weight in zip(digits, weights))
        remainder = total % 11
        return 0 if remainder < 2 else 11 - remainder
    
    def _has_repeated_digits(self, document: str) -> bool:
        """Check if all digits are the same"""
        return len(set(document)) == 1


class NotInformedValidationStrategy(IValidationStrategy):
    """Strategy for documents not informed"""
    
    def validate(self, document: str) -> ValidationResult:
        return ValidationResult(
            is_valid=True,
            document_type=DocumentType.NAO_INFORMADO
        )
    
    def get_document_type(self) -> DocumentType:
        return DocumentType.NAO_INFORMADO