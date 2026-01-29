# Custom Assembly Interpreter

A custom-designed assembly-like language interpreter developed step by step, covering
lexical analysis, syntax validation, execution simulation, and machine language generation.

This project demonstrates the fundamental concepts behind interpreters, CPU simulation,
and low-level instruction processing using Java.

---

## Project Overview

The goal of this project is to build a complete execution pipeline for a custom 8-bit
assembly language. Each stage focuses on a core component of language processing,
from reading raw input to producing executable machine code.

The project was developed incrementally, with each stage representing a key phase
in the lifecycle of an interpreter.

---

## Project Stages

### Stage 1 – Tokenizer
Performs lexical analysis by reading source code and converting raw text into meaningful tokens.
This stage identifies instructions, operands, and symbols.

### Stage 2 – Parser
Validates the syntax of the custom assembly language based on predefined grammar rules.
Ensures that instructions and operands follow correct structural patterns.

### Stage 3 – Execution Engine
Simulates the execution of assembly instructions, including:
- Register simulation (AX, BX, CX, DX)
- Memory simulation (256-cell RAM)
- Flag handling (zero, sign, overflow)
- Instruction-level execution logic

### Stage 4 – Machine Language Generation
Converts valid assembly instructions into hexadecimal machine code.
Handles opcode mapping, operand encoding, and label resolution.

---

## Documentation

Detailed project specifications and assignment descriptions are available in the `docs`
directory for technical reference and context.

---

## Technologies Used

- Java
- Object-Oriented Programming (OOP)
- Custom instruction set design
- Interpreter and execution engine concepts

---

## Learning Outcomes

- Understanding interpreter architecture
- Lexical and syntactic analysis
- CPU and memory simulation
- Low-level instruction execution
- Machine code encoding
