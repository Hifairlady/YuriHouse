package com.edgar.yurihouse.Items;

import java.util.ArrayList;

public class ViewPointItem {

//    {
//        "result": 1000,
//            "msg": "提交成功",
//            "data": {
//        "list": [
//        {
//            "id": 42274012,
//                "uid": 41550,
//                "nickname": "Ameari",
//                "num": 159,
//                "comic_id": 39883,
//                "chaper_id": 68989,
//                "title": "終於接吻了 不是吸血",
//                "createtime": 1521207534,
//                "isused": 1,
//                "status": 0,
//                "page": 7,
//                "ip": "140.206.109.142",
//                "type": 0,
//                "avatar_url": "https://avatar.dmzj.com/43/71/43716eb8b1f4181848a98853ee8b2c38.png"
//        },
//        {
//            "id": 42591414,
//                "uid": 101324150,
//                "nickname": "破晓の晨曦彡",
//                "num": 1,
//                "comic_id": 39883,
//                "chaper_id": 68989,
//                "title": "胃药啊",
//                "createtime": 1524701893,
//                "isused": 1,
//                "status": 0,
//                "page": 9,
//                "ip": "140.206.109.142",
//                "type": 0,
//                "avatar_url": "https://avatar.dmzj.com/db/b5/dbb5cd472dc54e1794fd7cbfd6d12ce3.png"
//        }
//        ],
//        "total": "975"
//    },
//        "page_data": ""
//    }

    private int result;
    private String msg, page_data;
    private VpDataItem data;

    public class VpDataItem {

        public String total;
        public ArrayList<VpListItem> list;

        public class VpListItem {
//            "id": 42274012,
//            "uid": 41550,
//            "nickname": "Ameari",
//            "num": 159,
//            "comic_id": 39883,
//            "chaper_id": 68989,
//            "title": "終於接吻了 不是吸血",
//            "createtime": 1521207534,
//            "isused": 1,
//            "status": 0,
//            "page": 7,
//            "ip": "140.206.109.142",
//            "type": 0,
//            "avatar_url": "https://avatar.dmzj.com/43/71/43716eb8b1f4181848a98853ee8b2c38.png"

            public int id, uid, num, comic_id, chaper_id, isused, status, page, type;
            public String nickname, title, ip, avatar_url;
            public long createtime;

            public VpListItem(int id, int uid, int num, int comic_id, int chaper_id, int isused, int status, int page, int type, String nickname, String title, String ip, String avatar_url, long createtime) {
                this.id = id;
                this.uid = uid;
                this.num = num;
                this.comic_id = comic_id;
                this.chaper_id = chaper_id;
                this.isused = isused;
                this.status = status;
                this.page = page;
                this.type = type;
                this.nickname = nickname;
                this.title = title;
                this.ip = ip;
                this.avatar_url = avatar_url;
                this.createtime = createtime;
            }

            public int getId() {
                return id;
            }

            public int getUid() {
                return uid;
            }

            public int getNum() {
                return num;
            }

            public int getComic_id() {
                return comic_id;
            }

            public int getChaper_id() {
                return chaper_id;
            }

            public int getIsused() {
                return isused;
            }

            public int getStatus() {
                return status;
            }

            public int getPage() {
                return page;
            }

            public int getType() {
                return type;
            }

            public String getNickname() {
                return nickname;
            }

            public String getTitle() {
                return title;
            }

            public String getIp() {
                return ip;
            }

            public String getAvatar_url() {
                return avatar_url;
            }

            public long getCreatetime() {
                return createtime;
            }
        }

        public VpDataItem(String total, ArrayList<VpListItem> list) {
            this.total = total;
            this.list = list;
        }

        public String getTotal() {
            return total;
        }

        public ArrayList<VpListItem> getList() {
            return list;
        }
    }

    public ViewPointItem(int result, String msg, String page_data, VpDataItem data) {
        this.result = result;
        this.msg = msg;
        this.page_data = page_data;
        this.data = data;
    }

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public String getPage_data() {
        return page_data;
    }

    public VpDataItem getData() {
        return data;
    }
}
