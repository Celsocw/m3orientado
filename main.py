#!/usr/bin/env python3
"""
Banco do Brasil AI Agent System
Main entry point for processing client questions and generating responses
"""

import argparse
import sys
from pathlib import Path

from src.data_loader import load_data
from src.classifier import QuestionClassifier
from src.response_generator import ResponseGenerator
from src.evaluator import Evaluator


def main():
    parser = argparse.ArgumentParser(description='Banco do Brasil AI Agent System')
    parser.add_argument('--csv', type=str, required=True, help='Path to CSV file with questions')
    parser.add_argument('--output', type=str, default='responses.csv', help='Output file for responses')
    parser.add_argument('--evaluate', action='store_true', help='Run evaluation metrics')
    parser.add_argument('--model', type=str, default='bert-base-multilingual-cased', help='BERT model name')
    
    args = parser.parse_args()
    
    # Validate input file
    csv_path = Path(args.csv)
    if not csv_path.exists():
        print(f"Error: CSV file '{args.csv}' not found")
        sys.exit(1)
    
    print("Loading data...")
    data = load_data(csv_path)
    
    print("Initializing classifier...")
    classifier = QuestionClassifier(model_name=args.model)
    
    print("Initializing response generator...")
    response_generator = ResponseGenerator()
    
    print("Processing questions...")
    results = []
    
    for idx, row in data.iterrows():
        question = row['question']
        true_category = row.get('category', None)
        true_subcategory = row.get('subcategory', None)
        
        # Classify question
        category, subcategory = classifier.classify(question)
        
        # Generate response
        response = response_generator.generate_response(category, subcategory, question)
        
        result = {
            'question': question,
            'predicted_category': category,
            'predicted_subcategory': subcategory,
            'response': response
        }
        
        if true_category is not None:
            result['true_category'] = true_category
        if true_subcategory is not None:
            result['true_subcategory'] = true_subcategory
            
        results.append(result)
        
        if (idx + 1) % 10 == 0:
            print(f"Processed {idx + 1}/{len(data)} questions")
    
    # Save results
    import pandas as pd
    df_results = pd.DataFrame(results)
    df_results.to_csv(args.output, index=False)
    print(f"Results saved to {args.output}")
    
    # Run evaluation if requested
    if args.evaluate and 'true_category' in df_results.columns:
        print("Running evaluation...")
        evaluator = Evaluator()
        metrics = evaluator.evaluate(df_results)
        print("\nEvaluation Results:")
        for metric, value in metrics.items():
            print(f"{metric}: {value:.4f}")


if __name__ == "__main__":
    main()