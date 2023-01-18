package jpa.finalproject.tala.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jpa.finalproject.tala.entity.Doctor;
import jpa.finalproject.tala.jwt.and.security.DoctorInterceptorService;
import jpa.finalproject.tala.repository.DoctorRepository;
import jpa.finalproject.tala.repository.PatientRepository;
import jpa.finalproject.tala.result.info.Result;

@Service
public class DoctorService {
	@Autowired
	DoctorRepository docRepository;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	DoctorInterceptorService doctorInterceptorService;

	// Doctor Reports: doctor can get the summary for all booked time-line from -to
	// date
	public Result getDoctorReportAsCsvFile(Integer doctorId, LocalDate startDate, LocalDate endDate) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		if (doctorId == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Doctor Id");
			objList.add("Request cannot be completed with empty Doctor Id");
			result.setSystemResult(objList);
			return result;
		}
		if (doctorId < 0) {
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
		List<ArrayList<Object>> appointmentRecords = docRepository.doctorReport(doctorId, startDate, endDate);
		File file = new File("C:\\Users\\XYZ\\Doctor Report.csv");
		try (PrintWriter writer = new PrintWriter(file)) {
			writer.println(
					"Appointment Id, Appointment Date, Appointment Time, End Time, Visit Status, Doctor Id, Doctor Name, Patient Id, Patient Name");
			appointmentRecords.forEach(x -> {
				StringJoiner sj = new StringJoiner(",");
				sj.add(x.get(0) != null ? x.get(0).toString() : "NULL"); // appointment Id
				sj.add(x.get(1) != null ? x.get(1).toString() : "NULL"); // appointment Date
				sj.add(x.get(2) != null ? x.get(2).toString() : "NULL"); // appointment Time
				sj.add(x.get(3) != null ? x.get(3).toString() : "NULL"); // appointment EndTime
				sj.add(x.get(4) != null ? x.get(4).toString() : "NULL"); // visit Status
				sj.add(x.get(5) != null ? x.get(5).toString() : "NULL"); // Doctor Id
				sj.add(x.get(7) != null ? x.get(7).toString() : "NULL"); // Doctor Name
				sj.add(x.get(6) != null ? x.get(6).toString() : "NULL"); // Patient Id
				sj.add(x.get(8) != null ? x.get(8).toString() : "NULL"); // Patient Name
				writer.println(sj.toString());
			});
			writer.flush();

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

	// SHOW A PATIENT PROFILE-----------------------------------------------------
	public Result showProfileOfPatient(Integer patientId) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();

		if (patientId == null) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Patient Id");
			objList.add("You must enter a patient Id to see their profile information");
			result.setSystemResult(objList);
			return result;
		}

		if (patientId < 0) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Negative ID");
			objList.add("ID values cannot be negative");
			result.setSystemResult(objList);
			return result;
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
		objList.add("Patient with id [" + patientId + "] Profile Info:");
		List<ArrayList<Object>> list = new ArrayList<>();
		list = patientRepository.showProfileOfPatient(patientId);
		for (ArrayList<Object> arrayList : list) {
			objList.add("Patient ID: " + arrayList.get(0));
			objList.add("Patient Username: " + arrayList.get(1));
			objList.add("Patient Name: " + arrayList.get(2));
			objList.add("Patient Phone Number: " + arrayList.get(3));
			objList.add("Patient Age: " + arrayList.get(4));
			objList.add("Patient Gender: " + arrayList.get(5));
		}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		result.setSystemResult(objList);
		return result;
	}

	// LIST OF ALL PATIENTS -------------------------------------------------------
	public Result showListOfAllPatients() {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		List<ArrayList<Object>> list = new ArrayList<>();
		list = patientRepository.showListOfAllPatients();
		objList.add("Your request has been successfully completed");
		objList.add("List of all patients; contains the Patient Id along with the Patient Name:");
		for (ArrayList<Object> arrayList : list) {
			objList.add("Patient ID: " + arrayList.get(0) + ", Patient Name: " + arrayList.get(1));
		}
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		result.setSystemResult(objList);
		return result;
	}

