class ValidationException(Exception):
    """Exceção levantada durante validação de dados"""
    
    def __init__(self, message: str, field: str = None, value: str = None):
        self.field = field
        self.value = value
        super().__init__(message)


class CSVProcessingException(Exception):
    """Exceção levantada durante processamento de CSV"""
    
    def __init__(self, message: str, line_number: int = None, csv_content: str = None):
        self.line_number = line_number
        self.csv_content = csv_content
        super().__init__(message)


class APIException(Exception):
    """Exceção levantada durante chamadas de API"""
    
    def __init__(self, message: str, status_code: int = None, endpoint: str = None):
        self.status_code = status_code
        self.endpoint = endpoint
        super().__init__(message)


class ConfigurationException(Exception):
    """Exceção levantada para erros de configuração"""
    pass


class DependencyInjectionException(Exception):
    """Exceção levantada para erros de injeção de dependência"""
    pass