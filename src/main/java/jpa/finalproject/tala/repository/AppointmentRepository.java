package jpa.finalproject.tala.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import jpa.finalproject.tala.entity.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
	
	@Query(nativeQuery = true, value = "SELECT appointment_id from appointment")
	List<Integer> findAllAppointmentIdNative();
	
	@Query(nativeQuery = true, value = "SELECT * from appointment where appointment_id=:id")
	Appointment findAppointmentData(Integer id);
	
	@Query(nativeQuery = true, value = "select * from appointment where doctor_id =:drId and patient_id =:pId and appointment_time =:time and appointment_date=:date")
	Appointment viewScheduledAppointmentData(Integer drId, Integer pId, LocalTime time, LocalDate date);
	
	@Query(nativeQuery = true, value = "SELECT appointment_time from appointment")
	List<LocalTime> findAllBookedAppointmentTimes();

	@Query(nativeQuery = true, value = "SELECT appointment_time FROM appointment where appointment_date=:date")
	List<Object> findAllBookedTimesInCertainDate(LocalDate date);
	
	@Query(nativeQuery = true, value ="select doctor_id from appointment where appointment_time =:time and appointment_date=:date")
	List<Integer> findDoctorIdInCertainDateTime(LocalDate date, LocalTime time);

	@Query(nativeQuery = true, value ="select patient_id from appointment where appointment_time =:time and appointment_date=:date")
	List<Integer> findPatientIdInCertainDateTime(LocalDate date, LocalTime time);

	@Query(nativeQuery = true, value = "SELECT * from work_hours")
	List<Object> findAllWorkHours();

	@Query(nativeQuery = true, value = "SELECT hours FROM work_hours EXCEPT (SELECT appointment_time FROM appointment where appointment_date=:date and doctor_id=:id)")
	List<Object> findAllWorkHours(LocalDate date, Integer id);

	@Query(nativeQuery = true, value = "SELECT appointment.appointment_time, appointment.patient_id, patient.name,appointment.doctor_id FROM appointment INNER JOIN patient ON appointment.patient_id=patient.id where appointment_date=:date and doctor_id =:id order by appointment_time")
	List<ArrayList<Object>> findBookedTimeLine(LocalDate date, Integer id);
	
	@Query(nativeQuery = true, value = "SELECT appointment.appointment_time, appointment.patient_id, patient.name,appointment.doctor_id FROM appointment INNER JOIN patient ON appointment.patient_id=patient.id where appointment_date=:date and doctor_id=:id order by appointment_time")
	List<ArrayList<Object>> findBookedTimeLineForCertainDoctor(Integer id, LocalDate date);
	
	@Query(nativeQuery = true, value="select count(patient_id) from appointment where doctor_id=:doctorId and  patient_id=:patientId and visit_status=0")
	Integer findHowManyTimesAPatientVisitedADoctor(Integer doctorId,Integer patientId);
	
	@Query(nativeQuery = true, value="select count( distinct patient_id) from appointment where doctor_id=:doctorId")
	Integer findTotalNumberOfPatientsVisitedADoctor(Integer doctorId);
	
	@Query(nativeQuery = true, value = "select doctor_id from appointment where appointment_id=:id ")
	Integer findDocIdByUsername(Integer id);
	
	@Query(nativeQuery = true, value = "select patient_id from appointment where appointment_id=:id ")
	Integer findPIdByUsername(Integer id);
	
	@Query(nativeQuery = true, value = "select * from appointment where appointment_date=:date order by appointment_time")
	List<Appointment> findAllAppointmentsByDate(LocalDate date);
}