	// 1.REGISTER DOCTOR-----------------------------------------------------------
	public Result addDoctor(Doctor doctor) throws SQLException {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		boolean docIdFlag = false, docNatIdFlag = false, docUsernameFlag = false;
		if (doctor.getName() == null || doctor.getName().isEmpty() || doctor.getName().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty name");
			objList.add("Doctor name cannot be empty, you must insert your name");
			result.setSystemResult(objList);
			return result;
		} else if (doctor.getName().matches("[0-9]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Name cannot be only digits");
			objList.add("Please enter your name correctly, a name cannot be only numbers!");
			result.setSystemResult(objList);
			return result;
		} else if (!doctor.getName().substring(0, 1).matches("[a-zA-Z]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Wrong Name");
			objList.add("Name must start with alphabets, Please enter your name correctly");
			result.setSystemResult(objList);
			return result;
		} else {
			String[] subS = doctor.getName().split("\\s+");
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
		boolean docName = false;
		List<String> docNames = docRepository.findAllDocNameNative();
		for (String string : docNames) {
			if (doctor.getName().equalsIgnoreCase(string)) {
				docName = true;
				break;
			}
		}
		if (docName) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Duplicated FULL NAME");
			objList.add("Doctor FULL name must be unique");
			result.setSystemResult(objList);
			return result;
		}
		doctor.setName(capitalizeWord(doctor.getName()));
		if (doctor.getUsername() == null || doctor.getUsername().isEmpty() || doctor.getUsername().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty username");
			objList.add("Doctor username cannot be empty, you must insert a username");
			result.setSystemResult(objList);
			return result;
		} else if (doctor.getUsername().matches("[0-9]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Username cannot be only digits");
			objList.add("Doctor Username cannot be only numbers! Please enter a correct username");
			result.setSystemResult(objList);
			return result;
		}else if (!doctor.getUsername().substring(0, 1).matches("[a-zA-Z]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Wrong Username");
			objList.add("Username cannot start with numbers or symbols, Please enter your username correctly");
			result.setSystemResult(objList);
			return result;
		} 
		else {
			String str = doctor.getUsername();
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
		doctor.setUsername(capitalizeWord(doctor.getUsername()));
		
		if (doctor.getPassword() == null || doctor.getPassword().isEmpty() || doctor.getPassword().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty password");
			objList.add("The password cannot be empty, you must insert a password");
			result.setSystemResult(objList);
			return result;
		}
		if (doctor.getSpecialty() == null || doctor.getSpecialty().isEmpty() || doctor.getSpecialty().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Specialty");
			objList.add("Doctor specialty cannot be empty");
			result.setSystemResult(objList);
			return result;
		} else if (doctor.getSpecialty().matches("[0-9]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Specialty cannot be only digits!");
			objList.add("Please enter your specialty correctly, a specialty cannot be only numbers!");
			result.setSystemResult(objList);
			return result;
		} else if (!doctor.getSpecialty().substring(0, 1).matches("[a-zA-Z]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Wrong Specialty");
			objList.add("Specialty must start with alphabets, Please enter your specialty correctly");
			result.setSystemResult(objList);
			return result;
		} else {
			String[] subS = doctor.getSpecialty().split("\\s+");
			for (int i = 0; i < subS.length; i++) {
				if (!subS[i].matches("[a-zA-Z]+")) {
					result.setStatusCode("1");
					result.setStatusDescription("Error: Wrong Specialty, specialty can only have alphabets");
					objList.add("Please enter your specialty correctly");
					result.setSystemResult(objList);
					return result;
				}
			}
		}
		if (doctor.getPhoneNumber() == null || doctor.getPhoneNumber().isEmpty() || doctor.getPhoneNumber().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Phone Number");
			objList.add(
					"Doctor Phone number cannot be empty, just in case an appointment has been rescheduled or cancelled if the patient cannot attend it or so");
			result.setSystemResult(objList);
			return result;
		} else {
			if (!doctor.getPhoneNumber().matches("[0-9]+")) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Wrong Phone Number");
				objList.add("Doctor Phone number must contain only Digits.");
				objList.add(
						"Please enter your correct phone number, to contact you just in case an appointment has been rescheduled or cancelled if the patient cannot attend it or so");
				result.setSystemResult(objList);
				return result;
			}
		}
		if (doctor.getNationalId() == null || doctor.getNationalId().isEmpty() || doctor.getNationalId().isBlank()) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty National Id");
			objList.add("Doctor National Id number cannot be empty");
			result.setSystemResult(objList);
			return result;
		} else if (doctor.getNationalId() != null) {
			if (!doctor.getNationalId().matches("[0-9]+")) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Wrong National Id");
				objList.add("Doctor National Id number must contain only Digits.");
				objList.add("Please enter your correct National Id Number");
				result.setSystemResult(objList);
				return result;
			}
		}

		if (doctor.getId() != null) {
			if (doctor.getId() < 0) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Negative Id");
				objList.add("Doctor id cannot be zero or negative");
				result.setSystemResult(objList);
				return result;
			}
		}
		List<Integer> docIdList = docRepository.findAllDocIdNative();
		for (Integer integer : docIdList) {
			if (doctor.getId() == integer) {
				docIdFlag = true;
				break;
			}
		}
		List<String> docNatIdList = docRepository.findAllDocNatIdNative();
		for (String string : docNatIdList) {
			if (doctor.getNationalId().equalsIgnoreCase(string)) {
				docNatIdFlag = true;
				break;
			}
		}
		docNatIdFlag = docNatIdFlag && doctor.getNationalId() != null;
		List<String> docUsernameList = docRepository.findAllDocUsernameNative();
		docUsernameList.addAll(patientRepository.findAllPatientUsernameNative());
		for (String string : docUsernameList) {
			if (doctor.getUsername().equalsIgnoreCase(string)) {
				docUsernameFlag = true;
				break;
			}
		}
		String note = "The username you entered may already have been taken by a patient or a doctor";
		if (docUsernameFlag || docIdFlag || docNatIdFlag) {
			if (docIdFlag && docUsernameFlag && docNatIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription(
						"Error in: duplicated Id, duplicated Username(username must be globally unique), and duplicated National Id");
				objList.add(
						"Doctor id must be unique, Doctor username must be unique in the whole system, and Doctor National ID must be unique.");
				objList.add(note);
				result.setSystemResult(objList);
				return result;
			}
			if (docNatIdFlag && docIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Id, and National Id");
				objList.add("Doctor id must be unique, and Doctor National ID must be unique.");
				result.setSystemResult(objList);
				return result;
			} else if (docNatIdFlag && docUsernameFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Username, and National Id");
				objList.add(
						"Doctor username must be globally unique (unique in the whole system), and Doctor National ID must be unique.");
				objList.add(note);
				result.setSystemResult(objList);
				return result;
			} else if (docIdFlag && docUsernameFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Id, and Username");
				objList.add(
						"Doctor id must be unique, and Doctor username must be unique in the whole system (globally unique).");
				objList.add(note);
				result.setSystemResult(objList);
				return result;
			} else if (docIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Doctor Id");
				objList.add("Doctor id is already existed, you must insert a unique id");
				result.setSystemResult(objList);
				return result;
			}
			else if (docUsernameFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Doctor Username");
				objList.add(
						"Username is already existed in the system, you cannot register unless you have unique username in the whole system (Globally unique)");
				objList.add(note);
				result.setSystemResult(objList);
				return result;
			} else if (docNatIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Doctor National Id");
				objList.add(
						"The National ID numbers are considered to be unique for all users, please enter your correct national id number");
				result.setSystemResult(objList);
				return result;
			}
		}

		docRepository.save(doctor);
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add(
				"Your registration has been successfully completed, as a Doctor, you have been added successfully with id: "
						+ doctor.getId() + ":");
		objList.add(docRepository.findDocById(doctor.getId()));
		result.setSystemResult(objList);
		return result;

	}

	// 2: UPDATE DOCTOR-------------------------------------------------------------
	public Result updateDoctor(Doctor doctor, String token) throws SQLException {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		List<String> notes = new ArrayList<>();
		boolean docIdFlag = false;
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String usernameToken = (String) s.get("username");
		if (doctor.getId() != null) {
			if (doctor.getId() < 0) {
				result.setStatusCode("1");
				result.setStatusDescription("None of the doctors have been updated; Error: Negative Id");
				objList.add("Doctor id cannot be zero or negative");
				result.setSystemResult(objList);
				return result;
			} else if (!(docRepository.findIdByUsername(usernameToken) == doctor.getId())) {
				result.setStatusCode("1");
				result.setStatusDescription(
						"You are not allowed to update the data of other doctor! you didn't enter your own ID");
				objList.add(
						"You can only make changes on your own data, so please enter your own doctor-ID to access this request correctly");
				objList.add("You are logged-in as a doctor with username [" + usernameToken + "]; and your ID: ["
						+ docRepository.findIdByUsername(usernameToken)
						+ "], so you cannot update doctor data with ID: [" + doctor.getId() + "]");
				result.setSystemResult(objList);
				return result;
			}
		} else {// ===========================
			result.setStatusCode("1");
			result.setStatusDescription("Error: Empty Doctor Id");
			objList.add(
					"None of the doctors have been updated, doctor id cannot be empty if you need to update a doctor with certain id");
			result.setSystemResult(objList);
			return result;
		}
		if (doctor.getName() == null || doctor.getName().isEmpty() || doctor.getName().isBlank()) {
			doctor.setName(docRepository.findNameById(doctor.getId()));
			notes.add("'Name' field empty; then it will not be changed, it will still have the same value as: "
					+ docRepository.findNameById(doctor.getId()));
		} else if (doctor.getName().matches("[0-9]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Name cannot be only digits");
			objList.add("Doctor Name cannot be only numbers! Please enter your name correctly");
			result.setSystemResult(objList);
			return result;
		} else if (!doctor.getName().substring(0, 1).matches("[a-zA-Z]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Wrong Name");
			objList.add("Name must start with alphabets, Please enter your name correctly");
			result.setSystemResult(objList);
			return result;
		} else {
			String[] subS = doctor.getName().split("\\s+");
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
		doctor.setName(capitalizeWord(doctor.getName()));
		if (doctor.getUsername() == null || doctor.getUsername().isEmpty() || doctor.getUsername().isBlank()) {
			doctor.setUsername(docRepository.findUsernameById(doctor.getId()));
			notes.add(
					"'Username' cannot be changed; If it is empty; then it is okay as it will still have the same value as: "
							+ docRepository.findUsernameById(doctor.getId()));
		}
		if (doctor.getPassword() == null || doctor.getPassword().isEmpty() || doctor.getPassword().isBlank()) {
			doctor.setPassword(docRepository.findPasswordById(doctor.getId()));
			notes.add("'Password' is field empty; then it will not be changed, it will still have the same value");
		}
		if (doctor.getSpecialty() == null || doctor.getSpecialty().isEmpty() || doctor.getSpecialty().isBlank()) {
			doctor.setSpecialty(docRepository.findSpecialtyById(doctor.getId()));
			notes.add("'Specialty' field is empty; then it will not be changed, it will still have the same value as: "
					+ docRepository.findSpecialtyById(doctor.getId()));
		} else if (doctor.getSpecialty().matches("[0-9]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Specialty cannot be only digits!");
			objList.add("Please enter your specialty correctly, a specialty cannot be only numbers!");
			result.setSystemResult(objList);
			return result;
		} else if (!doctor.getSpecialty().substring(0, 1).matches("[a-zA-Z]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Wrong Specialty");
			objList.add("Specialty must start with alphabets, Please enter your specialty correctly");
			result.setSystemResult(objList);
			return result;
		} else {
			String[] subS = doctor.getSpecialty().split("\\s+");
			for (int i = 0; i < subS.length; i++) {
				if (!subS[i].matches("[a-zA-Z]+")) {
					result.setStatusCode("1");
					result.setStatusDescription("Error: Wrong Specialty, specialty can only have alphabets");
					objList.add("Please enter your specialty correctly");
					result.setSystemResult(objList);
					return result;
				}
			}
		}
		if (doctor.getPhoneNumber() == null || doctor.getPhoneNumber().isEmpty() || doctor.getPhoneNumber().isBlank()) {
			doctor.setPhoneNumber(docRepository.findPhoneNumberById(doctor.getId()));
			notes.add(
					"'Phone Number' field is empty; then it will not be changed, it will still have the same value as: "
							+ docRepository.findPhoneNumberById(doctor.getId()));
		} else {
			if (!doctor.getPhoneNumber().matches("[0-9]+")) {
				result.setStatusCode("1");
				result.setStatusDescription("Error: Wrong Phone Number");
				objList.add("Doctor Phone number must contain only Digits.");
				objList.add(
						"Please enter your correct phone number, to contact you just in case an appointment has been rescheduled or cancelled if the patient cannot attend it or so");
				result.setSystemResult(objList);
				return result;
			}
		}

		List<Integer> docIdList = docRepository.findAllDocIdNative();
		for (Integer integer : docIdList) {
			if (doctor.getId() == integer) {
				docIdFlag = true;
				break;
			}
		}
		if (!docIdFlag) {
			result.setStatusCode("1");
			result.setStatusDescription("None of the doctors have been updated; Error: Not Existed Doctor Id");
			objList.add("Doctor id is not existed, you cannot update a non-existent doctor");
			result.setSystemResult(objList);
			return result;
		}
		if (doctor.getNationalId() == null || doctor.getNationalId().isEmpty() || doctor.getNationalId().isBlank()) {
			doctor.setNationalId(docRepository.findNatIdById(doctor.getId()));
			notes.add(
					"'National ID' field is empty; then it will not be changed, it will still have the same value as: "
							+ docRepository.findNatIdById(doctor.getId()));
		} else if (!doctor.getNationalId().matches("[0-9]+")) {
			result.setStatusCode("1");
			result.setStatusDescription("Error: Wrong National Id");
			objList.add("Doctor National Id number must contain only Digits.");
			objList.add("Please enter your correct National Id Number");
			result.setSystemResult(objList);
			return result;
		} else if (!doctor.getNationalId().equals(docRepository.findNatIdById(doctor.getId()))){
			boolean docNatIdFlag = false;
			List<String> docNatIdList = docRepository.findAllDocNatIdNative();
			for (String string : docNatIdList) {
				if (doctor.getNationalId().equals(string)) {
					docNatIdFlag = true;
					break;
				}
			}
			if (docNatIdFlag) {
				result.setStatusCode("1");
				result.setStatusDescription("Error in: Doctor National Id");
				objList.add(
						"The National ID numbers are considered to be unique for all users, please enter your correct National Id Number");
				objList.add("As other doctor has this national Id you attempted to insert");
				result.setSystemResult(objList);
				return result;
			}

		}
		boolean usernameCheck = !doctor.getUsername().equalsIgnoreCase(docRepository.findUsernameById(doctor.getId()));
		if (usernameCheck) {
			result.setStatusCode("1");
			result.setStatusDescription(
					"None of the doctors data have been updated; Error: Doctor Username Cannot be Changed");
			objList.add("You cannot update your username in this request");
			result.setSystemResult(objList);
			return result;
		}
		doctor.setUsername(docRepository.findUsernameById(doctor.getId()));
		docRepository.save(doctor);
		result.setStatusCode("0");
		result.setStatusDescription("Successful");
		objList.add("Your request has been successfully completed, the doctor with id " + doctor.getId()
				+ " has been successfully updated");
		objList.add(notes);
		objList.add("The final result after updating your data is:");
		objList.add(docRepository.findDocById(doctor.getId()));
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
