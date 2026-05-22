# Signal Project

This project is part of the Software Engineering course at Maastricht University.  
The goal of the project is to simulate, process, store, and evaluate patient health data using clean object-oriented design, testing, design patterns, and real-time data processing.

The system started as a basic health data simulator and has been extended over several project phases. It now supports file-based data processing, alert generation, design pattern-based alert handling, and real-time WebSocket-based data input.

---

## Project Overview

The project simulates patient health signals such as:

- ECG / heart rate data
- Blood oxygen saturation
- Blood pressure values
- Manual alert states

The generated data can be written to different outputs and later read into the system for analysis. The system stores patient data in `DataStorage`, organizes it by patient, and evaluates it through the alert system.

The latest phase adds real-time processing through WebSockets. This means the system can now receive patient data continuously from a running WebSocket server instead of only reading data from static files.

---

## Main Features

### Health Data Simulation

The simulator can generate different kinds of patient health data and send it to several output targets.

Supported output strategies include:

- Console output
- File output
- TCP output
- WebSocket output

The WebSocket output starts a WebSocket server and broadcasts generated patient data to connected clients.

---

### Data Management

The data management package is responsible for reading, parsing, storing, and organizing patient data.

Important classes include:

- `DataStorage`
- `Patient`
- `PatientRecord`
- `DataReader`
- `FileDataReader`
- `WebSocketDataReader`
- `PatientDataParser`
- `PatientDataMessage`

`DataStorage` keeps track of all patients and their records. Data can be loaded either from files or from a WebSocket stream.

---

### Real-Time WebSocket Processing

Project Part 5 adds support for real-time patient data processing.

The new WebSocket reader connects to a WebSocket server, receives live patient messages, parses them, and stores them in `DataStorage`.

The expected message format is:

```text
patientId,timestamp,label,value

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

##Project Member(s)
- Student ID: i6443234