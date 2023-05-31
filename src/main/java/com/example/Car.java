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

    public String prova(){
        System.out.println("Questo è un metodo che contiene un FOR");
        for(int i=0;i<10;i++){
            for(int j=0;j<10;j++){
                int s = 10;
                System.out.println(s);
            }
            int k = i;
            System.out.println(k);
        }
        int stop = 10;
        do{
            stop--;
        } while(stop!=10);

        int flag = 1;
        while(flag == 1){
            flag = 0;
            System.out.println("Endwhile");
        }
        int varSwitch = 2;
        switch(varSwitch){
            case 1:
                System.out.println("1");
                break;
            case 2:
                System.out.println("1");
                break;
            case 3:
                System.out.println("1");
                break;
            default:
                System.out.println("No value");
        }

        String[] cars = {"Volvo", "BMW", "Ford", "Mazda"};
        for (String i : cars) {
            System.out.println(i);
        }

        try {
            int[] myNumbers = {1, 2, 3};
            System.out.println(myNumbers[10]);
        } catch (Exception e) {
            System.out.println("Something went wrong.");
        }
        return "Metodo terminato";
    }
}