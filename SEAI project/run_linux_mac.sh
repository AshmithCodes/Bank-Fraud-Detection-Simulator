#!/bin/bash

echo "================================"
echo " Card Fraud Detection - Launcher"
echo "================================"

# ---- Check if java is installed ----
if ! command -v java &> /dev/null; then
    echo ""
    echo "[ERROR] Java is NOT installed."
    echo ""

    # Detect OS and give the right install command
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "Install it with:"
        echo "  sudo apt install default-jdk        (Ubuntu/Debian)"
        echo "  sudo dnf install java-21-openjdk    (Fedora)"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        echo "Install it with:"
        echo "  brew install openjdk"
        echo "  OR download from https://adoptium.net/"
    else
        echo "Download from: https://adoptium.net/"
    fi

    echo ""
    exit 1
fi

# ---- Check if javac (compiler) is available ----
if ! command -v javac &> /dev/null; then
    echo ""
    echo "[ERROR] Java Runtime found but JDK (compiler) is missing."
    echo "You need the full JDK, not just the JRE."
    echo ""
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "  sudo apt install default-jdk"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        echo "  brew install openjdk"
    fi
    echo ""
    exit 1
fi

echo "[OK] Java found: $(java -version 2>&1 | head -1)"

# ---- Check FraudDetection.java exists ----
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ ! -f "$SCRIPT_DIR/FraudDetection.java" ]; then
    echo ""
    echo "[ERROR] FraudDetection.java not found in: $SCRIPT_DIR"
    echo "Make sure this script is in the same folder as FraudDetection.java"
    echo ""
    exit 1
fi

# ---- Compile ----
echo "Compiling FraudDetection.java..."
javac "$SCRIPT_DIR/FraudDetection.java" -d "$SCRIPT_DIR"

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERROR] Compilation failed."
    echo ""
    exit 1
fi

echo "[OK] Compiled successfully."
echo "Launching..."
echo ""

# ---- Run ----
java -cp "$SCRIPT_DIR" FraudDetection
