package com.intermancer.gaiaf.core.evaluate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.intermancer.gaiaf.core.organism.DataQuantum;
import com.intermancer.gaiaf.core.organism.Organism;

/**
 * A concrete implementation of the Evaluator interface that provides fitness scoring
 * for organisms using a prediction-based evaluation methodology. It operates by feeding
 * historical time-series data to an organism and measuring how accurately the organism
 * can predict future values.
 */
@Component
public class BasicEvaluator implements Evaluator {
    
    private static final String HISTORICAL_DATA_PATH = "/training-data/HistoricalPrices.csv";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
    List<DataQuantum> historicalData;

    
    /**
     * Specifies which data column (by index) contains the target values to predict
     */
    private final int targetIndex;
    
    /**
     * Defines the number of data points the organism processes before making a prediction
     */
    private final int leadConsumptionCount;
    
    /**
     * Default constructor using sensible defaults.
     * Sets targetIndex to 1 (typically the "Open" column in stock data)
     * and leadConsumptionCount to 3.
     */
    public BasicEvaluator() {
        this(1, 3);
    }
    
    /**
     * Constructor with custom configuration.
     * 
     * @param targetIndex The index of the column containing target values to predict
     * @param leadConsumptionCount The number of data points to process before predictions begin
     */
    public BasicEvaluator(int targetIndex, int leadConsumptionCount) {
        this.targetIndex = targetIndex;
        this.leadConsumptionCount = leadConsumptionCount;
    }
    
    /**
     * Evaluates an organism by feeding it historical data and measuring prediction accuracy.
     * 
     * @param organism The organism to evaluate
     * @return The cumulative prediction error score (lower is better, 0 is perfect)
     */
    @Override
    public double evaluate(Organism organism) {
        if (historicalData == null) {
            // Load historical data only once
            historicalData = loadHistoricalData();
        }
        
        // Prediction phase: feed data and compare predictions against actual values
        return historicalData.stream()
            .skip(leadConsumptionCount)
            .mapToDouble(dataQuantum -> {
                // Feed the organism the current data
                organism.consume(dataQuantum);
                
                // Get the organism's prediction (final DataPoint value)
                double prediction = dataQuantum.getValue(dataQuantum.getDataPoints().size() - 1);
                
                // Get the actual target value
                double actualValue = dataQuantum.getValue(targetIndex);
                
                // Calculate prediction error
                return Math.abs(prediction - actualValue);
            })
            .sum();
    }
    
    /**
     * Loads historical data from the CSV file using streams.
     * 
     * @return List of DataQuantum objects representing the historical data
     */
    private List<DataQuantum> loadHistoricalData() {
        try {
            Path dataPath = Paths.get(getClass().getResource(HISTORICAL_DATA_PATH).toURI());
            
            try (Stream<String> lines = Files.lines(dataPath)) {
                return lines
                    .skip(1) // Skip header row
                    .map(this::parseDataRow)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to load historical data from " + HISTORICAL_DATA_PATH, e);
        }
    }
    
    /**
     * Parses a CSV row into a DataQuantum.
     * 
     * @param line The CSV line to parse
     * @return DataQuantum containing the parsed values, or null if parsing fails
     */
    private DataQuantum parseDataRow(String line) {
        String[] values = line.split(",");
        if (values.length < 2) {
            return null;
        }
        
        DataQuantum dataQuantum = new DataQuantum();
        
        // Parse each column value
        for (int i = 0; i < values.length; i++) {
            String value = values[i].trim();
            
            // First column is the date - convert to epoch time
            if (i == 0) {
                try {
                    long epochMillis = parseDateToEpoch(value);
                    dataQuantum.addValue((double) epochMillis);
                } catch (Exception e) {
                    // If date parsing fails, skip this row
                    return null;
                }
            } else {            
                // Parse numerical values
                try {
                    double numericValue = Double.parseDouble(value);
                    dataQuantum.addValue(numericValue);
                } catch (NumberFormatException e) {
                    // Skip non-numeric values
                }
            }
        }
        
        return dataQuantum.getDataPoints().isEmpty() ? null : dataQuantum;
    }
    
    /**
     * Parses a date string to epoch milliseconds.
     * Assumes dates are in MM/dd/yy format with years starting with "20".
     * All dates are assumed to be at midnight UTC.
     * 
     * @param dateStr The date string to parse (e.g., "01/12/17")
     * @return The epoch time in milliseconds
     */
    private long parseDateToEpoch(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, formatter);
        
        // For two-digit years, ensure they're in the 2000s
        if (date.getYear() < 100) {
            date = date.withYear(2000 + date.getYear());
        }
        
        // Convert to epoch milliseconds at midnight UTC
        return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }
    
    // Getters for configuration
    
    public int getTargetIndex() {
        return targetIndex;
    }
    
    public int getLeadConsumptionCount() {
        return leadConsumptionCount;
    }
}