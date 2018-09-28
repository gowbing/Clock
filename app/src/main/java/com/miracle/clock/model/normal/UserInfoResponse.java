package com.miracle.clock.model.normal;

/**
 * Created by hss on 2017/6/24.
 */

public class UserInfoResponse extends EmptyResponse {
    Data results;

    public void setResults(Data results) {
        this.results = results;
    }

    public Data getResults() {
        return results;
    }

    public static class Data {
        String qquid;
        String wechatuid;
        long createtime;
        String imgUrl;
        long birthday;
        String phone;
        int userguid;
        String nickname;
        String province;
        int userid;
        int logintype;
        int sex;
        String password;
        String city;
        String country;
        String favorite;
        public String getQquid() {
            return qquid;
        }

        public String getWechatuid() {
            return wechatuid;
        }

        public long getCreatetime() {
            return createtime;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public long getBirthday() {
            return birthday;
        }

        public String getPhone() {
            return phone;
        }

        public int getUserguid() {
            return userguid;
        }

        public String getNickname() {
            return nickname;
        }

        public String getProvince() {
            return province;
        }

        public int getUserid() {
            return userid;
        }

        public int getLogintype() {
            return logintype;
        }

        public int getSex() {
            return sex;
        }
        public String getPassword() {
            return password;
        }

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public String getFavorite() {
            return favorite;
        }
    }
}
