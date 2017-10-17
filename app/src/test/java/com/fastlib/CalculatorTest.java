package com.fastlib;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by sgfb on 2017/10/11.
 */
public class CalculatorTest {
    private Calculator mCalculator;

    @Before
    public void setUp() throws Exception {
        mCalculator=new Calculator();
    }

    @Test
    public void sum() throws Exception {
        assertEquals(6d,mCalculator.sum(1,5),0);
    }

    @Test
    public void subtract() throws Exception {
        assertEquals(1,mCalculator.subtract(5,4),0);
    }
}