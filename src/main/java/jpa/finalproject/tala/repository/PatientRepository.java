package jpa.finalproject.tala.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jpa.finalproject.tala.entity.Doctor;
import jpa.finalproject.tala.entity.Patient;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
	@Query(nativeQuery = true, value = "SELECT id FROM patient")
	List<Integer> findAllPatientIdNative();

	@Query(nativeQuery = true, value = "select username from patient")
	List<String> findAllPatientUsernameNative();

	@Query(nativeQuery = true, value = "select gender from patient where id=:id")
	String getPatientGenderById(Integer id);

	@Query(nativeQuery = true, value = "select username from patient where id=:id")
	String findPatientUsernameById(Integer id);
	
	@Query(nativeQuery = true, value = "select password from patient where id=:id")
	String findPasswordById(Integer id);
	
	@Query(nativeQuery = true, value = "select phone_number from patient where id=:id")
	String findPhoneNumberById(Integer id);
	
	@Query(nativeQuery = true, value = "select age from patient where id=:id")
	Integer findAgeById(Integer id);
	
	@Query(nativeQuery = true, value = "select name from patient where id=:id")
	String findPatientNameById(Integer id);

	@Query(nativeQuery = true, value = "select name from patient")
	List<String> findAllPatientNameNative();

	@Query(nativeQuery = true, value = "select * from patient where id=:id")
	Patient findPatientById(Integer id);

	@Query(nativeQuery = true, value = "select id, name from patient")
	List<ArrayList<Object>> showListOfAllPatients();

	@Query(nativeQuery = true, value = "SELECT ID,USERNAME,NAME,PHONE_NUMBER,AGE,GENDER FROM PATIENT WHERE ID=:id")
	List<ArrayList<Object>> showProfileOfPatient(Integer id);

	@Query(nativeQuery = true, value = "select appointment.*,doctor.name as 'Doctor Name', patient.name as 'Patient Name' from appointment JOIN doctor JOIN patient on(doctor_id = doctor.id and patient_id = patient.id) where patient_id =:id and appointment_date >=:startDate AND appointment_date <=:endDate order by appointment_date,appointment_time")
	List<ArrayList<Object>> patientReport(Integer id, LocalDate startDate, LocalDate endDate);
	
	@Query(nativeQuery = true, value = "SELECT * FROM patient WHERE username=:username")
	Patient findPatientByUsername(String username);
	
	@Query(nativeQuery = true, value = " select id from patient where username =:username")
	Integer findPIdByUsername(String username);
}

