import java.util.ArrayList;
import java.util.Random;

public class Main {
    private static ArrayList<String[]> users = new ArrayList<>();
    private static String loggedInUser = null;
    private static Random random = new Random();

    public static void main(String[] args) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.equals("exit")) {
                System.out.println("Goodbye!");
                break;
            }
            String[] parts = input.split(" ");
            String command = parts[0];
            try {
                switch (command) {
                    case "register":
                        if (parts.length == 6) {
                            register(parts[1], parts[2], parts[3], parts[4], parts[5]);
                        } else {
                            System.out.println("Error: Invalid number of arguments for register");
                        }
                        break;
                    case "login":
                        if (parts.length == 3) {
                            login(parts[1], parts[2]);
                        } else {
                            System.out.println("Error: Invalid number of arguments for login");
                        }
                        break;
                    case "show":
                        if (parts.length == 2 && parts[1].equals("balance")) {
                            showBalance();
                        } else {
                            System.out.println("Error: Invalid command");
                        }
                        break;
                    case "deposit":
                        if (parts.length == 2) {
                            deposit(parts[1]);
                        } else {
                            System.out.println("Error: Invalid number of arguments for deposit");
                        }
                        break;
                    case "withdraw":
                        if (parts.length == 2) {
                            withdraw(parts[1]);
                        } else {
                            System.out.println("Error: Invalid number of arguments for withdraw");
                        }
                        break;
                    case "transfer":
                        if (parts.length == 3) {
                            transfer(parts[1], parts[2]);
                        } else {
                            System.out.println("Error: Invalid number of arguments for transfer");
                        }
                        break;
                    case "logout":
                        logout();
                        break;
                    default:
                        System.out.println("Error: Unknown command");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void register(String username, String password, String fullName, String phoneNumber, String email) {
        for (String[] user : users) {
            if (user[0].equals(username)) {
                System.out.println("Error: username already exists.");
                return;
            }
        }
        if (phoneNumber.length() != 11 || !phoneNumber.startsWith("09")) {
            System.out.println("Error: invalid phone number.");
            return;
        }
        for (int i = 0; i < phoneNumber.length(); i++) {
            if (!Character.isDigit(phoneNumber.charAt(i))) {
                System.out.println("Error: invalid phone number.");
                return;
            }
        }
        int atIndex = email.indexOf('@');
        if (atIndex == -1 || atIndex == 0 || email.charAt(0) == '.') {
            System.out.println("Error: invalid email.");
            return;
        }
        String afterAt = email.substring(atIndex + 1);
        if (!afterAt.equals("aut.com")) {
            System.out.println("Error: invalid email.");
            return;
        }
        if (password.length() < 8) {
            System.out.println("Error: password must be at least 8 characters.");
            return;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        String specialChars = "@!&$?";
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) hasUpper = true;
            if (Character.isLowerCase(c)) hasLower = true;
            if (Character.isDigit(c)) hasDigit = true;
            if (specialChars.indexOf(c) != -1) hasSpecial = true;
        }
        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            System.out.println("Error: password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@, !, &, $, ?).");
            return;
        }
        String cardNumber;
        do {
            cardNumber = "6037";
            for (int i = 0; i < 12; i++) {
                cardNumber += random.nextInt(10);
            }
        } while (!isCardNumberUnique(cardNumber));
        String[] newUser = new String[7];
        newUser[0] = username;
        newUser[1] = password;
        newUser[2] = fullName;
        newUser[3] = cardNumber;
        newUser[4] = phoneNumber;
        newUser[5] = email;
        newUser[6] = "0";
        users.add(newUser);
        System.out.println("Registered successfully.");
        System.out.println("Assigned card number: " + cardNumber);
    }
    private static boolean isCardNumberUnique(String cardNumber) {
        for (String[] user : users) {
            if (user[3].equals(cardNumber)) {
                return false;
            }
        }
        return true;
    }
    private static void login(String username, String password) {
        if (loggedInUser != null) {
            System.out.println("Error: Another user is already logged in.");
            return;
        }
        for (String[] user : users) {
            if (user[0].equals(username)) {
                if (user[1].equals(password)) {
                    loggedInUser = username;
                    System.out.println("Login successful.");
                    return;
                } else {
                    System.out.println("Error: incorrect password.");
                    return;
                }
            }
        }
        System.out.println("Error: username not found.");
    }
    private static void showBalance() {
        if (loggedInUser == null) {
            System.out.println("Error: You should login first.");
            return;
        }
        String[] user = getUserByUsername(loggedInUser);
        System.out.println("Current balance: " + user[6]);
    }
    private static void deposit(String amountStr) {
        if (loggedInUser == null) {
            System.out.println("Error: You should login first.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                System.out.println("Error: amount must be positive.");
                return;
            }
            String[] user = getUserByUsername(loggedInUser);
            double currentBalance = Double.parseDouble(user[6]);
            double newBalance = currentBalance + amount;
            user[6] = String.valueOf(newBalance);
            System.out.println("Deposit successful. Current balance: " + newBalance);
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid amount.");
        }
    }
    private static void withdraw(String amountStr) {
        if (loggedInUser == null) {
            System.out.println("Error: You should login first.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                System.out.println("Error: amount must be positive.");
                return;
            }
            String[] user = getUserByUsername(loggedInUser);
            double currentBalance = Double.parseDouble(user[6]);
            if (currentBalance < amount) {
                System.out.println("Error: insufficient balance.");
                return;
            }
            double newBalance = currentBalance - amount;
            user[6] = String.valueOf(newBalance);
            System.out.println("Withdrawal successful. Current balance: " + newBalance);
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid amount.");
        }
    }
    private static void transfer(String cardNumber, String amountStr) {
        if (loggedInUser == null) {
            System.out.println("Error: You should login first.");
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                System.out.println("Error: amount must be positive.");
                return;
            }
            String[] destinationUser = null;
            for (String[] user : users) {
                if (user[3].equals(cardNumber)) {
                    destinationUser = user;
                    break;
                }
            }
            if (destinationUser == null) {
                System.out.println("Error: destination card number not found.");
                return;
            }
            if (destinationUser[0].equals(loggedInUser)) {
                System.out.println("Error: cannot transfer to your own account.");
                return;
            }
            String[] sourceUser = getUserByUsername(loggedInUser);
            double sourceBalance = Double.parseDouble(sourceUser[6]);
            if (sourceBalance < amount) {
                System.out.println("Error: insufficient balance.");
                return;
            }
            double destinationBalance = Double.parseDouble(destinationUser[6]);
            sourceUser[6] = String.valueOf(sourceBalance - amount);
            destinationUser[6] = String.valueOf(destinationBalance + amount);
            System.out.println("Transferred successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid amount.");
        }
    }
    private static void logout() {
        if (loggedInUser == null) {
            System.out.println("Error: No user is logged in.");
            return;
        }
        System.out.println("Logout successful.");
        loggedInUser = null;
    }
    private static String[] getUserByUsername(String username) {
        for (String[] user : users) {
            if (user[0].equals(username)) {
                return user;
            }
        }
        return null;
    }
}
