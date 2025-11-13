"""
Data loader module for processing CSV files with client questions
"""

import pandas as pd
from pathlib import Path
from typing import Union


def load_data(csv_path: Union[str, Path]) -> pd.DataFrame:
    """
    Load data from CSV file
    
    Args:
        csv_path: Path to CSV file
        
    Returns:
        DataFrame with questions and optional categories
    """
    csv_path = Path(csv_path)
    
    if not csv_path.exists():
        raise FileNotFoundError(f"CSV file not found: {csv_path}")
    
    # Read CSV file
    df = pd.read_csv(csv_path)
    
    # Validate required columns
    if 'question' not in df.columns:
        raise ValueError("CSV must contain 'question' column")
    
    # Clean data
    df = df.dropna(subset=['question'])
    df['question'] = df['question'].str.strip()
    
    # Filter out empty questions
    df = df[df['question'] != '']
    
    print(f"Loaded {len(df)} questions from {csv_path}")
    
    return df


def validate_data_structure(df: pd.DataFrame) -> bool:
    """
    Validate DataFrame structure
    
    Args:
        df: DataFrame to validate
        
    Returns:
        True if valid, raises ValueError if invalid
    """
    required_columns = ['question']
    optional_columns = ['category', 'subcategory', 'expected_response']
    
    # Check required columns
    for col in required_columns:
        if col not in df.columns:
            raise ValueError(f"Missing required column: {col}")
    
    # Check for unexpected columns
    valid_columns = required_columns + optional_columns
    unexpected_cols = set(df.columns) - set(valid_columns)
    if unexpected_cols:
        print(f"Warning: Unexpected columns found: {unexpected_cols}")
    
    return True