package jpa.finalproject.tala.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jpa.finalproject.tala.entity.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
	@Query(nativeQuery = true, value = "SELECT id FROM doctor")
	List<Integer> findAllDocIdNative();

	@Query(nativeQuery = true, value = "SELECT national_id FROM doctor")
	List<String> findAllDocNatIdNative();

	@Query(nativeQuery = true, value = "select username from doctor")
	List<String> findAllDocUsernameNative();

	@Query(nativeQuery = true, value = "select national_id from doctor where id=:id")
	String findNatIdById(Integer id);

	@Query(nativeQuery = true, value = "select username from doctor where id=:id")
	String findUsernameById(Integer id);
	
	@Query(nativeQuery = true, value = "select password from doctor where id=:id")
	String findPasswordById(Integer id);
	
	@Query(nativeQuery = true, value = "select phone_number from doctor where id=:id")
	String findPhoneNumberById(Integer id);

	@Query(nativeQuery = true, value = "select name from doctor")
	List<String> findAllDocNameNative();

	@Query(nativeQuery = true, value = "select name from doctor where id=:id")
	String findNameById(Integer id);

	@Query(nativeQuery = true, value = "select specialty from doctor where id=:id")
	String findSpecialtyById(Integer id);

	@Query(nativeQuery = true, value = "select * from doctor where id=:id")
	Doctor findDocById(Integer id);

	@Query(nativeQuery = true, value = "select id, name, specialty from doctor")
	List<ArrayList<Object>> showListOfAllDoctors();

	@Query(nativeQuery = true, value = "select id from doctor EXCEPT (select doctor_id from appointment where appointment_date=:date and appointment_time=:time) order by id")
	List<Integer> findAllAvailableDocId(LocalDate date, LocalTime time);

	@Query(nativeQuery = true, value = "select appointment.*,doctor.name as 'Doctor Name', patient.name as 'Patient Name' from appointment JOIN doctor JOIN patient on(doctor_id = doctor.id and patient_id = patient.id) where doctor_id =:id and appointment_date >=:startDate AND appointment_date <=:endDate order by appointment_date, appointment_time")
	List<ArrayList<Object>> doctorReport(Integer id, LocalDate startDate, LocalDate endDate);
	
	@Query(nativeQuery = true, value = "SELECT * FROM doctor WHERE username=:username")
	Doctor findDoctorByUsername(String username);
	
	@Query(nativeQuery = true, value = " select id from doctor where username =:username")
	Integer findIdByUsername(String username);
}
