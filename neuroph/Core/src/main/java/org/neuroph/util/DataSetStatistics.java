package org.neuroph.util;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

/**
 * This class calculates various statistics for a data set.
 *
 * @author Arsenovic Aleksandar <salle18@gmail.com>
 */
public class DataSetStatistics {

    private final DataSet dataSet;    
    
    private final int rowLength;
    private final int rowsCount;

    private final double[] mean;
    private final double[] max;
    private final double[] min;
    private final double[] sum;
    private final double[] variance;
    private final double[] stdDeviation;
    private final double[] frequency;

    public static final String MIN = "min";    
    public static final String MAX = "max";    
    public static final String MEAN = "mean";
    public static final String SUM = "sum";
    public static final String STD_DEV = "std dev";    
    public static final String VAR = "var";    
    public static final String FREQ = "freq";

    public DataSetStatistics(DataSet dataSet) {
        this.dataSet = dataSet;
        this.rowLength = dataSet.getInputSize() + dataSet.getOutputSize();
        this.rowsCount = dataSet.getRows().size();
        this.mean = new double[this.rowLength];
        this.max = new double[this.rowLength];
        this.min = new double[this.rowLength];
        this.sum = new double[this.rowLength];
        this.variance = new double[this.rowLength];
        this.stdDeviation = new double[this.rowLength];
        this.frequency = new double[this.rowLength];
        this.setDefaultValues();
    }

    /**
     * Sets default values for statistics.
     */
    private void setDefaultValues() {
        for (int i = 0; i < this.rowLength; i++) {
            this.max[i] = -Double.MAX_VALUE;
            this.min[i] = Double.MAX_VALUE;
        }
    }

    /**
     * Resets statistics values to default.
     */
    private void resetValues() {
        for (int i = 0; i < this.rowLength; i++) {
            this.sum[i] = 0;
            this.variance[i] = 0;
            this.frequency[i] = -0.0;
        }
    }

    /**
     * Calculates basic statistics by columns of the dataset.
     */
    public void calculateStatistics() {

        this.resetValues();
        DataSetColumnType[] columnTypes = this.dataSet.getColumnTypes();
        for (DataSetRow dataSetRow : this.dataSet.getRows()) {
            double[] row = dataSetRow.toArray(); // ovaj uzima i ulaze i izlaze
            for (int i = 0; i < this.rowLength; i++) {
                this.max[i] = Math.max(this.max[i], row[i]);
                this.min[i] = Math.min(this.min[i], row[i]);
                this.sum[i] += row[i];
               
                if (columnTypes[i] == DataSetColumnType.NOMINAL) {
                    this.frequency[i] += row[i];
                }
            }
        }

        // calculate mean for all columns
        for (int i = 0; i < this.rowLength; i++) {
            this.mean[i] = this.sum[i] / (double) this.rowsCount; // makes no sense for binary columns
//            if (columnTypes[i] == DataSetColumnType.NOMINAL) {
//                this.frequency[i] /= (double) this.rowsCount; // da li ovo ima smisla
//            }
        }

        // calculate variance for all columns
        for (DataSetRow dataSetRow : this.dataSet.getRows()) {
            double[] row = dataSetRow.toArray();
            for (int i = 0; i < this.rowLength; i++) {
                double delta = row[i] - this.mean[i];
                this.variance[i] += delta * delta;
            }
        }

        // and standard deviation
        for (int i = 0; i < this.rowLength; i++) {
            this.variance[i] /= (double) this.rowsCount;
            this.stdDeviation[i] = Math.sqrt(this.variance[i]);
        }
        
        // todo: add median, 1st and 3rd quantiles
    }
    
    /**
     * Get original data set.
     * 
     * @return Original dataset.
     */
    public DataSet getDataSet() {
        return this.dataSet;
    }

    /**
     * Get mean for each data set column.
     * 
     * @return Array of means by columns.
     */
    public double[] getMean() {
        return this.mean;
    }

    /**
     * Get maximum for each data set column.
     * 
     * @return Array of maximums by columns.
     */
    public double[] getMax() {
        return this.max;
    }

    /**
     * Get minimum for each data set column.
     * 
     * @return Array of minimums by columns. 
     */
    public double[] getMin() {
        return this.min;
    }

    /**
     * Get variant for each data set column.
     * 
     * @return Array of variants by columns. 
     */
    public double[] getVar() {
        return this.variance;
    }

    /**
     * Get standard deviation for each data set column.
     * 
     * @return Array of standard deviations by columns.
     */
    public double[] getStdDev() {
        return this.stdDeviation;
    }

    /**
     * Get data set frequency for nominal columns.
     * Returns -0.0 for numeric columns.
     * 
     * @return Array of frequencies by columns.
     */
    public double[] getFrequency() {
        return this.frequency;
    }
}
