package jpa.finalproject.tala.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jpa.finalproject.tala.entity.Patient;
import jpa.finalproject.tala.repository.AppointmentRepository;
import jpa.finalproject.tala.repository.DoctorRepository;
import jpa.finalproject.tala.repository.PatientRepository;
import jpa.finalproject.tala.result.info.Result;

@Service
public class PatientService {
	@Autowired
	DoctorRepository docRepository;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	AppointmentRepository appointmentRepository;

	// The Patient Can Show a Report To Check All time-lines in the Dentist
	public Result getPatientReportAsCsvFile(Integer patientId, LocalDate startDate, LocalDate endDate) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		if (patientId == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Patient Id");
			objList.add("Request cannot be completed with empty Patient Id");
			result.setSystemResult(objList);
			return result;
		}
		if (patientId < 0) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Negative IDs");
			objList.add("ID values cannot be negative");
			result.setSystemResult(objList);
			return result;
		}
		if (startDate == null || endDate == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Appointment dates cannot be empty");
			objList.add(
					"To get the summary for all booked timelines between two dates as a csv file; please write the two dates to complete this request successfully");
			result.setSystemResult(objList);
			return result;
		}
		if (startDate.isAfter(endDate)) {
			LocalDate temp = startDate;
			startDate = endDate;
			endDate = temp;
		}
		boolean patientIdFlag = false;
		List<Integer> patientIdList = patientRepository.findAllPatientIdNative();
		for (Integer integer : patientIdList) {
			if (patientId == integer) {
				patientIdFlag = true;
				break;
			}
		}
		if (!patientIdFlag) {
			result.setStatusCode("1");
			result.setStatusDescription("Error in: Patient Id");
			objList.add("Patient ID: " + patientId + " is not existed");
			result.setSystemResult(objList);
			return result;
		}
		List<ArrayList<Object>> appointmentRecords = patientRepository.patientReport(patientId, startDate, endDate);
		File file = new File("C:\\Users\\XYZ\\Patient Report.csv");
		try (PrintWriter writer = new PrintWriter(file)) {
			writer.println(
					"Appointment Id, Appointment Date, Appointment Time, End Time, Visit Status, Patient Id, Patient Name, Doctor Id, Doctor Name");
			appointmentRecords.forEach(x -> {
				StringJoiner sj = new StringJoiner(",");
				sj.add(x.get(0) != null ? x.get(0).toString() : "NULL"); // appointment Id
				sj.add(x.get(1) != null ? x.get(1).toString() : "NULL"); // appointment Date
				sj.add(x.get(2) != null ? x.get(2).toString() : "NULL"); // appointment Time
				sj.add(x.get(3) != null ? x.get(3).toString() : "NULL"); // appointment EndTime
				sj.add(x.get(4) != null ? x.get(4).toString() : "NULL"); // visit Status
				sj.add(x.get(6) != null ? x.get(6).toString() : "NULL"); // Patient Id
				sj.add(x.get(8) != null ? x.get(8).toString() : "NULL"); // Patient Name
				sj.add(x.get(5) != null ? x.get(5).toString() : "NULL"); // Doctor Id
				sj.add(x.get(7) != null ? x.get(7).toString() : "NULL"); // Doctor Name
				writer.println(sj.toString());
			});
			writer.flush();
			// writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return file.getAbsolutePath();
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add(file.getAbsolutePath());
		result.setSystemResult(objList);
		return result;
	}

	// find All Available Doctors For Today In Certain Time
	public Result findAllAvailableDoctorsForTodayInCertainTime(LocalTime time) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		List<Integer> docIdlist = docRepository.findAllAvailableDocId(LocalDate.now(), time);
		if (time == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty time");
			objList.add("Time cannot be empty, you must enter the time to know the available doctors at that time");
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
		LocalDateTime ldt = LocalDateTime.of(LocalDate.now(), time);
		if (ldt.isBefore(LocalDateTime.now())) {
			result.setStatusCode("1");
			result.setStatusDescription(
					"Error: Time is not available, you cannot insert a time earlier than the actual current time; "
							+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
			objList.add("Please enter a valid time");
			result.setSystemResult(objList);
			return result;
		}

		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add("Available doctors at time [" + time + "] for today at date [" + LocalDate.now() + "] are:");
		for (Integer integer : docIdlist) {
			objList.add("Doctor Id: " + integer + ", Dr." + docRepository.findNameById(integer) + ", Specialty: "
					+ docRepository.findSpecialtyById(integer));
		}
		result.setSystemResult(objList);
		return result;
	}

	// FIND ALL AVAILABLE DOCTORS--------------------------------------------------
	public Result findAllAvailableDoctors(LocalDate date, LocalTime time) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		List<Integer> docIdlist = docRepository.findAllAvailableDocId(date, time);
		if (date == null || time == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty date or empty time");
			objList.add(
					"Niether date nor time can be empty, you must enter the date and the time to know the available doctors at that time");
			result.setSystemResult(objList);
			return result;
		}
		if (date.isBefore(LocalDate.now())) {
			result.setStatusCode("1");
			result.setStatusDescription(
					"Error: Date is not available, you cannot insert a date earlier than the actual current one!");
			objList.add(
					"Please enter either the current date or any date after to know available doctors at that time");
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
		LocalDateTime ldt = LocalDateTime.of(date, time);
		if (ldt.isBefore(LocalDateTime.now())) {
			result.setStatusCode("1");
			result.setStatusDescription(
					"Error: Time is not available, you cannot insert a time earlier than the actual current time; "
							+ LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
			objList.add("Please enter a valid time");
			result.setSystemResult(objList);
			return result;
		}

		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add("Available doctors at time [" + time + "] on date [" + date + "] are:");
		for (Integer integer : docIdlist) {
			objList.add("Doctor Id: " + integer + ", Dr." + docRepository.findNameById(integer) + ", Specialty: "
					+ docRepository.findSpecialtyById(integer));
		}
		result.setSystemResult(objList);
		return result;
	}

	// LIST OF ALL DOCTORS -------------------------------------------------------
	public Result showListOfAllDoctors() {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		List<ArrayList<Object>> list = new ArrayList<>();
		list = docRepository.showListOfAllDoctors();
		objList.add("Your request has been successfully completed");
		objList.add("List of all doctors; contains the Doctor Id along with the Doctor Name, and their specialty:");
		for (ArrayList<Object> arrayList : list) {
			objList.add("ID: " + arrayList.get(0) + ", Doctor Name: " + arrayList.get(1) + ", Specialty: "
					+ arrayList.get(2));
		}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		result.setSystemResult(objList);
		return result;
	}

	// 1.REGISTER PATIENT-----------------------------------------------------------
	public Result addPatient(Patient patient) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		boolean patientIdFlag = false, patientUsernameFlag = false;
		if (patient.getName() == null || patient.getName().isEmpty() || patient.getName().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty name");
			objList.add("Patient name cannot be empty, you must insert your name");
			result.setSystemResult(objList);
			return result;
		}else if (patient.getName().matches("[0-9]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Name cannot be only digits");
			objList.add("Please enter your name correctly, a name cannot be only numbers!");
			result.setSystemResult(objList);
			return result;
		}else if (!patient.getName().substring(0, 1).matches("[a-zA-Z]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Wrong Name");
			objList.add("Name must start with alphabets, Please enter your name correctly");
			result.setSystemResult(objList);
			return result;
		} else {
			String[] subS = patient.getName().split("\\s+");
			for (int i = 0; i < subS.length; i++) {
				if (!subS[i].matches("[a-zA-Z]+")) {
					result.setStatusCode("1");
					result.setStatusDescription("Error: Wrong Name, name can only have alphabets");
					objList.add("Please enter your name correctly");
					result.setSystemResult(objList);
					return result;
				}
			}
		}  
			boolean patientName = false;
			List<String> patientNames = patientRepository.findAllPatientNameNative();
			for (String string : patientNames) {
				if (patient.getName().equalsIgnoreCase(string)) {
					patientName = true;
					break;
				}
			}
			if (patientName) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Duplicated FULL NAME");
				objList.add("Patient FULL name must be unique");
				result.setSystemResult(objList);
				return result;
			}
			patient.setName(capitalizeWord(patient.getName()));
		if (patient.getUsername() == null || patient.getUsername().isEmpty() || patient.getUsername().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty username");
			objList.add("Patient username cannot be empty, you must insert a username");
			result.setSystemResult(objList);
			return result;
		} else if (patient.getUsername().matches("[0-9]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Username cannot be only digits");
			objList.add("Patient Username cannot be only numbers! Please try again");
			result.setSystemResult(objList);
			return result;
		} else if (!patient.getUsername().substring(0, 1).matches("[a-zA-Z]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Wrong Username");
			objList.add("Username cannot start with numbers or symbols, Please enter your username correctly");
			result.setSystemResult(objList);
			return result;
		} else {
			String str = patient.getUsername();
			for (int i = 0; i <str.length();i++) {
				if(str.substring(i, i+1).equals(" "))
				{
					result.setStatusCode("1");
					result.setStatusDescription("Error: Wrong Username");
					objList.add("Username cannot contain any spaces, Please enter your username correctly");
					result.setSystemResult(objList);
					return result;
				}
				if(!str.substring(i, i+1).matches("[a-zA-Z]+")&&!str.substring(i, i+1).matches("[0-9]+")) {
					result.setStatusCode("1");
					result.setStatusDescription("Error: Wrong Username");
					objList.add("Username can only contain numbers and alphabets");
					result.setSystemResult(objList);
					return result;
				}
			}
		}
	
			patient.setUsername(patient.getUsername().toLowerCase());
	
		if (patient.getPassword() == null || patient.getPassword().isEmpty() || patient.getPassword().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty password");
			objList.add("The password cannot be empty, you must insert a password");
			result.setSystemResult(objList);
			return result;
		}

		if (patient.getId() != null) {
			if (patient.getId() < 0) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Negative Id");
				objList.add("Patient id cannot be zero or negative");
				result.setSystemResult(objList);
				return result;
			}
		}
		List<Integer> patientIdList = patientRepository.findAllPatientIdNative();
		for (Integer integer : patientIdList) {
			if (patient.getId() == integer) {
				patientIdFlag = true;
				break;
			}
		}

		List<String> patientUsernameList = patientRepository.findAllPatientUsernameNative();
		patientUsernameList.addAll(docRepository.findAllDocUsernameNative());
		for (String string : patientUsernameList) {
			if (patient.getUsername().equalsIgnoreCase(string)) {
				patientUsernameFlag = true;
				break;
			}
		}
		String note = "The username you entered may already have been taken by a patient or a doctor";
		if (patientUsernameFlag || patientIdFlag) {
			if (patientIdFlag && patientUsernameFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Patient has not been added, Error in: Patient Id, and Patient Username");
				objList.add("Patient id must be unique, Patient username must be globally unique in the whole system");
				objList.add(note);
				result.setSystemResult(objList);
				return result;
			} else if (patientIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Patient has not been added, Error in: Patient Id");
				objList.add("Patient id is already existed, you must insert a unique id");
				result.setSystemResult(objList);
				return result;
			}

			else if (patientUsernameFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Patient has not been added, Error in: Patient Username");
				objList.add(
						"Patient username is already existed, you cannot register unless you have unique username in the whole system");
				objList.add(note);
				result.setSystemResult(objList);
				return result;
			}

		}
		if (patient.getAge() != null) {
			if (patient.getAge() < 0 || patient.getAge() >= 100) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Patient Age value is not possible");
				objList.add("The age must be greater than 0 and less than 100");
				result.setSystemResult(objList);
				return result;
			}
		}
		if (patient.getPhoneNumber() == null || patient.getPhoneNumber().isEmpty()
				|| patient.getPhoneNumber().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Phone Number");
			objList.add(
					"Patient Phone number cannot be empty, just in case if an appointment has been rescheduled or cancelled if the doctor cannot attend it or so");
			result.setSystemResult(objList);
			return result;
		} else {
			if (!patient.getPhoneNumber().matches("[0-9]+")) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Wrong Phone Number");
				objList.add("Patient Phone number must contain only Digits.");
				objList.add(
						"Please enter your correct phone number, to contact you just in case an appointment has been rescheduled or cancelled if the doctor cannot attend it or so");
				result.setSystemResult(objList);
				return result;
			}
		}
		if (patient.getGender() != null) {
			if (patient.getGender().equalsIgnoreCase("female") || patient.getGender().equalsIgnoreCase("male")) {
				if (patient.getGender().equalsIgnoreCase("female")) {
					patient.setGender("Female");
				} else if (patient.getGender().equalsIgnoreCase("male")) {
					patient.setGender("Male");
				}
			} else {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Patient Gender");
				objList.add("Patient gender must be either Female or Male");
				result.setSystemResult(objList);
				return result;
			}
		} else {
			result.setStatusCode("1");
			result.setStatusDescription("Error in: Patient Gender");
			objList.add("Patient Gender cannot be empty");
			result.setSystemResult(objList);
			return result;
		}
		patientRepository.save(patient);
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add(
				"Your registration has been successfully completed, as a Patient, you have been added successfully with id: "
						+ patient.getId() + ":");
		objList.add(patientRepository.findPatientById(patientRepository.findPIdByUsername(patient.getUsername())));
		result.setSystemResult(objList);
		return result;
	}

	// 2: UPDATE PATIENT------------------------------------------------
	public Result updatePatient(Patient patient, String token) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		List<String> notes = new ArrayList<>();
		boolean patientIdFlag = false;
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String usernameToken = (String) s.get("username");
		if (patient.getId() != null) {
			if (patient.getId() < 0) {
				result.setStatusCode("1");
				result.setStatusDescription("None of the patients have been updated; Error: Negative Id");
				objList.add("Patient id cannot be zero or negative");
				result.setSystemResult(objList);
				return result;
			} else if (!(patientRepository.findPIdByUsername(usernameToken) == patient.getId())) {
				result.setStatusCode("1");
				result.setStatusDescription(
						"You are not allowed to update the data of other patient! you didn't enter your own ID");
				objList.add(
						"You can only make changes on your own data, so please enter your own patient-ID to access this request correctly");
				objList.add("You are logged-in as a patient with username [" + usernameToken + "]; and your ID: ["
						+ patientRepository.findPIdByUsername(usernameToken)
						+ "], so you cannot update doctor data with ID: [" + patient.getId() + "]");
				result.setSystemResult(objList);
				return result;
			}
		} else {// ===========================
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Patient Id");
			objList.add(
					"None of the patients have been updated, patient id cannot be empty if you need to update a patient with certain id");
			result.setSystemResult(objList);
			return result;
		}
		if (patient.getName() == null || patient.getName().isEmpty() || patient.getName().isBlank()) {
			patient.setName(patientRepository.findPatientNameById(patient.getId()));
			notes.add("'Name' field is empty; then it will not be changed, it will still have the same value as: "
					+ patientRepository.findPatientNameById(patient.getId()));
		}else if (patient.getName().matches("[0-9]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Name cannot be only digits");
			objList.add("Please enter your name correctly, a name cannot be only numbers!");
			result.setSystemResult(objList);
			return result;
		} else if (!patient.getName().substring(0, 1).matches("[a-zA-Z]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Wrong Name");
			objList.add("Name must start with alphabets, Please enter your name correctly");
			result.setSystemResult(objList);
			return result;
		} else {
			String[] subS = patient.getName().split("\\s+");
			for (int i = 0; i < subS.length; i++) {
				if (!subS[i].matches("[a-zA-Z]+")) {
					result.setStatusCode("1");
					result.setStatusDescription("Error: Wrong Name, name can only have alphabets");
					objList.add("Please enter your name correctly");
					result.setSystemResult(objList);
					return result;
				}
			}
		}
		patient.setName(capitalizeWord(patient.getName()));
		if (patient.getUsername() == null || patient.getUsername().isEmpty() || patient.getUsername().isBlank()) {
			patient.setUsername(patientRepository.findPatientUsernameById(patient.getId()));
			notes.add(
					"'Username' cannot be changed; If it is empty; then it is okay as it will still have the same value as: "
							+ patientRepository.findPatientUsernameById(patient.getId()));

		}
		if (patient.getPassword() == null || patient.getPassword().isEmpty() || patient.getPassword().isBlank()) {
			patient.setPassword(patientRepository.findPasswordById(patient.getId()));
			notes.add("'Password' field is empty; then it will not be changed, it will still have the same value");
		}
		if (patient.getPhoneNumber() == null || patient.getPhoneNumber().isEmpty()
				|| patient.getPhoneNumber().isBlank()) {
			patient.setPhoneNumber(patientRepository.findPhoneNumberById(patient.getId()));
			notes.add(
					"'Phone Number' field is empty; then it will not be changed, it will still have the same value as: "
							+ patientRepository.findPhoneNumberById(patient.getId()));
		}else {
			if (!patient.getPhoneNumber().matches("[0-9]+")) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Wrong Phone Number");
				objList.add("Patient Phone number must contain only Digits.");
				objList.add(
						"Please enter your correct phone number, to contact you just in case an appointment has been rescheduled or cancelled if the doctor cannot attend it or so");
				result.setSystemResult(objList);
				return result;
			}
		}
		if (patient.getAge() == null) {
			patient.setAge(patientRepository.findAgeById(patient.getId()));
			notes.add("'Age' field is empty; then it will not be changed, it will still have the same value as: "
					+ patientRepository.findAgeById(patient.getId()));
		}
		if (patient.getGender() == null || patient.getGender().isEmpty() || patient.getGender().isBlank()) {
			patient.setGender(patientRepository.getPatientGenderById(patient.getId()));
			notes.add("'Gender' field empty; then it will not be changed, it will still have the same value as: "
					+ patientRepository.getPatientGenderById(patient.getId()));

		}
		List<Integer> patientIdList = patientRepository.findAllPatientIdNative();
		for (Integer integer : patientIdList) {
			if (patient.getId() == integer) {
				patientIdFlag = true;
				break;
			}
		}
		if (!patientIdFlag) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Not Existed Patient Id");
			objList.add("Patient id is not existed, you cannot update a non-existent patient");
			result.setSystemResult(objList);
			return result;
		}
		if (patient.getAge() != null) {
			if (!(patient.getAge() > 0 || patient.getAge() <= 100)) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Patient Age");
				objList.add("The age must be greater than 0 and less than 100");
				result.setSystemResult(objList);
				return result;
			}
		}
		if (patient.getGender() != null) {
			if (patient.getGender().equalsIgnoreCase("female") || patient.getGender().equalsIgnoreCase("male")) {
				if (patient.getGender().equalsIgnoreCase("female")) {
					patient.setGender("Female");
				} else if (patient.getGender().equalsIgnoreCase("male")) {
					patient.setGender("Male");
				}
			} else {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Patient Gender");
				objList.add("Patient gender must be either Female or Male");
				result.setSystemResult(objList);
				return result;
			}
		}
		boolean usernameCheck = !patient.getUsername()
				.equalsIgnoreCase(patientRepository.findPatientUsernameById(patient.getId()));
		if (usernameCheck) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Patient Username Cannot be Changed");
			objList.add("You cannot update your username in this request");
			result.setSystemResult(objList);
			return result;
		}
		patient.setUsername(patientRepository.findPatientUsernameById(patient.getId()));
		patientRepository.save(patient);
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add("Your request has been successfully completed, the patient with id " + patient.getId()
				+ " has been updated successfully");
		objList.add(notes);
		objList.add("The final result after updating your data is:");
		objList.add(patientRepository.findPatientById(patient.getId()));
		result.setSystemResult(objList);
		return result;
	}
	public static String capitalizeWord(String str){  
	    String words[]=str.split("\\s");  
	    String capitalizeWord="";  
	    for(String w:words){  
	        String first=w.substring(0,1);  
	        String afterfirst=w.substring(1);  
	        capitalizeWord+=first.toUpperCase()+afterfirst+" ";  
	    }  
	    return capitalizeWord.trim();  
	} 
}
