package me.hsy.chap5.api.innerSyno;



import me.hsy.chap5.api.Ws;

/**
 * Copyright 2021 bejson.com
 */
import java.util.List;

/**
 * Auto-generated: 2021-11-19 23:59:24
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Syno {

    private String pos;
    private List<Ws> ws;
    private String tran;
    public void setPos(String pos) {
        this.pos = pos;
    }
    public String getPos() {
        return pos;
    }

    public void setWs(List<Ws> ws) {
        this.ws = ws;
    }
    public List<Ws> getWs() {
        return ws;
    }

    public void setTran(String tran) {
        this.tran = tran;
    }
    public String getTran() {
        return tran;
    }

}

