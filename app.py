from flask import Flask, render_template, request, jsonify, session, redirect, url_for, flash
from flask_sqlalchemy import SQLAlchemy
from flask_wtf.csrf import CSRFProtect
from werkzeug.security import generate_password_hash, check_password_hash
from datetime import datetime, timedelta
import os
import logging
from functools import wraps
import requests
import json
from decimal import Decimal
import hashlib
import hmac

from models import db, User, Contract, Transaction, DigitalAsset
from services.sbp_service import SBPService
from services.auth_service import AuthService
from services.asset_service import AssetService
from utils.validators import validate_cpf, validate_cnpj, validate_amount
from utils.security import rate_limit, require_auth
from config import Config

app = Flask(__name__)
app.config.from_object(Config)

db.init_app(app)
csrf = CSRFProtect(app)

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

sbp_service = SBPService()
auth_service = AuthService()
asset_service = AssetService()

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')
        
        if auth_service.authenticate_user(username, password):
            session['user_id'] = auth_service.get_user_id(username)
            session['username'] = username
            flash('Login successful!', 'success')
            return redirect(url_for('dashboard'))
        else:
            flash('Invalid credentials', 'error')
    
    return render_template('login.html')

@app.route('/logout')
def logout():
    session.clear()
    flash('Logged out successfully', 'info')
    return redirect(url_for('index'))

@app.route('/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        username = request.form.get('username')
        email = request.form.get('email')
        password = request.form.get('password')
        cpf = request.form.get('cpf')
        
        if not validate_cpf(cpf):
            flash('Invalid CPF format', 'error')
            return render_template('register.html')
        
        if auth_service.create_user(username, email, password, cpf):
            flash('Registration successful! Please login.', 'success')
            return redirect(url_for('login'))
        else:
            flash('Username or email already exists', 'error')
    
    return render_template('register.html')

@app.route('/dashboard')
@require_auth
def dashboard():
    user_id = session.get('user_id')
    user = User.query.get(user_id)
    contracts = Contract.query.filter_by(user_id=user_id).all()
    return render_template('dashboard.html', user=user, contracts=contracts)

@app.route('/api/contracts/query', methods=['POST'])
@require_auth
@rate_limit(limit=10, period=60)
def query_contract():
    try:
        data = request.get_json()
        contract_number = data.get('contract_number')
        document_type = data.get('document_type')
        document_number = data.get('document_number')
        
        if document_type == 'CPF' and not validate_cpf(document_number):
            return jsonify({'error': 'Invalid CPF format'}), 400
        elif document_type == 'CNPJ' and not validate_cnpj(document_number):
            return jsonify({'error': 'Invalid CNPJ format'}), 400
        
        contract_data = sbp_service.query_contract(contract_number, document_type, document_number)
        
        if contract_data:
            user_id = session.get('user_id')
            contract = Contract(
                contract_number=contract_number,
                document_type=document_type,
                document_number=document_number,
                contract_data=contract_data,
                user_id=user_id,
                status='active'
            )
            db.session.add(contract)
            db.session.commit()
            
            return jsonify({
                'success': True,
                'contract': contract_data,
                'contract_id': contract.id
            })
        else:
            return jsonify({'error': 'Contract not found'}), 404
            
    except Exception as e:
        logger.error(f"Error querying contract: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500

@app.route('/api/assets/digitalize', methods=['POST'])
@require_auth
@rate_limit(limit=5, period=60)
def digitalize_asset():
    try:
        data = request.get_json()
        contract_id = data.get('contract_id')
        asset_type = data.get('asset_type')
        amount = data.get('amount')
        
        if not validate_amount(amount):
            return jsonify({'error': 'Invalid amount format'}), 400
        
        user_id = session.get('user_id')
        contract = Contract.query.filter_by(id=contract_id, user_id=user_id).first()
        
        if not contract:
            return jsonify({'error': 'Contract not found'}), 404
        
        digital_asset = asset_service.create_digital_asset(
            contract_id=contract_id,
            asset_type=asset_type,
            amount=Decimal(amount),
            user_id=user_id
        )
        
        return jsonify({
            'success': True,
            'asset_id': digital_asset.id,
            'token_address': digital_asset.token_address,
            'token_id': digital_asset.token_id
        })
        
    except Exception as e:
        logger.error(f"Error digitalizing asset: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500

@app.route('/api/assets/transfer', methods=['POST'])
@require_auth
@rate_limit(limit=10, period=60)
def transfer_asset():
    try:
        data = request.get_json()
        asset_id = data.get('asset_id')
        recipient_address = data.get('recipient_address')
        
        user_id = session.get('user_id')
        asset = DigitalAsset.query.filter_by(id=asset_id, user_id=user_id).first()
        
        if not asset:
            return jsonify({'error': 'Asset not found'}), 404
        
        transaction = asset_service.transfer_asset(
            asset_id=asset_id,
            recipient_address=recipient_address,
            user_id=user_id
        )
        
        return jsonify({
            'success': True,
            'transaction_id': transaction.id,
            'transaction_hash': transaction.transaction_hash
        })
        
    except Exception as e:
        logger.error(f"Error transferring asset: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500

@app.route('/api/assets/<int:asset_id>')
@require_auth
def get_asset(asset_id):
    try:
        user_id = session.get('user_id')
        asset = DigitalAsset.query.filter_by(id=asset_id, user_id=user_id).first()
        
        if not asset:
            return jsonify({'error': 'Asset not found'}), 404
        
        return jsonify({
            'id': asset.id,
            'asset_type': asset.asset_type,
            'amount': str(asset.amount),
            'token_address': asset.token_address,
            'token_id': asset.token_id,
            'status': asset.status,
            'created_at': asset.created_at.isoformat()
        })
        
    except Exception as e:
        logger.error(f"Error getting asset: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500

@app.route('/api/transactions')
@require_auth
def get_transactions():
    try:
        user_id = session.get('user_id')
        transactions = Transaction.query.filter_by(user_id=user_id).order_by(Transaction.created_at.desc()).all()
        
        return jsonify([{
            'id': tx.id,
            'transaction_type': tx.transaction_type,
            'amount': str(tx.amount),
            'status': tx.status,
            'transaction_hash': tx.transaction_hash,
            'created_at': tx.created_at.isoformat()
        } for tx in transactions])
        
    except Exception as e:
        logger.error(f"Error getting transactions: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500

@app.route('/api/user/profile', methods=['GET', 'PUT'])
@require_auth
def user_profile():
    try:
        user_id = session.get('user_id')
        user = User.query.get(user_id)
        
        if request.method == 'GET':
            return jsonify({
                'username': user.username,
                'email': user.email,
                'document_number': user.document_number,
                'created_at': user.created_at.isoformat()
            })
        
        elif request.method == 'PUT':
            data = request.get_json()
            email = data.get('email')
            
            if email:
                user.email = email
                db.session.commit()
                
            return jsonify({'success': True})
            
    except Exception as e:
        logger.error(f"Error in user profile: {str(e)}")
        return jsonify({'error': 'Internal server error'}), 500

@app.errorhandler(404)
def not_found(error):
    return render_template('404.html'), 404

@app.errorhandler(500)
def internal_error(error):
    db.session.rollback()
    return render_template('500.html'), 500

if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run(debug=True, host='0.0.0.0', port=5000)