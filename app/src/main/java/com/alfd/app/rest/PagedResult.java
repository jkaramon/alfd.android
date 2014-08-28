package com.alfd.app.rest;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by karamon on 21. 7. 2014.
 */
public class PagedResult<T> {
    public List<T> items;

    public DateTime serverDate;
}
