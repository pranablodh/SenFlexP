package com.orela.senflexp.inputValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class inputValidator
{
    public static Boolean checkString(String data)
    {
        final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_ ]*$");
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }

    public static Boolean checkNoWhiteSpace(String data)
    {
        final Pattern pattern = Pattern.compile("^[a-zA-Z]*$");
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }

    public static Boolean checkMobileNumber(String data)
    {
        final Pattern pattern = Pattern.compile("[0-9]{10}$");
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }

    public static Boolean checkEmail(String data)
    {
        final Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }

    public static Boolean checkDate(String data)
    {
        final Pattern pattern = Pattern.compile("^[0-9]{1,2}([/\\-])[0-9]{1,2}([/\\-])[0-9]{4}$");
        Matcher matcher = pattern.matcher(data);
        return matcher.matches();
    }
}