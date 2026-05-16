import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OptionMenu {

	private final Scanner menuInput = new Scanner(System.in);
	private final DecimalFormat moneyFormat = new DecimalFormat("'$'###,##0.00");
	private final HashMap<Integer, Account> data = new HashMap<>();

	private static final String ACCOUNT_FILE = "accounts.txt";
	private static final String LOG_FILE = "transaction-log.txt";
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public OptionMenu() throws IOException {
		loadAccounts();

		// If accounts.txt does not exist yet, add default test accounts
		if (data.isEmpty()) {
			data.put(952141, new Account(952141, 191904, 1000, 5000));
			data.put(123, new Account(123, 123, 20000, 50000));
			saveAccounts();
		}
	}

	public void mainMenu() throws IOException {
		boolean end = false;

		while (!end) {
			try {
				System.out.println("\n========== MAIN MENU ==========");
				System.out.println(" Type 1 - Login");
				System.out.println(" Type 2 - Create Account");
				System.out.println(" Type 3 - Show All Account Balances");
				System.out.println(" Type 4 - Exit");
				System.out.print("\nChoice: ");

				int choice = menuInput.nextInt();

				switch (choice) {
					case 1:
						getLogin();
						break;

					case 2:
						createAccount();
						break;

					case 3:
						showAllAccountBalances();
						break;

					case 4:
						end = true;
						break;

					default:
						System.out.println("\nInvalid Choice.");
				}

			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice. Please enter numbers only.");
				menuInput.next();
			}
		}

		saveAccounts();
		System.out.println("\nThank you for using this ATM.\n");
	}

	public void getLogin() throws IOException {
		boolean end = false;

		while (!end) {
			try {
				System.out.print("\nEnter your customer number: ");
				int customerNumber = menuInput.nextInt();

				System.out.print("Enter your PIN number: ");
				int pinNumber = menuInput.nextInt();

				Account account = data.get(customerNumber);

				if (account != null && pinNumber == account.getPinNumber()) {
					logTransaction(account, "LOGIN", "User logged in successfully");
					getAccountType(account);
					end = true;
				} else {
					System.out.println("\nWrong Customer Number or PIN Number.");
				}

			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Character(s). Only numbers are allowed.");
				menuInput.next();
			}
		}
	}

	public void createAccount() throws IOException {
		try {
			System.out.print("\nEnter your new customer number: ");
			int customerNumber = menuInput.nextInt();

			if (data.containsKey(customerNumber)) {
				System.out.println("\nThis customer number is already registered.");
				return;
			}

			System.out.print("Enter PIN to be registered: ");
			int pin = menuInput.nextInt();

			Account account = new Account(customerNumber, pin);
			data.put(customerNumber, account);

			saveAccounts();
			logTransaction(account, "CREATE ACCOUNT", "New account created");

			System.out.println("\nYour new account has been successfully registered!");
			System.out.println("Now you can login from the main menu.");

		} catch (InputMismatchException e) {
			System.out.println("\nInvalid Choice. Please enter numbers only.");
			menuInput.next();
		}
	}

	public void getAccountType(Account account) throws IOException {
		boolean end = false;

		while (!end) {
			try {
				System.out.println("\n========== ACCOUNT MENU ==========");
				System.out.println(" Type 1 - Checking Account");
				System.out.println(" Type 2 - Savings Account");
				System.out.println(" Type 3 - Show Statement");
				System.out.println(" Type 4 - Show Transaction History");
				System.out.println(" Type 5 - Logout");
				System.out.print("\nChoice: ");

				int selection = menuInput.nextInt();

				switch (selection) {
					case 1:
						getChecking(account);
						break;

					case 2:
						getSaving(account);
						break;

					case 3:
						System.out.println(account.getStatement());
						break;

					case 4:
						showTransactionHistory(account);
						break;

					case 5:
						logTransaction(account, "LOGOUT", "User logged out");
						end = true;
						break;

					default:
						System.out.println("\nInvalid Choice.");
				}

			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice. Please enter numbers only.");
				menuInput.next();
			}
		}
	}

	public void getChecking(Account account) throws IOException {
		boolean end = false;

		while (!end) {
			try {
				System.out.println("\n========== CHECKING ACCOUNT ==========");
				System.out.println(" Type 1 - View Balance");
				System.out.println(" Type 2 - Withdraw Funds");
				System.out.println(" Type 3 - Deposit Funds");
				System.out.println(" Type 4 - Transfer Funds to Savings");
				System.out.println(" Type 5 - Exit Checking");
				System.out.print("\nChoice: ");

				int selection = menuInput.nextInt();

				switch (selection) {
					case 1:
						System.out.println("\nChecking Balance: "
								+ moneyFormat.format(account.getCheckingBalance()));
						break;

					case 2:
						withdrawChecking(account);
						break;

					case 3:
						depositChecking(account);
						break;

					case 4:
						transferCheckingToSaving(account);
						break;

					case 5:
						end = true;
						break;

					default:
						System.out.println("\nInvalid Choice.");
				}

			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice. Please enter numbers only.");
				menuInput.next();
			}
		}
	}

	public void getSaving(Account account) throws IOException {
		boolean end = false;

		while (!end) {
			try {
				System.out.println("\n========== SAVINGS ACCOUNT ==========");
				System.out.println(" Type 1 - View Balance");
				System.out.println(" Type 2 - Withdraw Funds");
				System.out.println(" Type 3 - Deposit Funds");
				System.out.println(" Type 4 - Transfer Funds to Checking");
				System.out.println(" Type 5 - Exit Savings");
				System.out.print("\nChoice: ");

				int selection = menuInput.nextInt();

				switch (selection) {
					case 1:
						System.out.println("\nSavings Balance: "
								+ moneyFormat.format(account.getSavingBalance()));
						break;

					case 2:
						withdrawSaving(account);
						break;

					case 3:
						depositSaving(account);
						break;

					case 4:
						transferSavingToChecking(account);
						break;

					case 5:
						end = true;
						break;

					default:
						System.out.println("\nInvalid Choice.");
				}

			} catch (InputMismatchException e) {
				System.out.println("\nInvalid Choice. Please enter numbers only.");
				menuInput.next();
			}
		}
	}

	private void withdrawChecking(Account account) throws IOException {
		System.out.println("\nCurrent Checking Balance: "
				+ moneyFormat.format(account.getCheckingBalance()));

		System.out.print("Amount to withdraw from Checking: ");
		double amount = menuInput.nextDouble();

		if (account.withdrawChecking(amount)) {
			String message = "Withdrew " + moneyFormat.format(amount) + " from Checking";

			System.out.println("\n" + message);
			System.out.println("New Checking Balance: "
					+ moneyFormat.format(account.getCheckingBalance()));

			logTransaction(account, "WITHDRAW CHECKING", message);
			saveAccounts();

		} else {
			System.out.println("\nTransaction failed. Amount must be positive and balance cannot be negative.");
		}
	}

	private void withdrawSaving(Account account) throws IOException {
		System.out.println("\nCurrent Savings Balance: "
				+ moneyFormat.format(account.getSavingBalance()));

		System.out.print("Amount to withdraw from Savings: ");
		double amount = menuInput.nextDouble();

		if (account.withdrawSaving(amount)) {
			String message = "Withdrew " + moneyFormat.format(amount) + " from Savings";

			System.out.println("\n" + message);
			System.out.println("New Savings Balance: "
					+ moneyFormat.format(account.getSavingBalance()));

			logTransaction(account, "WITHDRAW SAVINGS", message);
			saveAccounts();

		} else {
			System.out.println("\nTransaction failed. Amount must be positive and balance cannot be negative.");
		}
	}

	private void depositChecking(Account account) throws IOException {
		System.out.println("\nCurrent Checking Balance: "
				+ moneyFormat.format(account.getCheckingBalance()));

		System.out.print("Amount to deposit into Checking: ");
		double amount = menuInput.nextDouble();

		if (account.depositChecking(amount)) {
			String message = "Deposited " + moneyFormat.format(amount) + " into Checking";

			System.out.println("\n" + message);
			System.out.println("New Checking Balance: "
					+ moneyFormat.format(account.getCheckingBalance()));

			logTransaction(account, "DEPOSIT CHECKING", message);
			saveAccounts();

		} else {
			System.out.println("\nTransaction failed. Deposit amount must be positive.");
		}
	}

	private void depositSaving(Account account) throws IOException {
		System.out.println("\nCurrent Savings Balance: "
				+ moneyFormat.format(account.getSavingBalance()));

		System.out.print("Amount to deposit into Savings: ");
		double amount = menuInput.nextDouble();

		if (account.depositSaving(amount)) {
			String message = "Deposited " + moneyFormat.format(amount) + " into Savings";

			System.out.println("\n" + message);
			System.out.println("New Savings Balance: "
					+ moneyFormat.format(account.getSavingBalance()));

			logTransaction(account, "DEPOSIT SAVINGS", message);
			saveAccounts();

		} else {
			System.out.println("\nTransaction failed. Deposit amount must be positive.");
		}
	}

	private void transferCheckingToSaving(Account account) throws IOException {
		System.out.println("\nCurrent Checking Balance: "
				+ moneyFormat.format(account.getCheckingBalance()));

		System.out.print("Amount to transfer from Checking to Savings: ");
		double amount = menuInput.nextDouble();

		if (account.transferCheckingToSaving(amount)) {
			String message = "Transferred " + moneyFormat.format(amount)
					+ " from Checking to Savings";

			System.out.println("\n" + message);

			logTransaction(account, "TRANSFER", message);
			saveAccounts();

		} else {
			System.out.println("\nTransfer failed. Amount must be positive and balance cannot be negative.");
		}
	}

	private void transferSavingToChecking(Account account) throws IOException {
		System.out.println("\nCurrent Savings Balance: "
				+ moneyFormat.format(account.getSavingBalance()));

		System.out.print("Amount to transfer from Savings to Checking: ");
		double amount = menuInput.nextDouble();

		if (account.transferSavingToChecking(amount)) {
			String message = "Transferred " + moneyFormat.format(amount)
					+ " from Savings to Checking";

			System.out.println("\n" + message);

			logTransaction(account, "TRANSFER", message);
			saveAccounts();

		} else {
			System.out.println("\nTransfer failed. Amount must be positive and balance cannot be negative.");
		}
	}

	private void showTransactionHistory(Account account) {
		System.out.println("\n========== TRANSACTION HISTORY ==========");

		if (account.getTransactionHistory().isEmpty()) {
			System.out.println("No transactions found.");
		} else {
			for (String transaction : account.getTransactionHistory()) {
				System.out.println(transaction);
			}
		}

		System.out.println("=========================================");
	}

	private void showAllAccountBalances() {
		System.out.println("\n========== ALL ACCOUNT BALANCES ==========");

		for (Map.Entry<Integer, Account> entry : data.entrySet()) {
			Account account = entry.getValue();

			System.out.println("Customer Number: " + account.getCustomerNumber()
					+ " | Checking: " + moneyFormat.format(account.getCheckingBalance())
					+ " | Savings: " + moneyFormat.format(account.getSavingBalance())
					+ " | Total: "
					+ moneyFormat.format(account.getCheckingBalance() + account.getSavingBalance()));
		}

		System.out.println("==========================================");
	}

	private void saveAccounts() throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNT_FILE))) {
			for (Account account : data.values()) {
				writer.write(account.toFileString());
				writer.newLine();
			}
		}
	}

	private void loadAccounts() throws IOException {
		File file = new File(ACCOUNT_FILE);

		if (!file.exists()) {
			return;
		}

		try (Scanner fileScanner = new Scanner(file)) {
			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine();

				if (!line.isBlank()) {
					Account account = Account.fromFileString(line);
					data.put(account.getCustomerNumber(), account);
				}
			}
		}
	}

	private void logTransaction(Account account, String type, String message) throws IOException {
		String logLine = LocalDateTime.now().format(DATE_FORMAT)
				+ " | Customer: " + account.getCustomerNumber()
				+ " | " + type
				+ " | " + message;

		account.addHistory(logLine);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
			writer.write(logLine);
			writer.newLine();
		}
	}
}