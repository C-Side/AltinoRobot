package com.example.ibnas.altinorobot;

import android.widget.ToggleButton;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void small_test() throws Exception {
        MainActivity ma = new MainActivity();
        ma.getBt_loop().setChecked(true);
    }
}