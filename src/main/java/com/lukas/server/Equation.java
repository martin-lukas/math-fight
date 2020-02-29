package com.lukas.server;

import java.util.Random;

/**
 * Equation is generated here.
 *
 * @author Martin Lukáš
 */
public class Equation {
    private static final Random RAND = new Random();
    private static final char[] SIGNS = {'+', '-'};
    private static final int DEFAULT_NO_OF_ELEMENTS = 3;
    private static final int DEFAULT_MIN = 1;
    private static final int DEFAULT_MAX = 9;

    private int noOfElements;
    private int minValue;
    private int maxValue;
    private int[] elements;
    private char[] operations;
    private int result;

    public Equation() {
        this(DEFAULT_NO_OF_ELEMENTS, DEFAULT_MIN, DEFAULT_MAX);
    }
    
    public Equation(int noOfElements, int minValue, int maxValue) {
        this.noOfElements = noOfElements;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.elements = new int[noOfElements];
        this.operations = new char[noOfElements - 1];
        
        generateEquation();
    }
    
    private void generateEquation() {
        elements = generateRandomArray(noOfElements, minValue, maxValue);
        operations = generateOperations();

        result = elements[0];
        for (int i = 0; i < operations.length; i++) {
            if (operations[i] == '+') result += elements[i + 1];
            else if (operations[i] == '-') result -= elements[i + 1];
        }
    }

    private char[] generateOperations() {
        int[] signIndexes = generateRandomArray(noOfElements - 1, 0, 1);
        char[] generatedSigns = new char[noOfElements - 1];
        for (int i = 0; i < signIndexes.length; i++) {
            generatedSigns[i] = SIGNS[signIndexes[i]];
        }
        return generatedSigns;
    }

    private int[] generateRandomArray(int n, int min, int max) {
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = randInt(min, max);
        }
        return arr;
    }

    public int getResult() {
        return result;
    }

    private int randInt(int min, int max) {
        return RAND.nextInt((max - min) + 1) + min;
    }

    @Override
    public String toString() {
        StringBuilder equationString = new StringBuilder();
        for (int i = 0; i < noOfElements - 1; i++) {
            equationString.append(elements[i]);
            equationString.append(operations[i]);
        }
        // append the last element (because there's less operations than elements)
        equationString.append(elements[noOfElements - 1]);
        return equationString.toString();
    }
}
