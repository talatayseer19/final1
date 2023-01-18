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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
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

import jpa.finalproject.tala.entity.Doctor;
import jpa.finalproject.tala.result.info.Result;
import jpa.finalproject.tala.service.AppointmentService;
import jpa.finalproject.tala.service.DoctorService;

//@ControllerAdvice 
@RestController
@RequestMapping("/doctor")
public class DoctorController {

	@Autowired
	DoctorService docService;
	@Autowired
	AppointmentService appointmentService;

	@PostMapping("/doctorRegistration")
	public Result addDoctor(HttpServletRequest request, HttpServletResponse response, @RequestBody Doctor doctor)
			throws SQLException {
		return docService.addDoctor(doctor);
	}

	@PutMapping("/updateDoctorData")
	public Result updateDoctor(HttpServletRequest request, HttpServletResponse response, @RequestBody Doctor doctor)
			throws SQLException {
		String token = request.getHeader("token");
		return docService.updateDoctor(doctor,token);
	}

	@GetMapping("/showAvailableTimeForCertainDate")
	public Result showAvailableTimeForCertainDate(HttpServletRequest request, HttpServletResponse response,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date) {
		String token = request.getHeader("token");
		return appointmentService.showAvailableTimeForCertainDate(date,token);
	}

	@GetMapping("/showAvailableTimesForToday")
	public Result showAvailableTime(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getHeader("token");
		return appointmentService.showAvailableTimeForToday(token);
	}
	
	
	@GetMapping("/showAvailableTimesForCertainDoctorAndCertainDate")
	public Result showAvailableTimesForCertainDoctorAndCertainDate(HttpServletRequest request, HttpServletResponse response,@RequestParam Integer doctorId, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date) {
		return appointmentService.showAvailableTimesForCertainDoctorAndCertainDate(doctorId, date);
	}

	@GetMapping("/showBookedTimelineForCertainDate")
	public Result showBookedTimelineForCertainDate(HttpServletRequest request, HttpServletResponse response,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date) {
		String token = request.getHeader("token");
		return appointmentService.showBookedTimelineForCertainDate(date,token);
	}

	@GetMapping("/showBookedTimelineForToday")
	public Result showTimeline(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getHeader("token");
		return appointmentService.showBookedTimelineForToday(token);
	}
	
	@GetMapping("/showBookedTimelineForCertainDoctorAndCertainDate")
	public Result showBookedTimelineForCertainDoctorAndCertainDate(HttpServletRequest request, HttpServletResponse response,@RequestParam Integer doctorId, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date) {
		return appointmentService.showBookedTimelineForCertainDoctorAndCertainDate(doctorId, date);
	}

	@PostMapping("/createAnAppointment")
	public Result createAppointment(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer doctorId, @RequestParam Integer patientId,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date,
			@RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time) {
		String token = request.getHeader("token");
		return appointmentService.createAnAppointment(doctorId, patientId, date, time,token);
	}

	@DeleteMapping("/cancelAppointment")
	public Result cancelAppointment(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer appointmentId) {
		String token = request.getHeader("token");
		return appointmentService.cancelAppointment(appointmentId,token);
	}

	@PutMapping("/updateAppointmentVisitStatus")
	public Result updateAppointmentVisitStatus(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer appointmentId, @RequestParam Integer status) {
		String token = request.getHeader("token");
		return appointmentService.updateAppointmentVisitStatus(appointmentId, status, token);
	}

	@GetMapping(value = "/getDoctorReportFile")
	public Object downloadCsvWithNameFile(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer doctorId, @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
			@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {
		if (docService.getDoctorReportAsCsvFile(doctorId, startDate, endDate).getStatusCode() == "1") {
			List<Object> result = new ArrayList();
			result.add(docService.getDoctorReportAsCsvFile(doctorId, startDate, endDate));
			return result;
		}
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + "Doctor Report.csv");
		String pathFile = docService.getDoctorReportAsCsvFile(doctorId, startDate, endDate).getSystemResult().get(0)
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

	@GetMapping("/findHowManyTimesACertainPatientVisitedADoctor")
	public Result findHowManyTimesAPatientVisitedADoctor(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer doctorId, @RequestParam Integer patientId) {
		return appointmentService.findHowManyTimesAPatientVisitedADoctor(doctorId, patientId);
	}

	@GetMapping("findTotalNumberOfPatientsVisitedADoctor")
	public Result findTotalNumberOfPatientsVisitedADoctor(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer doctorId) {
		return appointmentService.findTotalNumberOfPatientsVisitedADoctor(doctorId);
	}

	@GetMapping("/showListOfAllPatients")
	public Result showListOfAllPatients(HttpServletRequest request, HttpServletResponse response) {
		return docService.showListOfAllPatients();
	}

	@GetMapping("/showProfileOfPatient")
	public Result showProfileOfPatient(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Integer patientId) {
		return docService.showProfileOfPatient(patientId);
	}
	
	@GetMapping("/showAllAppointmentsInCertainDate")
	public Result showAppointments(HttpServletRequest request, HttpServletResponse response,@RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date) {
		return appointmentService.showAllAppointments(date);
	}


	// ===========================================================================
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
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
	@ExceptionHandler(value = Exception.class)
	public Result handleAllExceptionMethod(Exception exc, WebRequest requset, HttpServletResponse res) {
		Result result = new Result();
		result.setStatusCode("1");
		result.setStatusDescription("Exception");
		List<Object> objList = new ArrayList();
		objList.add(exc.getMessage());
		result.setSystemResult(objList);
		return result;
	}
	
//	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Mismatched type of data")
//    @ExceptionHandler(value = HttpMessageNotReadableException.class)
//    public Result handleException(JsonParseException exc, WebRequest requset, HttpServletResponse res) {
//		Result result = new Result();
//		result.setStatusCode("1");
//		result.setStatusDescription("Exception");
//		List<Object> objList = new ArrayList();
//		objList.add(exc.getMessage());
//		result.setSystemResult(objList);
//		return result;
//    }

}
