package com.ethfoo.dbphoto.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/13.
 */
public class ItemProvider {


    private static List<Item> itemList;
    private static List<Item> albumItemList;
    private static List<Item> refreshItemList;
    private static List<Item> loadMoreList;

    private ItemProvider(){}

    public static List<Item> getEveryDayList(){

        if(itemList == null){
            itemList = new LinkedList<>();

            itemList.add(new Item("2245047673"));
            itemList.add(new Item("2253110682"));
            itemList.add(new Item("2238388155"));
            itemList.add(new Item("2158023868"));
            itemList.add(new Item("2173202405"));

            itemList.add(new Item("2244970597"));
            //itemList.add(new Item("2244102633"));
            itemList.add(new Item("1915026191"));
            itemList.add(new Item("1153023374"));
            itemList.add(new Item("2238603687"));

//            itemList.add(new Item("2245279030"));
//            itemList.add(new Item("2160000509"));
//            itemList.add(new Item("2238604815"));
//            itemList.add(new Item("931768579"));
//            itemList.add(new Item("931772560"));
//
//            itemList.add(new Item("2244609603"));
//            itemList.add(new Item("1460432256"));
//            itemList.add(new Item("2211196944"));

        }

        return itemList;
    }

    public static List<Item> getRefreshList(){
        if( refreshItemList == null){
            refreshItemList = new LinkedList<>();
            itemList.add(new Item("2212516830"));
            itemList.add(new Item("2174162889"));
            itemList.add(new Item("1690170190"));
            itemList.add(new Item("2204529023"));
            itemList.add(new Item("2248473508"));
            itemList.add(new Item("1429080512"));
            itemList.add(new Item("2247363234"));
            itemList.add(new Item("268180949"));
            itemList.add(new Item("2248137334"));
        }

        return refreshItemList;
    }

    public static List<Item> getLoadMoreList(){
        if(loadMoreList == null){
            itemList.add(new Item("2243225904"));
            itemList.add(new Item("2248473544"));
            itemList.add(new Item("1429080956"));
            itemList.add(new Item("276788582"));
            itemList.add(new Item("2246073284"));
            itemList.add(new Item("2247174349"));
            itemList.add(new Item("2247923035"));
        }

        return loadMoreList;
    }




}
