package com.miracle.clock.custom;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.addapp.pickers.picker.LinkagePicker;

/**
 * Created by Arthas on 2017/8/17.
 */

public class CustomTimePicker extends LinkagePicker {

    public CustomTimePicker(Activity activity) {
        super(activity, new CustomTimeDataProvider());
    }

    @Override
    protected int[] getColumnWidths(boolean onlyTwoColumn) {
        return new int[]{WRAP_CONTENT, WRAP_CONTENT, 0};
    }

    public static class CustomTimeDataProvider implements DataProvider {
        private List<String> provinces = new ArrayList<>();

        public CustomTimeDataProvider() {
            provinces = Arrays.asList(
                    "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
                    "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        }

        @Override
        public boolean isOnlyTwo() {
            return true;
        }

        @Override
        public List<String> provideFirstData() {
            return provinces;
        }

        @Override
        public List<String> provideSecondData(int firstIndex) {
            return parseData(provinces.get(firstIndex));
        }

        @Override
        public List<String> provideThirdData(int firstIndex, int secondIndex) {
            return new ArrayList<>();
        }

        @NonNull
        private List<String> parseData(String province) {
            List<String> min = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                if (i < 10) {
                    min.add("0" + i);
                } else {
                    min.add("" + i);
                }
            }
            return min;
        }

    }
}
