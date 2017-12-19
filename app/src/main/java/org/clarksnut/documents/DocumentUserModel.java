package org.clarksnut.documents;

import org.clarksnut.models.UserModel;

import java.util.Date;
import java.util.Set;

public interface DocumentUserModel {

    String STARRED = "starred";
    String VIEWED = "viewed";
    String CHECKED = "checked";
    String TAGS = "tags";

    boolean isStarred();

    void setStarred(boolean starred);

    boolean isViewed();

    void setViewed(boolean viewed);

    boolean isChecked();

    void setChecked(boolean checked);

    Set<String> getTags();

    void setTags(Set<String> tags);

    Date getCreatedAt();

    Date getUpdatedAt();

    UserModel getUser();

    DocumentModel getDocument();
}
