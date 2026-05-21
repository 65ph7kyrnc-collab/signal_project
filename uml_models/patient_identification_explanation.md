The Patient Identification System is responsible for linking incoming simulator data to the correct hospital patient records. Even though the original project mainly uses simple patient IDs, I expanded the design to model how a real hospital system could verify and manage patient identities more securely.

The PatientIdentifier class is responsible for validating incoming patient IDs and matching them against the HospitalDatabase. The HospitalPatient class represents a patient stored in the hospital system and contains information such as the hospital patient ID, name, and medical history. This separation allows the simulator IDs and the hospital IDs to remain independent, which improves flexibility and realism.

I introduced the IdentityManager class to coordinate the overall identification workflow and handle edge cases. Instead of placing all logic directly inside PatientIdentifier, the IdentityManager improves modularity by separating validation from management responsibilities.

The design also includes an IdentityMapping class that stores mappings between simulator IDs and hospital IDs. This makes it easier to extend the system later if multiple external systems need to be connected.

To address anomaly handling, I added the AnomalyHandler and IdentityAnomaly classes. These classes are responsible for logging mismatches, unknown IDs, or corrupted data. In a real hospital environment, detecting identity mismatches is extremely important because assigning patient data to the wrong individual could lead to dangerous medical decisions.

Overall, the subsystem was designed with security, data integrity, and extensibility in mind while still fitting naturally into the existing CHMS architecture.