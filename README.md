# Rule-Based Credit Card Fraud Detection System

A clean, modern, Java Swing desktop simulation application that demonstrates real-time credit card fraud detection using a deterministic rule-based heuristics approach.

## 📌 Overview

This program provides an interactive dashboard to test, evaluate, and visualize core transaction security metrics. It allows users to manually enter transactions or run a pre-packaged automated batch simulation to watch real-time rule-trigger patterns execute dynamically.

> ⚠️ **Simulation Note:** This application is entirely self-contained and operates as an educational simulation. It evaluates localized heuristics in a clean in-memory sandbox to demonstrate how transaction logic flags high-risk financial anomalies.
> 
> 

---

## 🚀 Key Features

* **Modern Dark UI:** Designed with an immersive dark aesthetic, featuring customized Swing components, distinct color-coded results, and a real-time responsive status bar.


* **Dual Processing Modes:**
* *Manual Verification:* Input individual card numbers, custom transaction amounts, and regional states to check execution live.


* *Automated Simulation:* Runs a built-in scenario sequence demonstrating how specific sequences of actions trip multiple rules over time.




* **Dynamic Customization:** Features an interactive threshold slider (ranging from Rs. 1,000 to Rs. 100,000) that modifies the high-amount evaluation threshold dynamically on the fly.


* **Persistent In-Memory History:** Tracks historical cards to lock in original "home states" and monitors continuous activity metrics (velocity limit checks).



---

## 🛡️ Fraud Detection Rule Engine

The simulation evaluates incoming transactions against four standalone architectural rules:

| Rule ID | Rule Name | Description | Trigger Boundary |
| --- | --- | --- | --- |
| **Rule 1** | **High Amount** | Flags any transaction with a value higher than the currently configured threshold.

 | `Amount > Current Slider Threshold` (Default: Rs. 10,000)

 |
| **Rule 2** | **Odd Hours** | Identifies risky late-night time windows typical of unauthorized nocturnal activity.

 | `Time between 12:00 AM and 05:00 AM` (Simulated transactions)

 |
| **Rule 3** | **Wrong State** | Checks geographic consistency. The location of the *very first* transaction locks in the card's home state.

 | `Current State != Initial Registered State`<br> |
| **Rule 4** | **Too Many Txns** | Prevents velocity-based card-exhaustion or card-testing attacks by capping usage.

 | `Total Transactions > 3`<br> |

---

## 🛠️ Setup & Quick Execution

The project includes automated launcher scripts that verify your Java environment, handle compilation, and run the simulation automatically.

### 📂 File Structure

For the automated launchers to work, ensure your files are named exactly as shown and placed in the same folder:

```text
📂 Card-Fraud-Simulation/
 ├── FraudDetection.java
 ├── run_windows.bat
 └── run_linux_mac.sh

```

### 🪟 Windows Setup

1. Double-click `run_windows.bat`.


2. The script will check for the Java Development Kit (JDK), compile `FraudDetection.java`, and launch the interface.


3. If Java is missing, it will provide direct download links automatically.



### 🐧 🍎 Linux & macOS Setup

1. Open your terminal in the project directory.


2. Give the script execution permissions:


```bash

```



chmod +x run_linux_mac.sh

```
3. Run the script[cite: 4]:
   ```bash
./run_linux_mac.sh

```

4. The script auto-detects your OS (Ubuntu, Fedora, or macOS) and provides the exact package manager command (`apt`, `dnf`, or `brew`) if Java needs to be installed.



### 💻 Manual CLI Fallback

If you prefer running it completely raw through the command line without using the scripts:

```bash
# Compile
javac FraudDetection.java

# Run
java FraudDetection

```

---

## 🖥️ Application Architecture

The core program structural pipeline consists of the following key pillars:

* **`Transaction` Data Model:** Captures active state footprints including card identifier strings, timestamps, pricing values, location states, fraud statuses, and detailed justification strings.


* **`analyze(...)` Method:** Runs deterministic multi-pass rule checks against cached data structures, appending validation results sequentially.


* **Swing UI Layer:** Implements decoupled layout components including customized table renderers (`DefaultTableCellRenderer`) that paint individual table rows dynamically—Red for **FRAUD** and Green for **SAFE**.



---

## 📝 License

This project is open-source and intended solely for educational, architectural simulation, and UI styling reference designs.
