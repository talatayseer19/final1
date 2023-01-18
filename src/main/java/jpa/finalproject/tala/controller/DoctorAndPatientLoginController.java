package jpa.finalproject.tala.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import jpa.finalproject.tala.result.info.Login;
import jpa.finalproject.tala.result.info.Result;
import jpa.finalproject.tala.service.LoginService;

@ControllerAdvice
@RestController
@RequestMapping("/login")
public class DoctorAndPatientLoginController {

	@Autowired
	LoginService loginService;

	@PostMapping("/userLogin")
	public Result login(@RequestParam String isDoctorOrPatient, @RequestBody Login login) {
		return loginService.login(isDoctorOrPatient, login);
	}

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
