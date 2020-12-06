package com.example.cotton.ValueObject.BookSearchByBarcode;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "rss")
public class BookSearchResultVO {

    @Attribute(name = "version")
    private String version;


    @Element(name = "channel")
    public ChannelVO channel;



}





