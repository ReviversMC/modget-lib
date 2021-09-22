package com.github.nebelnidas.modgetlib;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.github.nebelnidas.modgetlib.ModgetLib;

class ModgetLibTest {
    @Test void someLibraryMethodReturnsTrue() {
        ModgetLib classUnderTest = new ModgetLib();
        assertTrue(classUnderTest.someLibraryMethod(), "someLibraryMethod should return 'true'");
    }
}
