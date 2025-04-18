import java.time.LocalDate;
// Abstraction - Core banking entity
abstract class BankEntity {
protected int id;
protected String name;
protected boolean isActive;
public BankEntity(int id, String name) {
this.id = id;
this.name = name;
this.isActive = true;
}
public abstract void displayDetails();
public void deactivate() {
this.isActive = false;
}
}
// Inheritance - Account is a BankEntity
class Account extends BankEntity {
// Encapsulation - private fields with public methods
private String phone;
private double balance;
private float interestRate;
private List<String> transactions;
private List<String> billPayments;
private double savingsGoal;
public Account(int accountNumber, String accountHolder, String phone,
double balance, float interestRate) {
super(accountNumber, accountHolder);
this.phone = phone;
this.balance = balance;
this.interestRate = interestRate;
this.transactions = new ArrayList<>();
this.billPayments = new ArrayList<>();
this.savingsGoal = 0;
}
@Override
public void displayDetails() {
System.out.printf("Account %d: %s, Balance: $%.2f, Interest: %.2f%%\n",
id, name, balance, interestRate);
}
// Encapsulated methods to access/modify balance
public void deposit(double amount) {
if (amount > 0) {
balance += amount;
transactions.add(LocalDate.now() + ": Deposit $" + amount);
}
}
public void withdraw(double amount) throws InsufficientFundsException {
if (amount > balance) {
throw new InsufficientFundsException("Not enough money in account");
}
balance -= amount;
transactions.add(LocalDate.now() + ": Withdrawal $" + amount);
}
public void transfer(Account recipient, double amount) throws InsufficientFundsException {
this.withdraw(amount);
recipient.deposit(amount);
transactions.add(LocalDate.now() + ": Transfer $" + amount + " to " + recipient.id);
}
public double calculateInterest() {
return balance * (interestRate / 100);
}
public void addInterest() {
double interest = calculateInterest();
deposit(interest);
transactions.add(LocalDate.now() + ": Interest added $" + interest);
}
public void payBill(String billType, double amount) throws InsufficientFundsException {
withdraw(amount);
billPayments.add(billType + " paid: $" + amount);
}
public void setSavingsGoal(double goal) {
this.savingsGoal = goal;
}
public void checkSavingsProgress() {
System.out.printf("Savings Goal: $%.2f of $%.2f (%.1f%%)\n",
balance, savingsGoal,
(savingsGoal > 0 ? (balance/savingsGoal)*100 : 0));
}
public void convertCurrency(double exchangeRate, String currency) {
System.out.printf("$%.2f = %.2f %s\n", balance, balance*exchangeRate, currency);
}
// Getters for encapsulated fields
public double getBalance() { return balance; }
public List<String> getTransactions() { return Collections.unmodifiableList(transactions); }
public int getId() { return id; }
}
// Inheritance - Loan is a BankEntity
class Loan extends BankEntity {
private double amount;
private float interestRate;
private int termMonths;
private double amountPaid;
public Loan(int loanId, String loanName, double amount,
float interestRate, int termMonths) {
super(loanId, loanName);
this.amount = amount;
this.interestRate = interestRate;
this.termMonths = termMonths;
this.amountPaid = 0;
}
@Override
public void displayDetails() {
System.out.printf("Loan %d: %s, Amount: $%.2f, Paid: $%.2f, Term: %d months\n",
id, name, amount, amountPaid, termMonths);
}
public double calculateTotalAmount() {
return amount + (amount * interestRate / 100);
}
public void makePayment(double payment) {
if (payment <= 0) return;
amountPaid += payment;
if (amountPaid >= calculateTotalAmount()) {
amountPaid = calculateTotalAmount();
deactivate();
}
}
public double getRemainingAmount() {
return calculateTotalAmount() - amountPaid;
}
}
// Custom exception for encapsulation
class InsufficientFundsException extends Exception {
public InsufficientFundsException(String message) {
super(message);
}
}
// Bank system implementation
public class BankManagementSystem {
private static List<Account> accounts = new ArrayList<>();
private static List<Loan> loans = new ArrayList<>();
private static Scanner scanner = new Scanner(System.in);
public static void main(String[] args) {
while (true) {
System.out.println("\n===== Bank System Menu =====");
System.out.println("1. Create Account");
System.out.println("2. Deposit");
System.out.println("3. Withdraw");
System.out.println("4. Transfer");
System.out.println("5. Manage Loans");
System.out.println("6. View Accounts");
System.out.println("7. View Loans");
System.out.println("8. Pay Bill");
System.out.println("9. Set Savings Goal");
System.out.println("10. Add Interest");
System.out.println("11. Currency Conversion");
System.out.println("0. Exit");
System.out.print("Enter choice: ");
int choice = scanner.nextInt();
try {
switch (choice) {
case 1: createAccount(); break;
case 2: depositMoney(); break;
case 3: withdrawMoney(); break;
case 4: transferMoney(); break;
case 5: manageLoans(); break;
case 6: viewAccounts(); break;
case 7: viewLoans(); break;
case 8: payBill(); break;
case 9: setSavingsGoal(); break;
case 10: addInterest(); break;
case 11: convertCurrency(); break;
case 0: System.exit(0);
default: System.out.println("Invalid choice");
}
} catch (InsufficientFundsException e) {
System.out.println("Error: " + e.getMessage());
}
}
}
private static void createAccount() {
System.out.print("Enter account number: ");
int id = scanner.nextInt();
scanner.nextLine(); // consume newline
// Check if account already exists
for (Account acc : accounts) {
if (acc.getId() == id) {
System.out.println("Account already exists!");
return;
}
}
System.out.print("Enter account holder name: ");
String name = scanner.nextLine();
System.out.print("Enter phone number: ");
String phone = scanner.nextLine();
System.out.print("Enter initial balance: ");
double balance = scanner.nextDouble();
System.out.print("Enter interest rate (%): ");
float interest = scanner.nextFloat();
Account account = new Account(id, name, phone, balance, interest);
accounts.add(account);
System.out.println("Account created successfully!");
}
private static void depositMoney() {
System.out.print("Enter account number: ");
int accountNumber = scanner.nextInt();
Account account = findAccount(accountNumber);
if (account == null) {
System.out.println("Account not found!");
return;
}
System.out.print("Enter deposit amount: ");
double amount = scanner.nextDouble();
account.deposit(amount);
System.out.println("Deposit successful. New balance: " + account.getBalance());
}
private static void withdrawMoney() throws InsufficientFundsException {
System.out.print("Enter account number: ");
int accountNumber = scanner.nextInt();
Account account = findAccount(accountNumber);
if (account == null) {
System.out.println("Account not found!");
return;
}
System.out.print("Enter withdrawal amount: ");
double amount = scanner.nextDouble();
account.withdraw(amount);
System.out.println("Withdrawal successful. New balance: " + account.getBalance());
}
private static void transferMoney() throws InsufficientFundsException {
System.out.print("Enter source account number: ");
int fromAccountNumber = scanner.nextInt();
Account fromAccount = findAccount(fromAccountNumber);
if (fromAccount == null) {
System.out.println("Source account not found!");
return;
}
System.out.print("Enter destination account number: ");
int toAccountNumber = scanner.nextInt();
Account toAccount = findAccount(toAccountNumber);
if (toAccount == null) {
System.out.println("Destination account not found!");
return;
}
System.out.print("Enter transfer amount: ");
double amount = scanner.nextDouble();
fromAccount.transfer(toAccount, amount);
System.out.println("Transfer successful!");
System.out.println("From account new balance: " + fromAccount.getBalance());
System.out.println("To account new balance: " + toAccount.getBalance());
}
private static void manageLoans() {
System.out.println("\n1. Create Loan\n2. Make Payment\nEnter choice: ");
int choice = scanner.nextInt();
if (choice == 1) {
System.out.print("Enter loan ID: ");
int id = scanner.nextInt();
scanner.nextLine();
System.out.print("Enter loan name: ");
String name = scanner.nextLine();
System.out.print("Enter loan amount: ");
double amount = scanner.nextDouble();
System.out.print("Enter interest rate (%): ");
float interest = scanner.nextFloat();
System.out.print("Enter term (months): ");
int term = scanner.nextInt();
Loan loan = new Loan(id, name, amount, interest, term);
loans.add(loan);
System.out.println("Loan created!");
}
else if (choice == 2) {
System.out.print("Enter loan ID: ");
int id = scanner.nextInt();
Loan loan = findLoan(id);
if (loan == null) {
System.out.println("Loan not found!");
return;
}
System.out.print("Enter payment amount: ");
double payment = scanner.nextDouble();
loan.makePayment(payment);
System.out.println("Payment applied. Remaining: " + loan.getRemainingAmount());
}
}
private static void payBill() throws InsufficientFundsException {
System.out.print("Enter account number: ");
int accountNumber = scanner.nextInt();
Account account = findAccount(accountNumber);
if (account == null) {
System.out.println("Account not found!");
return;
}
scanner.nextLine(); // consume newline
System.out.print("Enter bill type: ");
String type = scanner.nextLine();
System.out.print("Enter bill amount: ");
double amount = scanner.nextDouble();
account.payBill(type, amount);
System.out.println("Bill paid successfully!");
}
private static void setSavingsGoal() {
System.out.print("Enter account number: ");
int accountNumber = scanner.nextInt();
Account account = findAccount(accountNumber);
if (account == null) {
System.out.println("Account not found!");
return;
}
System.out.print("Enter savings goal: ");
double goal = scanner.nextDouble();
account.setSavingsGoal(goal);
System.out.println("Savings goal set!");
}
private static void addInterest() {
System.out.print("Enter account number: ");
int accountNumber = scanner.nextInt();
Account account = findAccount(accountNumber);
if (account == null) {
System.out.println("Account not found!");
return;
}
account.addInterest();
System.out.println("Interest added. New balance: " + account.getBalance());
}
private static void convertCurrency() {
System.out.print("Enter account number: ");
int accountNumber = scanner.nextInt();
Account account = findAccount(accountNumber);
if (account == null) {
System.out.println("Account not found!");
return;
}
System.out.print("Enter exchange rate: ");
double rate = scanner.nextDouble();
scanner.nextLine(); // consume newline
System.out.print("Enter currency code: ");
String currency = scanner.nextLine();
account.convertCurrency(rate, currency);
}
private static void viewAccounts() {
System.out.println("\n===== Accounts =====");
for (Account acc : accounts) {
acc.displayDetails();
}
}
private static void viewLoans() {
System.out.println("\n===== Loans =====");
for (Loan loan : loans) {
loan.displayDetails();
}
}
private static Account findAccount(int accountNumber) {
for (Account acc : accounts) {
if (acc.getId() == accountNumber) {
return acc;
}
}
return null;
}
private static Loan findLoan(int loanId) {
for (Loan loan : loans) {
if (loan.id == loanId) {
return loan;
}
}
return null;
}
}