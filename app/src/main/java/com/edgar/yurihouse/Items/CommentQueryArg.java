package com.edgar.yurihouse.Items;

public class CommentQueryArg {

    private String obj_id, authoruid, is_Original, comment_type, dt;

    public CommentQueryArg(String obj_id, String authoruid, String is_Original, String comment_type, String dt) {
        this.obj_id = obj_id;
        this.authoruid = authoruid;
        this.is_Original = is_Original;
        this.comment_type = comment_type;
        this.dt = dt;
    }

    public String getObj_id() {
        return obj_id;
    }

    public String getAuthoruid() {
        return authoruid;
    }

    public String getIs_Original() {
        return is_Original;
    }

    public String getComment_type() {
        return comment_type;
    }

    public String getDt() {
        return dt;
    }
}
