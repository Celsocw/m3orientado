import requests
import json
import hashlib
import hmac
import logging
from datetime import datetime, timedelta
from typing import Dict, Optional, Any
from web3 import Web3
from web3.middleware import geth_poa_middleware
from eth_account import Account
import time

logger = logging.getLogger(__name__)

class SBPService:
    def __init__(self):
        self.sbp_api_url = "https://api.sbp.gov.br/v1"
        self.api_key = os.getenv('SBP_API_KEY')
        self.api_secret = os.getenv('SBP_API_SECRET')
        
        self.blockchain_rpc_url = os.getenv('BLOCKCHAIN_RPC_URL', 'https://mainnet.infura.io/v3/YOUR_PROJECT_ID')
        self.contract_address = os.getenv('SBP_CONTRACT_ADDRESS')
        self.private_key = os.getenv('BLOCKCHAIN_PRIVATE_KEY')
        
        self.web3 = Web3(Web3.HTTPProvider(self.blockchain_rpc_url))
        if self.web3.is_connected():
            logger.info("Connected to blockchain network")
        else:
            logger.error("Failed to connect to blockchain network")
    
    def _generate_signature(self, method: str, endpoint: str, payload: str = '') -> str:
        timestamp = str(int(time.time()))
        message = f"{method}{endpoint}{payload}{timestamp}"
        signature = hmac.new(
            self.api_secret.encode('utf-8'),
            message.encode('utf-8'),
            hashlib.sha256
        ).hexdigest()
        return f"{timestamp}:{signature}"
    
    def _make_api_request(self, method: str, endpoint: str, data: Dict = None) -> Optional[Dict]:
        try:
            url = f"{self.sbp_api_url}{endpoint}"
            headers = {
                'Content-Type': 'application/json',
                'X-API-Key': self.api_key
            }
            
            if data:
                payload = json.dumps(data)
                signature = self._generate_signature(method, endpoint, payload)
                headers['X-Signature'] = signature
            else:
                signature = self._generate_signature(method, endpoint)
                headers['X-Signature'] = signature
            
            if method == 'GET':
                response = requests.get(url, headers=headers, timeout=30)
            elif method == 'POST':
                response = requests.post(url, headers=headers, json=data, timeout=30)
            else:
                raise ValueError(f"Unsupported HTTP method: {method}")
            
            if response.status_code == 200:
                return response.json()
            else:
                logger.error(f"SBP API error: {response.status_code} - {response.text}")
                return None
                
        except requests.exceptions.RequestException as e:
            logger.error(f"Request error: {str(e)}")
            return None
        except Exception as e:
            logger.error(f"Unexpected error in API request: {str(e)}")
            return None
    
    def query_contract(self, contract_number: str, document_type: str, document_number: str) -> Optional[Dict]:
        try:
            endpoint = f"/contracts/{contract_number}"
            params = {
                'document_type': document_type,
                'document_number': document_number
            }
            
            contract_data = self._make_api_request('GET', endpoint, params)
            
            if contract_data:
                blockchain_data = self._verify_contract_on_blockchain(contract_number)
                
                if blockchain_data:
                    contract_data['blockchain_verification'] = {
                        'verified': True,
                        'block_number': blockchain_data.get('block_number'),
                        'transaction_hash': blockchain_data.get('transaction_hash'),
                        'timestamp': blockchain_data.get('timestamp')
                    }
                else:
                    contract_data['blockchain_verification'] = {
                        'verified': False,
                        'reason': 'Contract not found on blockchain'
                    }
                
                return contract_data
            
            return None
            
        except Exception as e:
            logger.error(f"Error querying contract {contract_number}: {str(e)}")
            return None
    
    def _verify_contract_on_blockchain(self, contract_number: str) -> Optional[Dict]:
        try:
            if not self.contract_address or not self.web3.is_connected():
                return None
            
            contract_abi = [
                {
                    "constant": True,
                    "inputs": [{"name": "_contractNumber", "type": "string"}],
                    "name": "getContract",
                    "outputs": [
                        {"name": "exists", "type": "bool"},
                        {"name": "blockNumber", "type": "uint256"},
                        {"name": "timestamp", "type": "uint256"},
                        {"name": "transactionHash", "type": "bytes32"}
                    ],
                    "type": "function"
                }
            ]
            
            contract = self.web3.eth.contract(
                address=self.contract_address,
                abi=contract_abi
            )
            
            result = contract.functions.getContract(contract_number).call()
            
            if result[0]:  # exists
                return {
                    'block_number': result[1],
                    'timestamp': result[2],
                    'transaction_hash': result[3].hex()
                }
            
            return None
            
        except Exception as e:
            logger.error(f"Error verifying contract on blockchain: {str(e)}")
            return None
    
    def register_contract_on_blockchain(self, contract_number: str, contract_data: Dict) -> Optional[str]:
        try:
            if not self.contract_address or not self.private_key or not self.web3.is_connected():
                return None
            
            contract_abi = [
                {
                    "constant": False,
                    "inputs": [
                        {"name": "_contractNumber", "type": "string"},
                        {"name": "_contractHash", "type": "bytes32"}
                    ],
                    "name": "registerContract",
                    "outputs": [{"name": "", "type": "bytes32"}],
                    "type": "function"
                }
            ]
            
            contract = self.web3.eth.contract(
                address=self.contract_address,
                abi=contract_abi
            )
            
            account = Account.from_key(self.private_key)
            address = account.address
            
            contract_json = json.dumps(contract_data, sort_keys=True)
            contract_hash = Web3.keccak(contract_json.encode('utf-8'))
            
            nonce = self.web3.eth.get_transaction_count(address)
            
            transaction = contract.functions.registerContract(
                contract_number,
                contract_hash
            ).build_transaction({
                'from': address,
                'nonce': nonce,
                'gas': 200000,
                'gasPrice': self.web3.eth.gas_price
            })
            
            signed_txn = self.web3.eth.account.sign_transaction(transaction, self.private_key)
            tx_hash = self.web3.eth.send_raw_transaction(signed_txn.rawTransaction)
            
            receipt = self.web3.eth.wait_for_transaction_receipt(tx_hash, timeout=120)
            
            if receipt.status == 1:
                logger.info(f"Contract {contract_number} registered on blockchain: {tx_hash.hex()}")
                return tx_hash.hex()
            else:
                logger.error(f"Failed to register contract on blockchain")
                return None
                
        except Exception as e:
            logger.error(f"Error registering contract on blockchain: {str(e)}")
            return None
    
    def get_contract_history(self, contract_number: str) -> Optional[list]:
        try:
            endpoint = f"/contracts/{contract_number}/history"
            history_data = self._make_api_request('GET', endpoint)
            
            if history_data:
                return history_data.get('history', [])
            
            return None
            
        except Exception as e:
            logger.error(f"Error getting contract history: {str(e)}")
            return None
    
    def validate_contract_status(self, contract_number: str) -> Optional[str]:
        try:
            endpoint = f"/contracts/{contract_number}/status"
            status_data = self._make_api_request('GET', endpoint)
            
            if status_data:
                return status_data.get('status')
            
            return None
            
        except Exception as e:
            logger.error(f"Error validating contract status: {str(e)}")
            return None
    
    def get_contract_balance(self, contract_number: str) -> Optional[Dict]:
        try:
            endpoint = f"/contracts/{contract_number}/balance"
            balance_data = self._make_api_request('GET', endpoint)
            
            if balance_data:
                return {
                    'principal_balance': balance_data.get('principal_balance', 0),
                    'interest_balance': balance_data.get('interest_balance', 0),
                    'total_balance': balance_data.get('total_balance', 0),
                    'currency': balance_data.get('currency', 'BRL')
                }
            
            return None
            
        except Exception as e:
            logger.error(f"Error getting contract balance: {str(e)}")
            return None