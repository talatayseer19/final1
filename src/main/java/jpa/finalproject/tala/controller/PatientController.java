package jpa.finalproject.tala.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jpa.finalproject.tala.entity.Doctor;
import jpa.finalproject.tala.entity.Patient;
import jpa.finalproject.tala.result.info.Result;
import jpa.finalproject.tala.service.AppointmentService;
import jpa.finalproject.tala.service.DoctorService;
import jpa.finalproject.tala.service.PatientService;

@RestController
@RequestMapping("/patient")
public class PatientController {

	@Autowired
	PatientService patientService;
	@Autowired	
	AppointmentService appointmentService;

	@GetMapping(value = "/getPatientReportFile")
	public Object downloadCsvWithNameFile(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer patientId, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {
		if(patientService.getPatientReportAsCsvFile(patientId, startDate, endDate).getStatusCode()=="1") {
			List<Object> result = new ArrayList();
			result.add(patientService.getPatientReportAsCsvFile(patientId, startDate, endDate));
			return result;
		}
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=" + "Patient Report.csv");
		String pathFile = patientService.getPatientReportAsCsvFile(patientId, startDate, endDate).getSystemResult().get(0)
				.toString();
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(new File(pathFile)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStreamResource resource = new InputStreamResource(input);
		return ResponseEntity.ok().headers(header).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}
	
	@DeleteMapping("/cancelAppointment")
	public Result cancelAppointment(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer appointmentId) {
		String token = request.getHeader("token");
		return appointmentService.cancelAppointment(appointmentId,token);
	}
	
	@PostMapping("/createAnAppointment")
	public Result createAppointment(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer doctorId, @RequestParam Integer patientId,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
			@RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time) {
		String token = request.getHeader("token");
		return appointmentService.createAnAppointment(doctorId, patientId, date, time,token);
	}
	
	@GetMapping("/findAllAvailableDoctorsInCertainDateAndTime")
	public Result findAllAvailableDoctors(HttpServletRequest request, HttpServletResponse response, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
			@RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time) {
		return patientService.findAllAvailableDoctors(date, time);
	}
	@GetMapping("/findAllAvailableDoctorsForTodayInCertainTime")
	public Result findAllAvailableDoctorsForTodayInCertainTime(HttpServletRequest request, HttpServletResponse response,
			@RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time) {
		return patientService.findAllAvailableDoctorsForTodayInCertainTime(time);
	}
	@GetMapping("/showListOfAllDoctors")
	public Result showListOfAllDoctors(HttpServletRequest request, HttpServletResponse response) {
		return patientService.showListOfAllDoctors();
	}

	@PutMapping("/updatePatientData")
	public Result updatePatient(HttpServletRequest request, HttpServletResponse response, @RequestBody Patient patient) {
		String token = request.getHeader("token");
		return patientService.updatePatient(patient,token);
	}
	
	@PostMapping("/patientRegistration")
	public Result addPatient(HttpServletRequest request, HttpServletResponse response, @RequestBody Patient patient) {
		return patientService.addPatient(patient);
	}
	// ===========================================================================
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result handleValidationExceptions(MethodArgumentNotValidException exc) {
		Result result = new Result();
		result.setStatusCode("1");
		List<Object> objList = new ArrayList();
		exc.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			result.setStatusDescription(fieldName);
			objList.add(errorMessage);
		});
		result.setSystemResult(objList);
		return result;

	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public Result handleAllExceptionMethod(Exception exc, WebRequest requset, HttpServletResponse res) {
		Result result = new Result();
		result.setStatusCode("1");
		result.setStatusDescription("Exception");
		List<Object> objList = new ArrayList();
		objList.add(exc.getMessage());
		result.setSystemResult(objList);
		return result;
	}

}
