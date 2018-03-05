package org.clarksnut.representations.idm;

import java.util.List;

public class NotificationsRepresentation {

    private NotificationsData data;

    public NotificationsRepresentation() {
    }

    public NotificationsRepresentation(NotificationsData data) {
        this.data = data;
    }

    public NotificationsData getData() {
        return data;
    }

    public void setData(NotificationsData data) {
        this.data = data;
    }

    public static class NotificationsData {
        private String type;
        private NotificationsAttributes attributes;

        public NotificationsRepresentation toNotificationsRepresentation() {
            return new NotificationsRepresentation(this);
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public NotificationsAttributes getAttributes() {
            return attributes;
        }

        public void setAttributes(NotificationsAttributes attributes) {
            this.attributes = attributes;
        }

    }

    public static class NotificationsAttributes {
        private List<RequestRepresentation.RequestData> requestAccesses;

        public List<RequestRepresentation.RequestData> getRequestAccesses() {
            return requestAccesses;
        }

        public void setRequestAccesses(List<RequestRepresentation.RequestData> requestAccesses) {
            this.requestAccesses = requestAccesses;
        }
    }

}
