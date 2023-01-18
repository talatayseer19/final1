package jpa.finalproject.tala.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jpa.finalproject.tala.entity.Appointment;
import jpa.finalproject.tala.repository.AppointmentRepository;
import jpa.finalproject.tala.repository.DoctorRepository;
import jpa.finalproject.tala.repository.PatientRepository;
import jpa.finalproject.tala.result.info.Result;

@Service
public class AppointmentService {
	@Autowired
	DoctorRepository docRepository;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	AppointmentRepository appointmentRepository;

	public Result findTotalNumberOfPatientsVisitedADoctor(Integer doctorId) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();

		if (doctorId == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Doctor Id");
			objList.add("You must insert a Doctor Id, so you can know the number of patients who visited them");
			result.setSystemResult(objList);
			return result;
		}

		if (doctorId < 0) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Negative ID");
			objList.add("ID values cannot be negative");
			result.setSystemResult(objList);
			return result;
		}
		boolean docIdFlag = false;
		List<Integer> docIdList = docRepository.findAllDocIdNative();
		for (Integer integer : docIdList) {
			if (doctorId == integer) {
				docIdFlag = true;
				break;
			}
		}
		if (!docIdFlag) {
			result.setStatusCode("1");
			result.setStatusDescription("Error in: Doctor Id");
			objList.add("Doctor ID: " + doctorId + " is not existed");
			result.setSystemResult(objList);
			return result;
		}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add("The number of patients who visited the doctor with id: [" + doctorId + "], Dr."
				+ docRepository.findDocById(doctorId).getName() + ", is: "
				+ appointmentRepository.findTotalNumberOfPatientsVisitedADoctor(doctorId) + " patients.");
		result.setSystemResult(objList);
		return result;
	}

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public Result findHowManyTimesAPatientVisitedADoctor(Integer doctorId, Integer patientId) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		if (doctorId == null || patientId == null) {
			if (doctorId == null && patientId == null) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Empty Doctor Id and Empty Patient Id");
				objList.add("You must enter a Doctor Id and a Patient Id to complete this request");
				result.setSystemResult(objList);
				return result;
			}
			if (doctorId == null) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Empty Doctor Id");
				objList.add("You must enter a Doctor Id to complete this request");
				result.setSystemResult(objList);
				return result;
			}
			if (patientId == null) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Empty Patient Id");
				objList.add("You must enter a Patient Id to complete this request");
				result.setSystemResult(objList);
				return result;
			}
			if (doctorId < 0 || patientId < 0) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Negative IDs");
				objList.add("ID values cannot be negative");
				result.setSystemResult(objList);
				return result;
			}
		}
		boolean docIdFlag = false;
		List<Integer> docIdList = docRepository.findAllDocIdNative();
		for (Integer integer : docIdList) {
			if (doctorId == integer) {
				docIdFlag = true;
				break;
			}
		}
		boolean patientIdFlag = false;
		List<Integer> patientIdList = patientRepository.findAllPatientIdNative();
		for (Integer integer : patientIdList) {
			if (patientId == integer) {
				patientIdFlag = true;
				break;
			}
		}
		if (!docIdFlag || !patientIdFlag) {
			if (!docIdFlag && !patientIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Doctor ID, and Patient ID");
				objList.add("Doctor ID: " + doctorId + " is not existed");
				objList.add("Patient ID: " + patientId + " is not existed");
				result.setSystemResult(objList);
				return result;
			} else if (!docIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Doctor Id");
				objList.add("Doctor ID: " + doctorId + " is not existed");
				result.setSystemResult(objList);
				return result;
			} else if (!patientIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Patient Id");
				objList.add("Patient ID: " + patientId + " is not existed");
				result.setSystemResult(objList);
				return result;
			}
		}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add("The number of times the patient with id: [" + patientId + "], name: ["
				+ patientRepository.findPatientById(patientId).getName() + "] visited the doctor with id: [" + doctorId
				+ "], Dr." + docRepository.findDocById(doctorId).getName() + ", is: "
				+ appointmentRepository.findHowManyTimesAPatientVisitedADoctor(doctorId, patientId) + " times.");
		result.setSystemResult(objList);
		return result;
	}

