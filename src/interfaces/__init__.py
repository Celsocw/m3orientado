from abc import ABC, abstractmethod
from typing import Any, Dict, List, Optional, Union
from dataclasses import dataclass
from enum import Enum


class ValidationStatus(Enum):
    VALID = "valid"
    INVALID = "invalid"
    ERROR = "error"


@dataclass
class ValidationResult:
    status: ValidationStatus
    value: str
    message: Optional[str] = None
    metadata: Optional[Dict[str, Any]] = None


@dataclass
class CSVRow:
    data: Dict[str, Any]
    line_number: int
    raw_content: str


@dataclass
class ProcessedData:
    headers: List[str]
    rows: List[CSVRow]
    total_rows: int
    valid_rows: int
    invalid_rows: int


@dataclass
class APIResponse:
    success: bool
    data: Optional[Any] = None
    error: Optional[str] = None
    status_code: Optional[int] = None
    metadata: Optional[Dict[str, Any]] = None


class IValidator(ABC):
    """Interface para estratégias de validação"""
    
    @abstractmethod
    def validate(self, value: str) -> ValidationResult:
        """Valida um valor e retorna o resultado"""
        pass
    
    @abstractmethod
    def get_validator_name(self) -> str:
        """Retorna o nome do validador"""
        pass


class ICSVProcessor(ABC):
    """Interface para estratégias de processamento de CSV"""
    
    @abstractmethod
    def process(self, csv_content: str) -> ProcessedData:
        """Processa o conteúdo CSV e retorna os dados estruturados"""
        pass
    
    @abstractmethod
    def validate_headers(self, headers: List[str]) -> bool:
        """Valida se os headers são esperados"""
        pass


class IAPIClient(ABC):
    """Interface para estratégias de cliente API"""
    
    @abstractmethod
    def send_request(self, endpoint: str, data: Dict[str, Any]) -> APIResponse:
        """Envia uma requisição para a API"""
        pass
    
    @abstractmethod
    def get_base_url(self) -> str:
        """Retorna a URL base da API"""
        pass


class ILogger(ABC):
    """Interface para sistema de logging"""
    
    @abstractmethod
    def info(self, message: str, **kwargs) -> None:
        """Registra mensagem informativa"""
        pass
    
    @abstractmethod
    def error(self, message: str, error: Optional[Exception] = None, **kwargs) -> None:
        """Registra mensagem de erro"""
        pass
    
    @abstractmethod
    def warning(self, message: str, **kwargs) -> None:
        """Registra mensagem de aviso"""
        pass
    
    @abstractmethod
    def debug(self, message: str, **kwargs) -> None:
        """Registra mensagem de debug"""
        pass