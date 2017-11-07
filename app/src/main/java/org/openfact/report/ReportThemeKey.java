package org.openfact.report;

public class ReportThemeKey {

    private String name;

    public static ReportThemeKey get(String name) {
        return new ReportThemeKey(name);
    }

    private ReportThemeKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportThemeKey themeKey = (ReportThemeKey) o;

        if (name != null ? !name.equals(themeKey.name) : themeKey.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        return result;
    }

}
