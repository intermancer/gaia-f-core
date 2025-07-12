package com.intermancer.gaiaf.core.evaluate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
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
    
    private static final String DEFAULT_HISTORICAL_DATA_PATH = "/training-data/HistoricalPrices-reversed.csv";
    private String trainingDataPath = DEFAULT_HISTORICAL_DATA_PATH;
    private static final int DEFAULT_LEAD_CONSUMPTION_COUNT = 3;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
    List<DataQuantum> historicalData;

    
    /**
     * Specifies which data column (by index) contains the target values to predict
     */
    private int targetIndex = 1;
    
    /**
     * Defines the number of data points the organism processes before making a prediction
     */
    private int leadConsumptionCount = DEFAULT_LEAD_CONSUMPTION_COUNT;
    
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
    
    private class EvaluationState {
        private final Queue<Double> leadData = new LinkedList<>();
        private boolean leadCountMet = false;
        private int leadCount = 0;

        boolean offerPrediction(double prediction) {
            leadData.offer(prediction);
            if (!leadCountMet) {
                leadCount++;
                if (leadCount >= getLeadConsumptionCount()) {
                    leadCountMet = true;
                }
            }
            return leadCountMet;
        }

        double getPrediction() {
            return leadData.poll();
        }
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

        EvaluationState state = new EvaluationState();

        // Prediction phase: feed data and compare predictions against actual values
        return historicalData.stream()
            .mapToDouble(dataQuantum -> {
                // Feed the organism the current data
                organism.consume(dataQuantum);
                
                // Get the organism's prediction (final DataPoint value)
                double futurePrediction = dataQuantum.getValue(dataQuantum.getDataPoints().size() - 1);
                double currentPrediction = 0.0;

                if (state.offerPrediction(futurePrediction)) {
                    currentPrediction = state.getPrediction();
                }
                
                // Get the actual target value
                double actualValue = dataQuantum.getValue(targetIndex);
                
                // Calculate prediction error
                return Math.abs(currentPrediction - actualValue);
            })
            .sum();
    }

    /**
     * Sets the historical data used for evaluation. Useful for testing.
     */
    public void setHistoricalData(List<DataQuantum> historicalData) {
        this.historicalData = historicalData;
    }
    
    /**
     * Loads historical data from the CSV file using streams.
     * 
     * @return List of DataQuantum objects representing the historical data
     */
    private List<DataQuantum> loadHistoricalData() {
        try {
            Path dataPath = Paths.get(getClass().getResource(getTrainingDataPath()).toURI());
            
            try (Stream<String> lines = Files.lines(dataPath)) {
                return lines
                    .skip(1) // Skip header row
                    .map(this::parseDataRow)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Failed to load historical data from " + DEFAULT_HISTORICAL_DATA_PATH, e);
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

    public void setLeadConsumptionCount(int leadConsumptionCount) {
        if (leadConsumptionCount < 1) {
            throw new IllegalArgumentException("Lead consumption count must be at least 1");
        }
        this.leadConsumptionCount = leadConsumptionCount;
    }

    public String getTrainingDataPath() {
        return trainingDataPath;
    }

    public void setTrainingDataPath(String trainingDataPath) {
        this.trainingDataPath = trainingDataPath;
        this.historicalData = null; // Reset historical data to reload with new path
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }

}