# MiniBank – Java Banking Application

## Project Overview

MiniBank is a console-based banking application developed using Java. It demonstrates Object-Oriented Programming, exception handling, multithreading, collections, file handling, generics, reflection, and JUnit testing.

## Features

* Open savings accounts
* Deposit and withdraw money
* Transfer money between accounts
* View account statements
* Display all accounts
* Process batch transactions using multithreading
* Save and load account data
* Generate transaction reports
* Validate banking operations using custom exceptions

## Technologies Used

* Java 26
* IntelliJ IDEA
* JUnit 5
* Git and GitHub

## Project Structure

* `src/model` – Account classes and interfaces
* `src/service` – Banking services and transaction processing
* `src/util` – Utility and validation classes
* `src/exception` – Custom exceptions
* `test` – JUnit test classes
* `data` – Saved account data and reports
* `logs` – Transaction log files

## How to Run

1. Open the project in IntelliJ IDEA.
2. Configure JDK 26.
3. Build the project.
4. Run `MiniBank.java`.
5. Follow the console menu.

## Testing

JUnit 5 tests verify:

* Deposit increases account balance.
* Withdrawal beyond the permitted limit throws an exception.
* Concurrent deposits maintain the correct balance.

## Author

Rajdeep R Bariya (25IT005)

## Project

MiniBank – Java Semester-III Project
