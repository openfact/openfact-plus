package org.clarksnut.report;

import java.util.Objects;

public class ReportThemeKey {

    private String name;
    private String type;

    public static ReportThemeKey get(String name, String type) {
        return new ReportThemeKey(name, type);
    }

    private ReportThemeKey(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReportThemeKey themeKey = (ReportThemeKey) o;

        if (name != null ? !name.equals(themeKey.name) : themeKey.name != null) return false;
        if (!Objects.equals(type, themeKey.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

}
