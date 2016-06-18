package com.github.johanrg.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the main args for easy implementation of command line switches.
 *
 * @author Johan Gustafsson
 * @since 6/2/2016.
 */
public class CommandLineParser {
    private final String[] args;

    public CommandLineParser(String[] args, int mustHaveNParams, String usageMessage) throws CommandLineParserException {
        if (args.length < mustHaveNParams) {
            throw new CommandLineParserException(usageMessage);
        }
        this.args = args;
    }

    /**
     * Find a command line switch and expect it to hold an integer.
     *
     * @param commandSwitch name of the switch
     * @param defaultValue default value or null if one must be specified, this will force exception to happen if
     *                     it can't find the commandSwitch.
     * @return int holding the value for the command switch.
     * @throws CommandLineParserException
     */
    public int getInteger(String commandSwitch, Integer defaultValue) throws CommandLineParserException {
        int i = findSwitch(commandSwitch);

        if (i != -1) {
            if (i + 1 < args.length) {
                ++i;
                try {
                    return Integer.parseInt(args[i]);
                } catch (NumberFormatException e) {
                    throw new CommandLineParserException(String.format("%s must have an integer value.", commandSwitch));
                }
            } else {
                throw new CommandLineParserException(String.format("%s did not have a value", commandSwitch));
            }
        }
        if (defaultValue != null) {
            return defaultValue;
        } else {
            throw new CommandLineParserException(String.format("Expected command line switch %s", commandSwitch));
        }
    }


    /**
     * @param commandSwitch name of the switch
     * @param defaultValue default value or null if one must be specified, this will force exception to happen if
     *                     it can't find the commandSwitch.
     * @param restOfTheArgs if true it will append all the rest of the command line into a string.
     * @return String holding the value for the command switch.
     * @throws CommandLineParserException
     */
    public String getString(String commandSwitch, String defaultValue, boolean restOfTheArgs) throws CommandLineParserException {
        int i = findSwitch(commandSwitch);

        if (i < 2) {
            if (i + 1 < args.length) {
                ++i;
                List<String> list = new ArrayList<>();
                while (i < args.length) {
                    list.add(args[i]);
                    ++i;
                    if (!restOfTheArgs) {
                        break;
                    }
                }

                if (list.size() > 0) {
                   return String.join(" ", list);
                } else {
                    throw new CommandLineParserException(String.format("%s did not have a value", commandSwitch));
                }

            }
        }

        if (defaultValue != null) {
            return defaultValue;
        } else {
            throw new CommandLineParserException(String.format("Expected command line switch %s", commandSwitch));
        }
    }

    /**
     * @param commandSwitch name of the switch
     * @param maxNumberOfElements max number of elements it will return.
     * @return String array holding the value of the command switch.
     * @throws CommandLineParserException
     */
    public String[] getStringArray(String commandSwitch, int maxNumberOfElements) throws CommandLineParserException {
        int i = findSwitch(commandSwitch);
        if ((args.length - 1) - i > 0) {
            int pos = i + 1;
            int len = args.length - pos;
            if (maxNumberOfElements > 0 && maxNumberOfElements <= len) {
                len = maxNumberOfElements;
            }
            String[] result = new String[len];
            System.arraycopy(args, pos, result, 0, len);
            return result;
        } else {
            throw new CommandLineParserException(String.format("%s did not have a value", commandSwitch));
        }
    }

    /**
     * Find a single parameter that holds no value
     *
     * @param commandSwitch name of the switch
     * @return boolean value true if available, else false.
     */
    public boolean getParam(String commandSwitch) {
        return findSwitch(commandSwitch) != -1;
    }

    /**
     * Helper method, returns the index that holds the commandSwitch.
     *
     * @param commandSwitch name of the switch
     * @return the index position of the switch or -1 if not available.
     */
    private int findSwitch(String commandSwitch) {
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals(commandSwitch)) {
                return i;
            }
        }

        return -1;
    }

}