//@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public Result updateAppointmentVisitStatus(Integer appointmentId, Integer status, String token) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String usernameToken = (String) s.get("username");
		boolean appointmentIdFlag = false;
		if (appointmentId == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Appointment Id");
			objList.add("Insert the Appointment Id to update its status");
			result.setSystemResult(objList);
			return result;
		}
		if (appointmentId < 0) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Negative IDs");
			objList.add("ID values cannot be negative");
			result.setSystemResult(objList);
			return result;
		}
		if (!(docRepository.findIdByUsername(usernameToken) == appointmentRepository
				.findDocIdByUsername(appointmentId))) {
			result.setStatusCode("1");
			result.setStatusDescription(
					"You are not allowed to update the the visit status of the appointment with id: [" + appointmentId
							+ "]");
			objList.add("Because are not the doctor who scheduled this appointment");
			objList.add("You are logged-in as a doctor with username [" + usernameToken + "]; and your ID: ["
					+ docRepository.findIdByUsername(usernameToken) + "]");
			objList.add("And this appointment with id: [" + appointmentId + "] was scheduled for doctor-ID: ["
					+ appointmentRepository.findDocIdByUsername(appointmentId) + "] and patient-ID: ["
					+ appointmentRepository.findPIdByUsername(appointmentId) + "]");
			result.setSystemResult(objList);
			return result;
		}
		List<Integer> appointmentIdList = appointmentRepository.findAllAppointmentIdNative();
		for (Integer integer : appointmentIdList) {
			if (appointmentId == integer) {
				appointmentIdFlag = true;
				break;
			}
		}
		if (!appointmentIdFlag) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Appointment Id");
			objList.add("Appointment with id " + appointmentId
					+ " is not existed, you cannot update a status of a non-existent appointment!");
			result.setSystemResult(objList);
			return result;
		}
		if (status == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Status");
			objList.add(
					"Enter either 0 as in 'Status 0: patient has visited the appointment' or 1 as in 'Status 1: patient has not visited the appointment'");
			result.setSystemResult(objList);
			return result;
		} else if (!(status == 0 || status == 1)) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Value of the Status can only be 0 or 1");
			objList.add(
					"Enter either 0 as in 'Status 0: patient has visited the appointment' or 1 as in 'Status 1: patient has not visited the appointment'");
			result.setSystemResult(objList);
			return result;
		}
		String str = "";
		if (status == 0) {
			str = "Status 0: patient has visited the appointment";
		} else {
			str = "Status 1: patient has not visited the appointment";
		}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		Appointment app = appointmentRepository.findAppointmentData(appointmentId);
		app.setVisitStatus(status);
		appointmentRepository.save(app);
		objList.add("You have successfully updated the status of the appointment with id: " + appointmentId + " to: "
				+ status);
		objList.add(str);
		objList.add("Summary of the appointment info:");
		Appointment a = appointmentRepository.findById(appointmentId).orElse(null);
		objList.add("Appointment Id: "+a.getId());
		objList.add("Appointment Date: "+a.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		objList.add("Appointment Time: "+a.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"))+", Appointment End Time: "+a.getAppointmentEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
		objList.add("Doctor info: ");
		objList.add("Doctor name: "+a.getDoctor().getName()+", Doctor Id: "+a.getDoctor().getId());
		objList.add("Doctor Specialty: "+ a.getDoctor().getSpecialty()+", Doctor Phone Number: "+a.getDoctor().getPhoneNumber());
		objList.add("Patient info: ");
		objList.add("Patient name: "+a.getPatient().getName()+", Patient Id: "+a.getPatient().getId());
		objList.add("Patient Phone Number: "+a.getPatient().getPhoneNumber());
		result.setSystemResult(objList);
		return result;
	}

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public Result cancelAppointment(Integer appointmentId, String token) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		boolean appointmentIdFlag = false;
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String usernameToken = (String) s.get("username");
		String userType = (String) s.get("userType");
		if (appointmentId == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Appointment Id");
			objList.add("Insert the Appointment Id to cancel the associated appointment");
			result.setSystemResult(objList);
			return result;
		}
		if (appointmentId < 0) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Negative IDs");
			objList.add("ID values cannot be negative");
			result.setSystemResult(objList);
			return result;
		}
		List<Integer> appointmentIdList = appointmentRepository.findAllAppointmentIdNative();
		for (Integer integer : appointmentIdList) {
			if (appointmentId == integer) {
				appointmentIdFlag = true;
				break;
			}
		}
		if (!appointmentIdFlag) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Appointment Id");
			objList.add("Appointment with id " + appointmentId
					+ " is not existed, you cannot cancel a non-existent appointment!");
			result.setSystemResult(objList);
			return result;
		}
		if (userType.equalsIgnoreCase("p") || userType.equalsIgnoreCase("patient")) {
			if (!(patientRepository.findPIdByUsername(usernameToken) == appointmentRepository
					.findPIdByUsername(appointmentId))) {
				result.setStatusCode("1");
				result.setStatusDescription(
						"Appointment has not been cancelled. You are not allowed to cancel an appointment that does not include you!");
				objList.add("Because are not the patient who scheduled this appointment");
				objList.add(
						"You can only cancel your own appointments, so please enter the appointments correct id to cancel it");
				objList.add("You are logged-in as a patient with username [" + usernameToken + "]; and your ID: ["
						+ patientRepository.findPIdByUsername(usernameToken) + "]");
				objList.add("And this appointment with id: [" + appointmentId + "] was scheduled for doctor-ID: ["
						+ appointmentRepository.findDocIdByUsername(appointmentId) + "] and patient-ID: ["
						+ appointmentRepository.findPIdByUsername(appointmentId) + "]");
				objList.add("so you cannot cancel this appointment");
				result.setSystemResult(objList);
				return result;
			}
		} else {
			if (!(docRepository.findIdByUsername(usernameToken) == appointmentRepository
					.findDocIdByUsername(appointmentId))) {
				result.setStatusCode("1");
				result.setStatusDescription(
						"Appointment has not been cancelled. You are not allowed to cancel an appointment that does not include you!");
				objList.add("Because are not the doctor who scheduled this appointment");
				objList.add(
						"You can only cancel your own appointments, so please enter the appointments correct id to cancel it");
				objList.add("You are logged-in as a doctor with username [" + usernameToken + "]; and your ID: ["
						+ docRepository.findIdByUsername(usernameToken) + "]");
				objList.add("And this appointment with id: [" + appointmentId + "] was scheduled for doctor-ID: ["
						+ appointmentRepository.findDocIdByUsername(appointmentId) + "] and patient-ID: ["
						+ appointmentRepository.findPIdByUsername(appointmentId) + "]");
				objList.add("so you cannot cancel this appointment");
				result.setSystemResult(objList);
				return result;
			}
		}
		Appointment app = appointmentRepository.findAppointmentData(appointmentId);
		appointmentRepository.deleteById(appointmentId);
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add("Appointment with id [" + appointmentId + "] on date: [" + app.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
				+ "] at time: [" + app.getAppointmentTime().toString() + "]");
		objList.add("for doctor: '" + app.getDoctor().getName() + "' and patient: '" + app.getPatient().getName()
				+ "' has been successfully cancelled");
		result.setSystemResult(objList);
		return result;
	}

	// **********************************************************
	public Result showBookedTimelineForCertainDate(LocalDate date, String token) {
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String usernameToken = (String) s.get("username");
		Integer doctorId = docRepository.findIdByUsername(usernameToken);
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		if (date == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Appointment date cannot be empty");
			objList.add("Please select a date to show all booked time-lines");
			result.setSystemResult(objList);
			return result;
		}
		List<ArrayList<Object>> list = appointmentRepository.findBookedTimeLine(date, doctorId);
		if (list.isEmpty()) {
			objList.add("All times from 08:00 until 17:00 are available for date [" + date + "] for doctor '"
					+ docRepository.findNameById(doctorId) + "', username: " + usernameToken);
		} else {
			objList.add("The booked timeline for doctor " + docRepository.findNameById(doctorId) + ", username: '"
					+ usernameToken + "' at date: [" + date + "]:");
			for (ArrayList<Object> arrayList : list) {
				objList.add("Time: " + arrayList.get(0));
				objList.add("Patient Id: " + arrayList.get(1));
				objList.add("Patient Name: " + arrayList.get(2));
				objList.add("Doctor ID: " + arrayList.get(3));
				objList.add("\n");
			}
		}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		result.setSystemResult(objList);
		return result;
	}

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public Result showAvailableTimesForCertainDoctorAndCertainDate(Integer doctorId, LocalDate date) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		if (date == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: date cannot be empty");
			objList.add("Please select a date to show all available times");
			result.setSystemResult(objList);
			return result;
		}
		if (date.isBefore(LocalDate.now())) {
			result.setStatusCode("1");
			result.setStatusDescription(
					"Error: Date is not available, you inserted a date earlier than the actual current one!; "
							+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			objList.add(
					"Please enter either the current date or any date after to show available time for the doctor in it, not before!");
			result.setSystemResult(objList);
			return result;
		}
		objList.add("Available Times at date: [" + date + "] for doctor: [" + docRepository.findNameById(doctorId)
				+ "] are:");
		List<Object> times = appointmentRepository.findAllWorkHours(date, doctorId);
		if(times.isEmpty()) {
			objList.add("There are no available times for Dr."+docRepository.findNameById(doctorId));
			objList.add("Doctor "+docRepository.findNameById(doctorId)+" is busy today");
		}else {
		for (Object objects : times) {
			objList.add("time: " + objects);
		}}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		result.setSystemResult(objList);
		return result;
	}

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public Result showBookedTimelineForCertainDoctorAndCertainDate(Integer doctorId, LocalDate date) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		if (date == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Appointment date cannot be empty");
			objList.add("Please select a date to show all booked time-lines");
			result.setSystemResult(objList);
			return result;
		}
		if (doctorId != null) {
			if (doctorId < 0) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Negative IDs");
				objList.add("ID values cannot be negative");
				result.setSystemResult(objList);
				return result;
			}
		} else {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Doctor Id");
			objList.add("You must enter a Doctor Id to complete this request");
			result.setSystemResult(objList);
			return result;
		}

		boolean docIdFlag = false;
		List<Integer> docIdList = docRepository.findAllDocIdNative();
		for (Integer integer : docIdList) {
			if (doctorId == integer) {
				docIdFlag = true;
				break;
			}
		}
		if (!docIdFlag) {
			result.setStatusCode("1");
			result.setStatusDescription("Error in: Doctor Id");
			objList.add("Doctor ID: " + doctorId + " is not existed");
			result.setSystemResult(objList);
			return result;
		}
		List<ArrayList<Object>> list = appointmentRepository.findBookedTimeLineForCertainDoctor(doctorId, date);
		if (list.isEmpty()) {
			objList.add("All times from 08:00 until 17:00 are available for date [" + date + "]");
		} else {
			objList.add("All booked timelines at date [" + date + "]" + " for Dr.'"
					+ docRepository.findNameById(doctorId) + "' are:");
			for (ArrayList<Object> arrayList : list) {
				objList.add("Time: " + arrayList.get(0));
				objList.add("Patient Id: " + arrayList.get(1));
				objList.add("Patient Name: " + arrayList.get(2));
				objList.add("Doctor ID: " + arrayList.get(3));
				objList.add("\n");
			}
		}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		result.setSystemResult(objList);
		return result;
	}

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public Result showBookedTimelineForToday(String token) {
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String usernameToken = (String) s.get("username");
		Integer doctorId = docRepository.findIdByUsername(usernameToken);
		Result result = new Result();
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		List<Object> objList = new ArrayList<>();
		List<ArrayList<Object>> list = appointmentRepository.findBookedTimeLine(LocalDate.now(), doctorId);
		if (list.isEmpty()) {
			objList.add("All times from 08:00 to 17:00 are available for today for doctor '"
					+ docRepository.findNameById(doctorId) + "', username: " + usernameToken);
		} else {
			objList.add("The booked timeline for doctor " + docRepository.findNameById(doctorId) + ", " + usernameToken
					+ " for today:");
			for (ArrayList<Object> arrayList : list) {
				objList.add("Time: " + arrayList.get(0));
				objList.add("Patient Id: " + arrayList.get(1));
				objList.add("Patient Name: " + arrayList.get(2));
				objList.add("Doctor ID: " + arrayList.get(3));
				objList.add("\n");
			}
		}
		result.setSystemResult(objList);
		return result;
	}

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public Result showAllAppointments(LocalDate date) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		result.setStatusCode("0");
		if (date == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: date cannot be empty");
			objList.add("Please select a date to show all booked appointments scheduled in it");
			result.setSystemResult(objList);
			return result;
		}
		int i = 1;
		result.setStatusDescription("Successful");
		List <Appointment> appList = appointmentRepository.findAllAppointmentsByDate(date);
		if(appList.isEmpty()) {
			objList.add("No appointments have been scheduled yet for date: ["+date+"]");
		}else {
		for (Appointment a : appList) {
			objList.add(i+":");
			objList.add("Appointment Id: "+a.getId());
			objList.add("Date: "+a.getAppointmentDate());
			objList.add("Appointment Time: "+a.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"))+", Appointment End Time: "+a.getAppointmentEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
			objList.add("Doctor name: "+a.getDoctor().getName()+", Doctor Id: "+a.getDoctor().getId()+", Doctor Specialty: "+ a.getDoctor().getSpecialty());
			objList.add("Patient name: "+a.getPatient().getName()+", Patient Id: "+a.getPatient().getId());
			i++;
		}}
		result.setSystemResult(objList);
		return result;
	}

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	public Result showAvailableTimeForCertainDate(LocalDate date, String token) {// All Available Time
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String usernameToken = (String) s.get("username");
		Integer doctorId = docRepository.findIdByUsername(usernameToken);
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		if (date == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: date cannot be empty");
			objList.add("Please select a date to show all available times");
			result.setSystemResult(objList);
			return result;
		}
		if (date.isBefore(LocalDate.now())) {
			result.setStatusCode("1");
			result.setStatusDescription(
					"Error: Date is not available, you inserted a date earlier than the actual current one!; "
							+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			objList.add(
					"Please enter either the current date or any date after to show available appointments in it, not before!");
			result.setSystemResult(objList);
			return result;
		}
		objList.add("Available Times at date: [" + date + "] for doctor username: [" + usernameToken + "] are:");
		List<Object> times = appointmentRepository.findAllWorkHours(date, doctorId);
		if(times.isEmpty()) {
			objList.add("There are no available times for Dr."+docRepository.findNameById(docRepository.findIdByUsername(usernameToken)));
			objList.add("Doctor "+docRepository.findNameById(docRepository.findIdByUsername(usernameToken))+" is busy in date: "+date);
		}else {
		for (Object objects : times) {
			objList.add("time: " + objects);
		}}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		result.setSystemResult(objList);
		return result;
	}

//	@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@!!!!!!!
	// See All Available Time---------------------------------------------
	public Result showAvailableTimeForToday(String token) {// All Available Time
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String usernameToken = (String) s.get("username");
		Integer doctorId = docRepository.findIdByUsername(usernameToken);
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		objList.add("The Available Times for today for doctor username: [" + usernameToken + "] are:");
		List<Object> times = appointmentRepository.findAllWorkHours(LocalDate.now(), doctorId);
		if(times.isEmpty()) {
			objList.add("There are no available times for Dr."+docRepository.findNameById(docRepository.findIdByUsername(usernameToken)));
			objList.add("Doctor "+docRepository.findNameById(docRepository.findIdByUsername(usernameToken))+" is busy today");
		}else {
		for (Object objects : times) {
			objList.add("time: " + objects);
		}}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		result.setSystemResult(objList);
		return result;
	}

	// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@!!!!!!!
	// Create Appointment------------------------------------------------
	public Result createAnAppointment(Integer doctorId, Integer patientId, LocalDate date, LocalTime time,
			String token) {
		Appointment appointment = new Appointment();
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		List<String> note = new ArrayList();
		if (doctorId == null || patientId == null) {
			if (doctorId == null && patientId == null) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Empty Doctor Id and Empty Patient Id");
				objList.add("You must enter a Doctor Id and a Patient Id to create an appointment");
				result.setSystemResult(objList);
				return result;
			}
			if (doctorId == null) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Empty Doctor Id");
				objList.add("You cannot book an appointment with empty Doctor Id, you have to choose a Doctor");
				result.setSystemResult(objList);
				return result;
			}
			if (patientId == null) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Empty Patient Id");
				objList.add("You cannot book an appointment with empty Patient Id");
				result.setSystemResult(objList);
				return result;
			}
		}
		if (doctorId < 0 || patientId < 0) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Negative IDs");
			objList.add("ID values cannot be negative");
			result.setSystemResult(objList);
			return result;
		}
		if (date == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Appointment date cannot be empty");
			objList.add("Please select the date of the appointment you want to book");
			result.setSystemResult(objList);
			return result;
		}
		if (time == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Appointment time cannot be empty");
			objList.add("Check available times to choose the a free available time");
			result.setSystemResult(objList);
			return result;
		}
		boolean docIdFlag = false;
		List<Integer> docIdList = docRepository.findAllDocIdNative();
		for (Integer integer : docIdList) {
			if (doctorId == integer) {
				docIdFlag = true;
				break;
			}
		}
		boolean patientIdFlag = false;
		List<Integer> patientIdList = patientRepository.findAllPatientIdNative();
		for (Integer integer : patientIdList) {
			if (patientId == integer) {
				patientIdFlag = true;
				break;
			}
		}
		if (!docIdFlag || !patientIdFlag) {
			if (!docIdFlag && !patientIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Doctor ID, and Patient ID");
				objList.add("Doctor ID: " + doctorId + " is not existed");
				objList.add("Patient ID: " + patientId + " is not existed");
				result.setSystemResult(objList);
				return result;
			} else if (!docIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Doctor Id");
				objList.add("Doctor ID: " + doctorId + " is not existed");
				result.setSystemResult(objList);
				return result;
			} else if (!patientIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Patient Id");
				objList.add("Patient ID: " + patientId + " is not existed");
				result.setSystemResult(objList);
				return result;
			}
		}
		if (date.isBefore(LocalDate.now())) {
			result.setStatusCode("1");
			result.setStatusDescription(
					"Error: Date is not available, you cannot book an appointment in a date earlier than the actual current one!");
			objList.add("Please enter either the current date or any date after to book an appointment, not before!");
			objList.add(
					"for example, see available times for the current day for the doctor you chose to schedule appointment with: "
							+ LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			objList.add(appointmentRepository.findAllWorkHours(LocalDate.now(), doctorId));
			result.setSystemResult(objList);
			return result;
		}
		LocalDateTime ldt = LocalDateTime.of(date, time);
		if (ldt.isBefore(LocalDateTime.now())) {
			result.setStatusCode("1");
			result.setStatusDescription(
					"Error: You inserted the date of  today at a time earlier than now! Time is not available, you cannot book an appointment at a time earlier than the actual current time; "
							+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
			objList.add("Please enter a valid time");
			result.setSystemResult(objList);
			return result;
		}
		if (time.isAfter(LocalTime.of(16, 0)) || time.isBefore(LocalTime.of(8, 0))) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Check the time");
			objList.add("The Appointment Time: " + time + " is not a work hour");
			objList.add("The Clinic Working hours are: 08:00 – 17:00; so at such time; [" + time
					+ "], the clinic is closed");
			result.setSystemResult(objList);
			return result;
		}
		if (time.getMinute() != 0) {
			note.add("Note: you entered the time as: [" + time + "] but the appointment time will be at: ["
					+ time.getHour() + ":00]");
			note.add("as appointment hours are only as follows: ");
			note.add(appointmentRepository.findAllWorkHours().toString());
			note.add("Also, you can always cancel the appointment and reschedule another one.");
		}
		// '''''''''''''''''''''''''''''
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String usernameToken = (String) s.get("username");
		String userType = (String) s.get("userType");
		if (userType.equalsIgnoreCase("p") || userType.equalsIgnoreCase("patient")) {
			Integer patientIdUser = patientRepository.findPIdByUsername(usernameToken);
			if (patientIdUser != patientId) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: You as a patient cannot book an appointment for other patient");
				objList.add("Please enter your own Id if you wish to schedule an appointment");
				result.setSystemResult(objList);
				return result;
			}
		} else {
			Integer doctorIdUser = docRepository.findIdByUsername(usernameToken);
			if (doctorIdUser != doctorId) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: You as a doctor cannot book an appointment for other doctor");
				objList.add("Please enter your own Id if you wish to schedule an appointment");
				result.setSystemResult(objList);
				return result;
			}
		}
		// ''''''''''''''''''''''''''''''
		time = LocalTime.of(time.getHour(), 0);
		boolean docIdFlag0 = false;
		List<Integer> idOfDoctor = appointmentRepository.findDoctorIdInCertainDateTime(date, time);
		for (Integer integer : idOfDoctor) {
			if (doctorId == integer) {
				docIdFlag0 = true;
				break;
			}
		}
		boolean patientIdFlag0 = false;
		List<Integer> idOfPatient = appointmentRepository.findPatientIdInCertainDateTime(date, time);
		for (Integer integer : idOfPatient) {
			if (patientId == integer) {
				patientIdFlag0 = true;
				break;
			}
		}
		if (docIdFlag0 || patientIdFlag0) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Your Appointment has not been scheduled");
			if (docIdFlag0 && patientIdFlag0) {
				result.setStatusDescription("Error: Appointment is already scheduled");
				objList.add("The Appointment for doctor " + docRepository.findNameById(doctorId) + ", id: [" + doctorId
						+ "] with patient " + patientRepository.findPatientNameById(patientId) + ", id [" + patientId
						+ "] at Time [" + time + "] is already booked and confirmed");
				objList.add(
						"If you wish to schedule another appointment, please check available appointment times and try scheduling at an available time");
				result.setSystemResult(objList);
				return result;
			} else if (docIdFlag0) {
				objList.add("The doctor with id [" + doctorId + "] is not available for Appointment at Time [" + time
						+ "]");
				objList.add("Dr. " + docRepository.findNameById(doctorId)
						+ " has another appointment for other patient at this time, please try scheduling at another time");
				result.setSystemResult(objList);
				return result;
			} else if (patientIdFlag0) {
				objList.add("The patient with id [" + patientId + "] is not available for Appointment at Time [" + time
						+ "]");
				objList.add("Patient '" + patientRepository.findPatientNameById(patientId)
						+ "' has another appointment with other doctor at this time, please try scheduling at another time");
				result.setSystemResult(objList);
				return result;
			}
		}
		appointment.setAppointmentDate(date);
		appointment.setAppointmentTime(time);
		appointment.setAppointmentEndTime(time.plusHours(1));
		appointment.setDoctor(docRepository.findDocById(doctorId));
		appointment.setPatient(patientRepository.findPatientById(patientId));
		appointment.setVisitStatus(0);
		appointmentRepository.save(appointment);
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add("An appointment has been scheduled at time: [" + time.format(DateTimeFormatter.ofPattern("hh:mm"))
				+ "] on date: [" + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "]");
		objList.add("Your appointment has been successfully confirmed, Appointment Id: " + appointment.getId());
		objList.add("For the patient with Id: " + patientId + "; name: " + appointment.getPatient().getName());
		objList.add("With the doctor with Id: " + doctorId + "; Dr." + appointment.getDoctor().getName());
		objList.add("Note: The end time of the appointment will be at: " + appointment.getAppointmentEndTime()
				+ " as each appointment has a 1 hour duration");
		objList.add(
				"As a doctor or a patient, if you wish to reschedule, you can cancel the appointment and re-create an appointment");
		objList.add(note);
		objList.add("Summary of scheduled appointment:");
		Appointment a =appointmentRepository.viewScheduledAppointmentData(doctorId, patientId, time, date);
		objList.add("Appointment Id: "+a.getId());
		objList.add("Appointment Date: "+a.getAppointmentDate());
		objList.add("Appointment Time: "+a.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"))+", Appointment End Time: "+a.getAppointmentEndTime().format(DateTimeFormatter.ofPattern("HH:mm")));
		objList.add("Doctor info: ");
		objList.add("Doctor name: "+a.getDoctor().getName()+", Doctor Id: "+a.getDoctor().getId());
		objList.add("Doctor Specialty: "+ a.getDoctor().getSpecialty()+", Doctor Phone Number: "+a.getDoctor().getPhoneNumber());
		objList.add("Patient info: ");
		objList.add("Patient name: "+a.getPatient().getName()+", Patient Id: "+a.getPatient().getId());
		objList.add("Patient Phone Number: "+a.getPatient().getPhoneNumber());
		objList.add("Note: please make sure to memorize the appointment Id, because cancelling an appointment requires entering the appointment id.");
		result.setSystemResult(objList);
		return result;
	}
}
