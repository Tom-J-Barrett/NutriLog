package com.example.tom13.fooddemo.presenters;

import android.app.Activity;

import com.example.tom13.fooddemo.foodLog.FoodLog;
import com.example.tom13.fooddemo.foodLog.FoodLogImpl;
import com.example.tom13.fooddemo.storage.DAO;
import com.example.tom13.fooddemo.storage.DAOFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by tom13 on 06/03/2018.
 * Presenter class to control the FoodLogsActivity View.
 */

public class FoodLogsPresenter {

    private DAO dao;
    private Activity activity;
    private Map<String, Runnable> listViewOptions;
    private Map<String, Integer> dateChanges;
    private  List<FoodLog> foodLogs;
    private Date dateToQuery;
    private String listType;
    private Double calories = 0.0;

    public FoodLogsPresenter(Activity activity) {
        this.activity = activity;
        dao = new DAOFactory().getDAO(activity);
        dateToQuery = new Date();
        populateMaps();
    }

    public List<String> getSpinnerContents(){
        List<String> spinnerContents = new ArrayList<>();
        spinnerContents.add("Day");
        spinnerContents.add("Week");
        spinnerContents.add("Month");
        return spinnerContents;
    }

    private void populateMaps() {
        listViewOptions = new HashMap<>();
        listViewOptions.put("Day", () -> getLogsByDay());
        listViewOptions.put("Week",() -> getLogsByWeek());
        listViewOptions.put("Month", () -> getLogsByMonth());

        dateChanges = new HashMap<>();
        dateChanges.put("Day", Calendar.DAY_OF_WEEK);
        dateChanges.put("Week", Calendar.WEEK_OF_MONTH);
        dateChanges.put("Month", Calendar.MONTH);
    }

    public List<FoodLog> getListViewContents(String listType, Date dateToQuery) {
        this.listType = listType;
        this.dateToQuery = dateToQuery;
        calories = 0.0;
       // List<String> foodLogEntry = new ArrayList<>();
        foodLogs = new ArrayList<>();
        listViewOptions.get(listType).run();

        for(FoodLog foodLog : foodLogs) {
            //foodLogEntry.add(foodLog.toString());
            calories += foodLog.getCalories();
        }
        //return foodLogEntry;
        return foodLogs;
    }

    public String getDate() {
        return FoodLogImpl.formatDate(dateToQuery);
    }

    public Date updateDateNext(Date date) {
        return changeDate(date, 1);
    }

    public Date updateDatePrevious(Date date) {
        return changeDate(date, -1);
    }

    private Date changeDate(Date date, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(dateChanges.get(listType), value);
        Date newDate = calendar.getTime();
        return newDate;
    }

    public Double getCalories() {
        return calories;
    }

    private void getLogsByDay() {
        foodLogs = dao.getLogsByDay(dateToQuery);
    }

    private void getLogsByWeek() {
        foodLogs = dao.getLogsByWeek(dateToQuery);
    }

    private void getLogsByMonth() {
        foodLogs = dao.getLogsByMonth(dateToQuery);
    }

    public void deleteFoodLog(List<FoodLog> foodLogs) {
        dao.deleteFoodLogs(foodLogs);
    }
}
