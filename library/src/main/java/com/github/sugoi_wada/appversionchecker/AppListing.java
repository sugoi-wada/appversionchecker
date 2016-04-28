package com.github.sugoi_wada.appversionchecker;

public class AppListing {
    private int versionCode;
    private PlayStore.ReleaseType releaseType;
    private String language = "";
    private String recentChanges = "";
    private String defaultLanguage = "";
    private String defaultRecentChanges = "";

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public PlayStore.ReleaseType getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(PlayStore.ReleaseType releaseType) {
        this.releaseType = releaseType;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRecentChanges() {
        return recentChanges;
    }

    public void setRecentChanges(String recentChanges) {
        this.recentChanges = recentChanges;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getDefaultRecentChanges() {
        return defaultRecentChanges;
    }

    public void setDefaultRecentChanges(String defaultRecentChanges) {
        this.defaultRecentChanges = defaultRecentChanges;
    }

    @Override
    public String toString() {
        return "versionCode: " + versionCode + " \n" +
                "releaseType: " + releaseType.name() + " \n" +
                "language: " + language + " \n" +
                "recentChanges: " + recentChanges + " \n" +
                "defaultLanguage: " + defaultLanguage + " \n" +
                "defaultRecentChanges: " + defaultRecentChanges;
    }
}
