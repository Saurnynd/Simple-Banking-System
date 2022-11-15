package banking;

import java.text.DecimalFormat;
import java.util.Random;

public class Account {
    String number;
    int pin;
    private Random random = new Random();
    int balance;

    public int getBalance() {
        return balance;
    }

    public String getNumber() {
        return number;
    }

    public int getPin() {
        return pin;
    }
    private long generateLuneNumber() {
        long numb = Long.parseLong("400000" + new DecimalFormat("000000000").format(random.nextLong(1_000_000_000L)));
        int counter = 15;
        int sum = 0;
        for (long i = numb; i > 0; i /= 10) {
            long temp = i % 10;
            if (counter % 2 == 1) {
                temp *= 2;
                if (temp >= 10) temp -= 9;
            }

            sum += temp;
            counter--;
        }
        if (sum % 10 == 0) {
            numb *= 10;
        } else {
            numb = numb * 10 + 10 - sum % 10;
        }

        return numb;
    }

    public void addBalance(int value){
        this.balance+=value;
    }
    public boolean tryDoTransfer(int value){
        if(this.balance >= value){
            this.balance -= value;
            return true;
        }
        return false;
    }

    public Account() {
        this.number = generateLuneNumber() + "";
        this.pin = (int) (Math.random() * (9999 - 1000) + 1000);
        this.balance = 0;

    }
    public Account(long numb,int pin,int balance) {
        this.number = numb + "";
        this.pin = pin;
        this.balance = balance;

    }
}
