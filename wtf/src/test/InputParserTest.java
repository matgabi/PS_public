package test;

import model.InputParser;
import org.junit.Test;

import java.util.Optional;
import static org.junit.Assert.assertEquals;

public class InputParserTest {

    @Test
    public void parseString_test()
    {
        Optional<String> stringValue = InputParser.tryParseString("abcd");
        assertEquals(stringValue.isPresent(),true);


        stringValue = InputParser.tryParseString("");
        assertEquals(stringValue.isPresent(),false);

        stringValue = InputParser.tryParseString(null);
        assertEquals(stringValue.isPresent(),false);

    }

    @Test
    public void parseDouble_test()
    {
        Optional<Double> doubleValue = InputParser.tryParseDouble("12.32");
        assertEquals(doubleValue.isPresent(),true);

        doubleValue = InputParser.tryParseDouble(null);
        assertEquals(doubleValue.isPresent(),false);

        doubleValue = InputParser.tryParseDouble("");
        assertEquals(doubleValue.isPresent(),false);
    }

    @Test
    public void parseInteger_test()
    {
        Optional<Integer> doubleValue = InputParser.tryParseInteger("12");
        assertEquals(doubleValue.isPresent(),true);

        doubleValue = InputParser.tryParseInteger(null);
        assertEquals(doubleValue.isPresent(),false);

        doubleValue = InputParser.tryParseInteger("");
        assertEquals(doubleValue.isPresent(),false);
    }

}
