package org.openfact.reports;

public class ThemeKey {

    private String name;

    public static ThemeKey get(String name) {
        return new ThemeKey(name);
    }

    private ThemeKey(String name) {
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

        ThemeKey themeKey = (ThemeKey) o;

        if (name != null ? !name.equals(themeKey.name) : themeKey.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        return result;
    }

}
