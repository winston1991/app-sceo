package com.huntkey.software.sceo.entity;

import java.util.List;

public class Domain {
    /**
     * 可切换域列表
     */
    public static class Lists {
        private int Total;
        private List<Item> Rows;

        public int getTotal() {
            return Total;
        }

        public void setTotal(int total) {
            Total = total;
        }

        public List<Item> getRows() {
            return Rows;
        }

        public void setRows(List<Item> rows) {
            Rows = rows;
        }

        public static class Item {
            private int acctid;
            private String domm_name;
            private String guid;
            private int cur_acctid;

            public int getAcctid() {
                return acctid;
            }

            public void setAcctid(int acctid) {
                this.acctid = acctid;
            }

            public String getDomm_name() {
                return domm_name;
            }

            public void setDomm_name(String domm_name) {
                this.domm_name = domm_name;
            }

            public String getGuid() {
                return guid;
            }

            public void setGuid(String guid) {
                this.guid = guid;
            }

            public int getCur_acctid() {
                return cur_acctid;
            }

            public void setCur_acctid(int cur_acctid) {
                this.cur_acctid = cur_acctid;
            }
        }
    }
}
