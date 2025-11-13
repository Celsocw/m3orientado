from abc import ABC, abstractmethod
from typing import Dict, Any, List, Optional
from dataclasses import dataclass
from enum import Enum


class DocumentType(Enum):
    CPF = "CPF"
    CNPJ = "CNPJ"
    NAO_INFORMADO = "NÃO INFORMADO"


@dataclass
class ValidationResult:
    is_valid: bool
    document_type: DocumentType
    error_message: Optional[str] = None


@dataclass
class ProcessedData:
    document: str
    document_type: DocumentType
    is_valid: bool
    error_message: Optional[str] = None


@dataclass
class APIResponse:
    success: bool
    data: Optional[Dict[str, Any]] = None
    error_message: Optional[str] = None
    status_code: Optional[int] = None


class IValidationStrategy(ABC):
    """Strategy interface for document validation"""
    
    @abstractmethod
    def validate(self, document: str) -> ValidationResult:
        pass
    
    @abstractmethod
    def get_document_type(self) -> DocumentType:
        pass


class ICSVProcessingStrategy(ABC):
    """Strategy interface for CSV processing"""
    
    @abstractmethod
    def process_csv(self, file_path: str) -> List[ProcessedData]:
        pass
    
    @abstractmethod
    def validate_headers(self, headers: List[str]) -> bool:
        pass


class IAPIClientStrategy(ABC):
    """Strategy interface for API communication"""
    
    @abstractmethod
    def send_data(self, data: List[ProcessedData]) -> APIResponse:
        pass
    
    @abstractmethod
    def configure(self, **kwargs) -> None:
        pass


class IValidationFactory(ABC):
    """Factory interface for creating validation strategies"""
    
    @abstractmethod
    def create_validator(self, document_type: DocumentType) -> IValidationStrategy:
        pass
    
    @abstractmethod
    def register_validator(self, document_type: DocumentType, validator: IValidationStrategy) -> None:
        pass


class ILogger(ABC):
    """Strategy interface for logging"""
    
    @abstractmethod
    def info(self, message: str) -> None:
        pass
    
    @abstractmethod
    def error(self, message: str) -> None:
        pass
    
    @abstractmethod
    def warning(self, message: str) -> None:
        pass