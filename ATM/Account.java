import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Account {
	// variables
	private int customerNumber;
	private int pinNumber;
	private double checkingBalance;
	private double savingBalance;
	private List<String> transactionHistory;

	private static final DecimalFormat moneyFormat = new DecimalFormat("'$'###,##0.00");

	public Account() {
		this.transactionHistory = new ArrayList<>();
	}

	public Account(int customerNumber, int pinNumber) {
		this.customerNumber = customerNumber;
		this.pinNumber = pinNumber;
		this.checkingBalance = 0;
		this.savingBalance = 0;
		this.transactionHistory = new ArrayList<>();
	}

	public Account(int customerNumber, int pinNumber, double checkingBalance, double savingBalance) {
		this.customerNumber = customerNumber;
		this.pinNumber = pinNumber;
		this.checkingBalance = checkingBalance;
		this.savingBalance = savingBalance;
		this.transactionHistory = new ArrayList<>();
	}

	public int getCustomerNumber() {
		return customerNumber;
	}

	public int getPinNumber() {
		return pinNumber;
	}

	public double getCheckingBalance() {
		return checkingBalance;
	}

	public double getSavingBalance() {
		return savingBalance;
	}

	public List<String> getTransactionHistory() {
		return transactionHistory;
	}

	public void addHistory(String transaction) {
		transactionHistory.add(transaction);
	}

	public String getStatement() {
		return "\n========== ACCOUNT STATEMENT =========="
				+ "\nCustomer Number: " + customerNumber
				+ "\nChecking Balance: " + moneyFormat.format(checkingBalance)
				+ "\nSavings Balance: " + moneyFormat.format(savingBalance)
				+ "\nTotal Balance: " + moneyFormat.format(checkingBalance + savingBalance)
				+ "\n=======================================";
	}

	public boolean withdrawChecking(double amount) {
		if (amount > 0 && checkingBalance >= amount) {
			checkingBalance = checkingBalance - amount;
			return true;
		}
		return false;
	}

	public boolean withdrawSaving(double amount) {
		if (amount > 0 && savingBalance >= amount) {
			savingBalance = savingBalance - amount;
			return true;
		}
		return false;
	}

	public boolean depositChecking(double amount) {
		if (amount > 0) {
			checkingBalance = checkingBalance + amount;
			return true;
		}
		return false;
	}

	public boolean depositSaving(double amount) {
		if (amount > 0) {
			savingBalance = savingBalance + amount;
			return true;
		}
		return false;
	}

	public boolean transferCheckingToSaving(double amount) {
		if (amount > 0 && checkingBalance >= amount) {
			checkingBalance = checkingBalance - amount;
			savingBalance = savingBalance + amount;
			return true;
		}
		return false;
	}

	public boolean transferSavingToChecking(double amount) {
		if (amount > 0 && savingBalance >= amount) {
			savingBalance = savingBalance - amount;
			checkingBalance = checkingBalance + amount;
			return true;
		}
		return false;
	}

	// This method converts account data into one line for accounts.txt
	public String toFileString() {
		String history = String.join("~", transactionHistory);
		return customerNumber + "," + pinNumber + "," + checkingBalance + "," + savingBalance + "," + history;
	}

	// This method converts one line from accounts.txt back into an Account object
	public static Account fromFileString(String line) {
		String[] parts = line.split(",", 5);

		int customerNumber = Integer.parseInt(parts[0]);
		int pinNumber = Integer.parseInt(parts[1]);
		double checkingBalance = Double.parseDouble(parts[2]);
		double savingBalance = Double.parseDouble(parts[3]);

		Account account = new Account(customerNumber, pinNumber, checkingBalance, savingBalance);

		if (parts.length == 5 && !parts[4].isBlank()) {
			String[] historyItems = parts[4].split("~");

			for (String item : historyItems) {
				account.addHistory(item);
			}
		}

		return account;
	}
}
