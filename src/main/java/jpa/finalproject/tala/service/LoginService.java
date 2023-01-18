package jpa.finalproject.tala.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jpa.finalproject.tala.entity.Doctor;
import jpa.finalproject.tala.entity.Patient;
import jpa.finalproject.tala.jwt.and.security.TokenGenerator;
import jpa.finalproject.tala.repository.DoctorRepository;
import jpa.finalproject.tala.repository.PatientRepository;
import jpa.finalproject.tala.result.info.Login;
import jpa.finalproject.tala.result.info.Result;

@Service
public class LoginService {
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	PatientRepository patientRepository;
	@Autowired
	TokenGenerator tokenGenerator;

	public Result login(String isDoctorOrPatient, Login login) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		if (isDoctorOrPatient.equalsIgnoreCase("d") || isDoctorOrPatient.equalsIgnoreCase("doctor")) {
			Doctor doctor = doctorRepository.findDoctorByUsername(login.getUsername());
			if (doctor == null) {
				result.setStatusCode("1");
				result.setStatusDescription("Couldn't find Doctor with username: " + login.getUsername());
				objList.add("The doctor user is not found");
				result.setSystemResult(objList);
				return result;
			}
			if (!(doctor.getPassword().equalsIgnoreCase(login.getPassword()))) {
				result.setStatusCode("1");
				result.setStatusDescription("Incorrect Password");
				objList.add("Your password is incorrect");
				result.setSystemResult(objList);
				return result;
			}
			String token = tokenGenerator.generateToken(login.getUsername(), isDoctorOrPatient);
			result.setStatusCode("0");
			result.setStatusDescription("Successful");
			objList.add("Token has been successfully generated for DOCTOR: "+login.getUsername());
			objList.add("token: " + token);
			objList.add("Note: you are logged as ["+login.getUsername()+"]; there are some restrictions:");
			objList.add("You are only allowed to update your own data, and you can only schedule or cancel appointments that are only associated by you");
			result.setSystemResult(objList);
			return result;
		} else if (isDoctorOrPatient.equalsIgnoreCase("p") || isDoctorOrPatient.equalsIgnoreCase("patient")) {
			Patient patient = patientRepository.findPatientByUsername(login.getUsername());
			if (patient == null) {
				result.setStatusCode("1");
				result.setStatusDescription("Couldn't find Patient with username: " + login.getUsername());
				objList.add("The patient user is not found");
				result.setSystemResult(objList);
				return result;
			}
			if (!(patient.getPassword().equalsIgnoreCase(login.getPassword()))) {
				result.setStatusCode("1");
				result.setStatusDescription("Incorrect Password");
				objList.add("Your password is incorrect");
				result.setSystemResult(objList);
				return result;
			}
			String token = tokenGenerator.generateToken(login.getUsername(), isDoctorOrPatient);
			result.setStatusCode("0");
			result.setStatusDescription("Successful");
			objList.add("Token has been successfully generated for PATIENT: "+login.getUsername().toLowerCase());
			objList.add("token: " + token);
			objList.add("Note: you are logged as ["+login.getUsername()+"]; there are some restrictions:");
			objList.add("You are only allowed to update your own data, and you can only schedule or cancel appointments that are only associated by you");
			result.setSystemResult(objList);
			return result;
		} else {
			result.setStatusCode("1");
			result.setStatusDescription(
					"You must select whether you are a patient or a doctor to get access to the system");
			objList.add(
					"You can type 'doctor' or simply 'd' if you're a doctor, or type 'patient' or simply 'p' if you're a patient");
			objList.add("And then enter your username and password to get access to the system");
			result.setSystemResult(objList);
			return result;
		}
	}

}
