package org.clarksnut.documents;

public enum UnsavedReasonType {

    /**
     * Documents has a reader, but it could not read it
     */
    UNREADABLE,

    /**
     * Document is a valid UBL but has not reader at the moment
     */
    UNSUPPORTED
}
