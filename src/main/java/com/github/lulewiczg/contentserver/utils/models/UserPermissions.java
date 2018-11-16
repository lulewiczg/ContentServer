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

    @JSONProperty(propertyName = "deleteAllowed")
    private boolean delete;

    public String getName() {
        return name;
    }

    public boolean isUpload() {
        return upload;
    }

    public boolean isDelete() {
        return delete;
    }

    public UserPermissions(String name, boolean upload, boolean delete) {
        this.name = name;
        this.upload = upload;
        this.delete = delete;
    }

}
