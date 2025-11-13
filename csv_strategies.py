import csv
import os
from typing import List, Dict, Any
from interfaces import ICSVProcessingStrategy, ProcessedData, IValidationStrategy, IValidationFactory, DocumentType, ILogger


class CSVProcessingStrategy(ICSVProcessingStrategy):
    """Concrete strategy for CSV processing following Single Responsibility Principle"""
    
    def __init__(self, validation_factory: IValidationFactory, logger: ILogger):
        self._validation_factory = validation_factory
        self._logger = logger
        self._required_headers = ['documento']
    
    def process_csv(self, file_path: str) -> List[ProcessedData]:
        """Process CSV file and validate documents"""
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"Arquivo não encontrado: {file_path}")
        
        processed_data = []
        
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                reader = csv.DictReader(file)
                
                # Validate headers
                if not self.validate_headers(reader.fieldnames or []):
                    raise ValueError(f"Cabeçalhos inválidos. Esperado: {self._required_headers}")
                
                for row_number, row in enumerate(reader, start=2):
                    document = row.get('documento', '').strip()
                    
                    if not document:
                        self._logger.warning(f"Linha {row_number}: documento vazio encontrado")
                        continue
                    
                    processed_item = self._process_document(document)
                    processed_data.append(processed_item)
                    
                    if processed_item.is_valid:
                        self._logger.info(f"Linha {row_number}: {processed_item.document_type.value} válido")
                    else:
                        self._logger.error(f"Linha {row_number}: {processed_item.error_message}")
        
        except Exception as e:
            self._logger.error(f"Erro ao processar arquivo CSV: {str(e)}")
            raise
        
        return processed_data
    
    def validate_headers(self, headers: List[str]) -> bool:
        """Validate CSV headers"""
        return all(header in headers for header in self._required_headers)
    
    def _process_document(self, document: str) -> ProcessedData:
        """Process individual document"""
        document_type = self._identify_document_type(document)
        validator = self._validation_factory.create_validator(document_type)
        validation_result = validator.validate(document)
        
        return ProcessedData(
            document=document,
            document_type=validation_result.document_type,
            is_valid=validation_result.is_valid,
            error_message=validation_result.error_message
        )
    
    def _identify_document_type(self, document: str) -> DocumentType:
        """Identify document type based on length and content"""
        clean_document = ''.join(c for c in document if c.isdigit())
        
        if not clean_document:
            return DocumentType.NAO_INFORMADO
        
        if len(clean_document) == 11:
            return DocumentType.CPF
        elif len(clean_document) == 14:
            return DocumentType.CNPJ
        else:
            return DocumentType.NAO_INFORMADO


class AdvancedCSVProcessingStrategy(ICSVProcessingStrategy):
    """Advanced CSV processing strategy with additional features"""
    
    def __init__(self, validation_factory: IValidationFactory, logger: ILogger, 
                 required_headers: List[str] = None, batch_size: int = 1000):
        self._validation_factory = validation_factory
        self._logger = logger
        self._required_headers = required_headers or ['documento']
        self._batch_size = batch_size
    
    def process_csv(self, file_path: str) -> List[ProcessedData]:
        """Process CSV file in batches for better memory management"""
        if not os.path.exists(file_path):
            raise FileNotFoundError(f"Arquivo não encontrado: {file_path}")
        
        processed_data = []
        batch_count = 0
        
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                reader = csv.DictReader(file)
                
                if not self.validate_headers(reader.fieldnames or []):
                    raise ValueError(f"Cabeçalhos inválidos. Esperado: {self._required_headers}")
                
                batch = []
                for row_number, row in enumerate(reader, start=2):
                    document = row.get('documento', '').strip()
                    
                    if not document:
                        self._logger.warning(f"Linha {row_number}: documento vazio encontrado")
                        continue
                    
                    processed_item = self._process_document(document)
                    batch.append(processed_item)
                    
                    if len(batch) >= self._batch_size:
                        processed_data.extend(batch)
                        batch_count += 1
                        self._logger.info(f"Processado lote {batch_count} com {len(batch)} documentos")
                        batch = []
                
                # Process remaining items
                if batch:
                    processed_data.extend(batch)
                    batch_count += 1
                    self._logger.info(f"Processado lote final {batch_count} com {len(batch)} documentos")
        
        except Exception as e:
            self._logger.error(f"Erro ao processar arquivo CSV: {str(e)}")
            raise
        
        return processed_data
    
    def validate_headers(self, headers: List[str]) -> bool:
        """Validate CSV headers with flexible matching"""
        normalized_headers = [header.lower().strip() for header in headers]
        normalized_required = [header.lower().strip() for header in self._required_headers]
        return all(req in normalized_headers for req in normalized_required)
    
    def _process_document(self, document: str) -> ProcessedData:
        """Process individual document with enhanced error handling"""
        try:
            document_type = self._identify_document_type(document)
            validator = self._validation_factory.create_validator(document_type)
            validation_result = validator.validate(document)
            
            return ProcessedData(
                document=document,
                document_type=validation_result.document_type,
                is_valid=validation_result.is_valid,
                error_message=validation_result.error_message
            )
        except Exception as e:
            self._logger.error(f"Erro ao processar documento '{document}': {str(e)}")
            return ProcessedData(
                document=document,
                document_type=DocumentType.NAO_INFORMADO,
                is_valid=False,
                error_message=f"Erro no processamento: {str(e)}"
            )
    
    def _identify_document_type(self, document: str) -> DocumentType:
        """Enhanced document type identification"""
        clean_document = ''.join(c for c in document if c.isdigit())
        
        if not clean_document or document.lower() in ['nao informado', 'não informado', '']:
            return DocumentType.NAO_INFORMADO
        
        if len(clean_document) == 11:
            return DocumentType.CPF
        elif len(clean_document) == 14:
            return DocumentType.CNPJ
        else:
            return DocumentType.NAO_INFORMADO