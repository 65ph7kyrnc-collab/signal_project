# Project Part 3 Completion Notes

## Overview

This project phase implements a patient storage and alert system.

The implemented system:
- reads simulator output files;
- stores patient records;
- retrieves patient records by time range;
- evaluates medical alert rules;
- stores generated alerts;
- includes JUnit tests;
- includes Maven test and JaCoCo coverage configuration.

## Changed files

### pom.xml
Added:
- JUnit 5 API
- JUnit 5 engine
- Maven Surefire plugin
- JaCoCo Maven plugin
- Maven JAR plugin
- Maven Exec plugin
- Java-WebSocket dependency

### Main.java
Added a dispatcher that can run either:
- HealthDataSimulator
- DataStorage

### DataReader.java
Defined the interface for reading data into DataStorage.

### FileDataReader.java
Added a concrete DataReader implementation for simulator file output.

### DataStorage.java
Completed central storage logic:
- stores patients by ID;
- adds patient records;
- retrieves records by patient and time range;
- returns all patients;
- can run from the command line.

### Patient.java
Implemented:
- getRecords(startTime, endTime);
- getAllRecords();
- getPatientId().

### PatientRecord.java
Stores one measurement:
- patient ID;
- measurement value;
- record type;
- timestamp.

### Alert.java
Stores one generated alert:
- patient ID;
- condition;
- timestamp.

### AlertGenerator.java
Implemented:
- critical blood pressure alerts;
- blood pressure trend alerts;
- low blood oxygen saturation alerts;
- rapid oxygen drop alerts;
- hypotensive hypoxemia alerts;
- ECG peak alerts;
- manual triggered alerts.

## Important assumptions

### ECG alert assumption
The assignment requires ECG abnormal-peak detection using a sliding window / moving average, but does not provide exact thresholds.

Assumption used:
- use the previous 5 ECG values as the moving-average window;
- compare absolute ECG values;
- trigger alert if current ECG value is more than 2 times the previous moving average;
- and at least 0.5 higher than the moving average.

### File format assumption
The file reader assumes simulator output lines follow this format:

Patient ID: 1, Timestamp: 1714376789050, Label: Saturation, Data: 91%

Manual alert values are converted as:
- triggered = 1.0
- resolved = 0.0
- untriggered = 0.0

Invalid lines are ignored.