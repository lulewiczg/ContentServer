package com.github.lulewiczg.contentserver.utils.models;

import com.github.lulewiczg.contentserver.utils.json.JSONModel;
import com.github.lulewiczg.contentserver.utils.json.JSONProperty;

/**
 * Model to represent user and his permissions to given directory.
 *
 * @author lulewiczg
 */
public class UserPermissions extends JSONModel<UserPermissions> {

    @JSONProperty(propertyName = "name")
    private String name;

    @JSONProperty(propertyName = "uploadAllowed")
    private boolean upload;

    public String getName() {
        return name;
    }

    public boolean isUpload() {
        return upload;
    }

    public UserPermissions(String name, boolean upload) {
        this.name = name;
        this.upload = upload;
    }

}
