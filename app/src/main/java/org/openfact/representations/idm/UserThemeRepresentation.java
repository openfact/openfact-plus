package org.openfact.representations.idm;

public class UserThemeRepresentation {

    private Data data;

    public UserThemeRepresentation() {
    }

    public UserThemeRepresentation(Data data) {
        this.data = data;
    }

    public static class Data {
        private String id;
        private String type;
        private Attributes attributes;
        private ThemeLink links;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public ThemeLink getLinks() {
            return links;
        }

        public void setLinks(ThemeLink links) {
            this.links = links;
        }
    }

    public static class Attributes {
        private String id;
        private String type;
        private String name;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class ThemeLink {
        private String previewImg;

        public String getPreviewImg() {
            return previewImg;
        }

        public void setPreviewImg(String previewImg) {
            this.previewImg = previewImg;
        }
    }

}
