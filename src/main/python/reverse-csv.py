import pandas as pd
import sys

def reverse_csv(input_file, output_file):
    """
    Reverse the rows in a CSV file while keeping the header in place.
    
    Args:
        input_file (str): Path to the input CSV file
        output_file (str): Path to the output CSV file
    """
    try:
        # Read the CSV file
        df = pd.read_csv(input_file)
        
        # Reverse all rows except the header
        df_reversed = df.iloc[::-1]
        
        # Save to the output file
        df_reversed.to_csv(output_file, index=False)
        
        print(f"Successfully reversed {input_file} and saved to {output_file}")
        print(f"Processed {len(df)} rows (excluding header)")
        
    except FileNotFoundError:
        print(f"Error: Could not find file '{input_file}'")
        sys.exit(1)
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)

def main():
    # Check if correct number of arguments provided
    if len(sys.argv) != 3:
        print("Usage: python reverse_csv.py <input_file> <output_file>")
        print("Example: python reverse_csv.py data.csv reversed_data.csv")
        sys.exit(1)
    
    input_file = sys.argv[1]
    output_file = sys.argv[2]
    
    reverse_csv(input_file, output_file)

if __name__ == "__main__":
    main()
