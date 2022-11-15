
package banking;

import banking.dao.BankingDao;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;


public class Main {
    public static boolean isContinue = true;

    private static BankingDao dbConnection;

    public Main(String fileName) {
        dbConnection = new BankingDao(String.format("jdbc:sqlite:%s", fileName));
        dbConnection.createTable();
    }

    public static void main(String[] args) {
        Main app = new Main("card.s3db");
        app.start();
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        while (isContinue) {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            switch (scanner.nextInt()) {
                case 1 -> this.createAccount();
                case 2 -> this.logIntoAccount();
                case 0 -> {
                    dbConnection.closeDB();
                    System.out.println("Bye!");
                    isContinue = false;
                    return;
                }
                default -> System.out.println("Wrong input");
            }
        }
    }

    public void createAccount() {
        Account account = new Account();

        dbConnection.insertCard(account);

        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(account.getNumber());
        System.out.println("Your card PIN:");
        System.out.println(new DecimalFormat("0000").format(account.getPin()));
        System.out.println();

    }

    public void logIntoAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your card number:");
        long userInput = scanner.nextLong();
        System.out.println("Enter your PIN:");
        int userPin = scanner.nextInt();
        Account current = dbConnection.findCard(userInput, userPin);
        if (current == null) {
            System.out.println("Wrong card number or PIN!");
        } else {
            System.out.println("You have successfully logged in!");
            runAccount(current);
        }
    }

    public void runAccount(Account account) {
        Scanner scanner = new Scanner(System.in);
        boolean isLogged = true;
        while (isLogged) {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            switch (scanner.nextInt()) {
                case 1:
                    System.out.println("Balance: " + account.getBalance());
                    break;
                case 2:
                    makeDeposit(account);
                    break;
                case 3:
                    doTransfer(account);
                    break;
                case 4:
                    deleteAccount(account);
                    break;
                case 5:
                    isLogged = false;
                    System.out.println("You have successfully logged out!");
                    break;
                case 0:
                    System.out.println("Bye!");
                    isContinue = false;
                    isLogged = false;
                    break;
                default:
                    System.out.println("Wrong input");
                    break;
            }
        }
    }

    private void doTransfer(Account account) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String number = scanner.next();
        String errorMessage = checkNumber(account.getNumber(), number);
        if (errorMessage == null) {
            System.out.println("Enter how much money you want to transfer:");
            int money = scanner.nextInt();
            if (account.tryDoTransfer(money)) {
                dbConnection.doTransfer(account,money,number);
                System.out.println("Success!");
            } else {
                System.out.println("Not enough money!");
            }
        } else {
            System.out.println(errorMessage);
        }
    }

    private String checkNumber(String currentNumber, String number) {
        if (currentNumber == number) return "You can't transfer money to the same account!";
        if (!checkByLuhnAlg(number)) return "Probably you made a mistake in the card number. Please try again!";
        if (!dbConnection.checkCardIfExist(number)) return "Such a card does not exist.";
        return null;
    }

    private boolean checkByLuhnAlg(String number) {
        char[] arr = number.toCharArray();
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            int value = Character.getNumericValue(arr[i]);
            if (i % 2 == 0) {
                value *= 2;
                if (value >= 10) value -= 9;
            }
            sum += value;
        }
        return sum % 10 == 0;
    }

    private void deleteAccount(Account account) {
        dbConnection.deleteAccount(account);
        System.out.println("The account has been closed!");
    }

    private void makeDeposit(Account account) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter income:");
        int deposit = scanner.nextInt();
        account.addBalance(deposit);
        dbConnection.deposit(deposit, account.number);
        System.out.println("Income was added!");
    }


}