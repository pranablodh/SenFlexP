package com.orela.senflexp.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

public class sharedPreference
{
    private static final String credentialSharedPreferenceName = "credential";
    private static final String credentialSharedPreferenceKeyUserID = "userID";
    private static final String credentialSharedPreferenceKeyPassword = "password";

    private static final String tokenStorageSharedPreferenceName = "token";
    private static final String tokenStorageSharedPreferenceKeyAccess = "accessToken";
    private static final String tokenStorageSharedPreferenceKeyRefresh = "refreshToken";

    private static final String onboardingSharedPreferenceName = "onboarding";
    private static final String onboardingSharedPreferenceKeyFlag = "flag";

    private static final String testDataSharedPreferenceName = "testData";
    private static final String testDataSharedPreferenceKeyName = "name";
    private static final String testDataSharedPreferenceKeyDeviceID = "deviceID";
    private static final String testDataSharedPreferenceKeyTestID = "testID";
    private static final String testDataSharedPreferenceKeyAddress = "address";
    private static final String testDataSharedPreferenceKeyDOB = "dob";
    private static final String testDataSharedPreferenceKeySex = "sex";
    private static final String testDataSharedPreferenceKeyMobile = "mobile";
    private static final String testDataSharedPreferenceKeyEmail = "email";
    private static final String testDataSharedPreferenceKeyPhoto = "photo";
    private static final String testDataSharedPreferenceKeySenFlexPFile = "senflex";
    private static final String testDataSharedPreferenceKeyBPLiOxyFile = "ioxy";

    private static SharedPreferences credentialStorage;
    private static SharedPreferences tokenStorage;
    private static SharedPreferences onboarding;
    private static SharedPreferences testDataStorage;

    public static void storeCredentials(String userID, String password, Context mCtx)
    {
        credentialStorage = mCtx.getSharedPreferences(credentialSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = credentialStorage.edit();
        editor.putString(credentialSharedPreferenceKeyUserID, userID);
        editor.putString(credentialSharedPreferenceKeyPassword, password);
        editor.apply();
    }

    public static String[] getCredentials(Context mCtx)
    {
        credentialStorage = mCtx.getSharedPreferences(credentialSharedPreferenceName, Context.MODE_PRIVATE);
        String userID = credentialStorage.getString(credentialSharedPreferenceKeyUserID,"");
        String password = credentialStorage.getString(credentialSharedPreferenceKeyPassword,"");
        return new String[] {userID, password};
    }

    public static void deleteCredentials(Context mCtx)
    {
        credentialStorage = mCtx.getSharedPreferences(credentialSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = credentialStorage.edit();
        editor.clear();
        editor.apply();
    }

    public void storeTokens(String accessToken, String refreshToken, Context mCtx)
    {
        tokenStorage = mCtx.getSharedPreferences(tokenStorageSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = tokenStorage.edit();
        editor.putString(tokenStorageSharedPreferenceKeyAccess, accessToken);
        editor.putString(tokenStorageSharedPreferenceKeyRefresh, refreshToken);
        editor.apply();
    }

    public String[] getTokens(Context mCtx)
    {
        tokenStorage = mCtx.getSharedPreferences(tokenStorageSharedPreferenceName, Context.MODE_PRIVATE);
        String accessToken = tokenStorage.getString(tokenStorageSharedPreferenceKeyAccess,"");
        String refreshToken = tokenStorage.getString(tokenStorageSharedPreferenceKeyRefresh,"");
        return new String[] {accessToken, refreshToken};
    }

    public void deleteTokens(Context mCtx)
    {
        tokenStorage = mCtx.getSharedPreferences(tokenStorageSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = tokenStorage.edit();
        editor.clear();
        editor.apply();
    }

    public static void storeOnboarding(Boolean flag, Context mCtx)
    {
        onboarding = mCtx.getSharedPreferences(onboardingSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = onboarding.edit();
        editor.putBoolean(onboardingSharedPreferenceKeyFlag, flag);
        editor.apply();
    }

    public static Boolean getFlag(Context mCtx)
    {
        onboarding = mCtx.getSharedPreferences(onboardingSharedPreferenceName, Context.MODE_PRIVATE);
        return onboarding.getBoolean(onboardingSharedPreferenceKeyFlag, false);
    }

    public static void deleteOnboarding(Context mCtx)
    {
        onboarding = mCtx.getSharedPreferences(onboardingSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = onboarding.edit();
        editor.clear();
        editor.apply();
    }

    public static void storeTestParameters(String name, String testID, String address, String dob, String sex,
                                           String mobile, String email, String photo, String senflex, String ioxy,
                                           Context mCtx)
    {
        testDataStorage = mCtx.getSharedPreferences(testDataSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = testDataStorage.edit();
        editor.putString(testDataSharedPreferenceKeyName, name);
        editor.putString(testDataSharedPreferenceKeyTestID, testID);
        editor.putString(testDataSharedPreferenceKeyAddress, address);
        editor.putString(testDataSharedPreferenceKeyDOB, dob);
        editor.putString(testDataSharedPreferenceKeySex, sex);
        editor.putString(testDataSharedPreferenceKeyMobile, mobile);
        editor.putString(testDataSharedPreferenceKeyEmail, email);
        editor.putString(testDataSharedPreferenceKeyPhoto, photo);
        editor.putString(testDataSharedPreferenceKeySenFlexPFile, senflex);
        editor.putString(testDataSharedPreferenceKeyBPLiOxyFile, ioxy);
        editor.apply();
    }

    public static void storeDeviceID(String deviceID, Context mCtx)
    {
        testDataStorage = mCtx.getSharedPreferences(testDataSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = testDataStorage.edit();
        editor.putString(testDataSharedPreferenceKeyDeviceID, deviceID);
        editor.apply();
    }

    public static String[] getTestParameters(Context mCtx)
    {
        testDataStorage = mCtx.getSharedPreferences(testDataSharedPreferenceName, Context.MODE_PRIVATE);
        String name = testDataStorage.getString(testDataSharedPreferenceKeyName, "");
        String deviceID = testDataStorage.getString(testDataSharedPreferenceKeyDeviceID, "");
        String testID = testDataStorage.getString(testDataSharedPreferenceKeyTestID, "");
        String address = testDataStorage.getString(testDataSharedPreferenceKeyAddress, "");
        String dob = testDataStorage.getString(testDataSharedPreferenceKeyDOB, "");
        String sex = testDataStorage.getString(testDataSharedPreferenceKeySex, "");
        String mobile = testDataStorage.getString(testDataSharedPreferenceKeyMobile, "");
        String email = testDataStorage.getString(testDataSharedPreferenceKeyEmail, "");
        String photo = testDataStorage.getString(testDataSharedPreferenceKeyPhoto, "");
        String senflex = testDataStorage.getString(testDataSharedPreferenceKeySenFlexPFile, "");
        String ioxy = testDataStorage.getString(testDataSharedPreferenceKeyBPLiOxyFile, "");
        return new String[] {name, deviceID, testID, address, dob, sex, mobile, email, photo, senflex, ioxy};
    }

    public static void deleteTestData(Context mCtx)
    {
        testDataStorage = mCtx.getSharedPreferences(testDataSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = testDataStorage.edit();
        editor.clear();
        editor.apply();
    }
}
