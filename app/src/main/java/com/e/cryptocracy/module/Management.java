package com.e.cryptocracy.module;

import com.highsoft.highcharts.common.hichartsclasses.HIOptions;

import javax.inject.Inject;

public class Management {

    HIOptions hiOptions;

    @Inject
    public Management(HIOptions hiOptions) {
        this.hiOptions = hiOptions;
    }

    public HIOptions getHiOptions() {
        return hiOptions;
    }
}
