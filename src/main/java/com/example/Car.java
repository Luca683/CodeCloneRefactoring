package com.example;

public class Car{
    private String color;
    private int speed;
    private boolean on;

    public Car(String c){
        color = c;
        speed = 0;
        on = false;
    }
    
    public void turnOn(){ on = true; }
    public void turnOff(){ on = false; }
    public void accelerate(){
        System.out.println("Sto aumentando la velocità");
        if(on){
            speed++;
            System.out.println("Velocità aumentata di 1");
        }
        else{
            System.out.println("La macchina è spenta");
        }
    }
    public String getColor(){ return color; }
    public int getSpeed(){ return speed; }
    public boolean isOn(){ return on; }
    public void printCarInfo(){
        System.out.println("Car color: " + getColor());
        System.out.println("Car speed: " + getSpeed());
    }
}