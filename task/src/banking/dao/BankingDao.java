package banking.dao;

import banking.Account;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BankingDao {
    private static Connection con;
    private Statement statement;

    public BankingDao(String url) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);
        try {
            con = dataSource.getConnection();
            statement = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        try (Statement statement = con.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS card (" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    number VARCHAR," +
                    "    pin VARCHAR," +
                    "    balance INT DEFAULT 0" +
                    ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertCard(Account account) {
        try {
            statement.executeUpdate(String.format("INSERT INTO card (number, pin, balance) VALUES ('%s', '%s', %d);",
                    account.getNumber(),
                    account.getPin(),
                    account.getBalance()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Account findCard(long number, int pin) {
        Account account = null;
        String srtN = number + "";
        try {
            try (ResultSet cards = statement.executeQuery(String.format("SELECT * FROM card\n" +
                    "WHERE number = '%s' AND pin = '%d' ;", srtN, pin))) {
                while (cards.next()) {
                    account = new Account(number, pin, cards.getInt("balance"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }

    public void closeDB() {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println("DB is CLOSE");
        }
    }

    private void updateBalance(int balance, String number) {
        String sql = String.format("UPDATE card SET balance = %d WHERE number = %s;", balance, number);
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deposit(int deposit, String number) {
        String sql = String.format("UPDATE card SET balance = balance + %d WHERE number = %s;", deposit, number);
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAccount(Account account) {
        String sql = String.format("DELETE FROM card WHERE number = %s;", account.getNumber());
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkCardIfExist(String number) {
        try (ResultSet card = statement.executeQuery(String.format("SELECT * FROM card\n" +
                "WHERE number = '%s' ;", number))) {
            return card.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void doTransfer(Account account, int money, String number) {
        updateBalance(account.getBalance(),account.getNumber());
        deposit(money,number);
    }
}

