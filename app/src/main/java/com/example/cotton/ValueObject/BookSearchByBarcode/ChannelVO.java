package com.example.cotton.ValueObject.BookSearchByBarcode;

import android.content.ClipData;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

public class ChannelVO {

    @Element(name = "title")
    public String title;

    @Element(name = "link")
    public String link;

    @Element(name = "description")
    public String description;

    @Element(name = "lastBuildDate")
    public String lastBuildDate;

    @Element(name = "total")
    public String total;

    @Element(name = "start")
    public String start;

    @Element(name = "display")
    public String display;

    @Element(name = "item")
    public ItemVO item;
}
