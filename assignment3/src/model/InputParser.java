package model;

import server.ServerCommands;

import java.util.Optional;

public class InputParser {

    public static Optional<Double> tryParseDouble(String value)
    {
        try{
            Double doubleValue = Double.parseDouble(value);
            return Optional.of(doubleValue);
        }catch (NumberFormatException e)
        {
            return Optional.empty();
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public static Optional<String> tryParseString(String value)
    {
        if(value == null || value.equals(""))
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(value);
        }
    }

    public static Optional<Integer> tryParseInteger(String value)
    {
        try {
            Integer integerValue = Integer.parseInt(value);
            return Optional.of(integerValue);
        }
        catch (NumberFormatException e)
        {
            return Optional.empty();
        }
        catch (Exception e)
        {
            return Optional.empty();
        }
    }

    public static Optional<ServerCommands.COMMAND> tryParseCommand(String value)
    {

        Optional<Integer> integer = tryParseInteger(value);
        if(!integer.isPresent())
        {
            return Optional.of(ServerCommands.COMMAND.INVALID_COMMAND);
        }
        else
        {
            switch (integer.get())
            {
                case 1 : return Optional.of(ServerCommands.COMMAND.CLOSE);
                case 2 : return Optional.of(ServerCommands.COMMAND.GET_ALL_CLIENTS);
                default: return Optional.of(ServerCommands.COMMAND.INVALID_COMMAND);
            }
        }
    }
}
