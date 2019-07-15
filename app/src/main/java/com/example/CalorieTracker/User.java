package com.example.CalorieTracker;


import android.icu.util.Calendar;

import java.sql.Array;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class User {
    private String name;
    private String eMail;
    private double height;
    private int weight;
    private int daysTracker;
    private int startweight;
    private int currentweight;
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public int getCurrentweight() {
        return currentweight;
    }

    public void setCurrentweight(int currentweight) {
        this.currentweight = currentweight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getDaysTracker() {
        return daysTracker;
    }

    public void setDaysTracker(int daysTracker) {
        this.daysTracker = daysTracker;
    }

    public int getStartweight() {
        return startweight;
    }

    public void setStartweight(int startweight) {
        this.startweight = startweight;
    }

    public Meal[] getFoodLog() {
        return foodLog;
    }

    public void setFoodLog(Meal[] foodLog) {
        this.foodLog = foodLog;
    }

    private Meal[] foodLog;

    public User(){}
    //this helps deal with the database asynchronous fetching
    public User(User copy){
        //copy each value from copy onto the new one
        name = copy.name;
        eMail = copy.eMail;
        height = copy.height;
        weight = copy.weight;
        daysTracker = copy.daysTracker;
        startweight = copy.startweight;
        foodLog = copy.foodLog;
    }


    public User(String name, String eMail){
        this.name = name;
        this.eMail = eMail;
    }





    class Meal{
        private String[] foodName;
        private int[] calories;
        private String MealID;

        Meal(String name, int count) {
            foodName[foodName.length-1] = name;
            calories[calories.length-1] = count;
        }

        int getCalories(int num){
            return calories[num];
        }

        String getFood(int num){
            return foodName[num];
        }
    }



}
