package com.lidapp.kaloriq.Custom;

import com.lidapp.kaloriq.Model.Bar;

import java.util.Comparator;

public class BarComparator implements Comparator<Bar> {

    @Override
    public int compare(Bar left, Bar right) {
        return left.getall().compareTo(right.getall());
    }
}
