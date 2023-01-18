package jpa.finalproject.tala.jwt.and.security;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jpa.finalproject.tala.result.info.Result;
@Component
public class PatientInterceptorService implements HandlerInterceptor {
	@Autowired
	TokenGenerator tokenGenerator;
	@Autowired
	private ObjectMapper mapper;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String token = request.getHeader("token");
		Result result = new Result();
		Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
		String userType = (String) s.get("userType");
		List<Object> obList = new ArrayList<>();
		if (token == null || token.isEmpty() || token.isBlank()) {
			result.setStatusCode("401");
			result.setStatusDescription("token");
			obList.add("token cannot be empty, please enter the token in the header to get access");
			result.setSystemResult(obList);
			String finalResult = mapper.writeValueAsString(result);
			response.setStatus(401);
			response.setContentType("application/json");
			try (PrintWriter writer = response.getWriter()) {
				writer.write(finalResult);
			}
			return false;
		} else if (userType.equalsIgnoreCase("d")||userType.equalsIgnoreCase("doctor")) {
			result.setStatusCode("403");
			result.setStatusDescription("A doctor is not autherized to access this URL Request.");
			obList.add("Please try other Requests that are valid for patients");
			result.setSystemResult(obList);
			String finalResult = mapper.writeValueAsString(result);
			response.setStatus(403);
			response.setContentType("application/json");
			try (PrintWriter writer = response.getWriter()) {
				writer.write(finalResult);
			}
			return false;
		} else {
			Result resultToken = tokenGenerator.checkToken(token);
			if (resultToken.getStatusCode().equalsIgnoreCase("0")) {
				return true;
			} else {
				String finalResult = mapper.writeValueAsString(resultToken);
				response.setStatus(401);
				response.setContentType("application/json");

				try (PrintWriter writer = response.getWriter()) {
					writer.write(finalResult);
				}
				return false;
			}

		}
	}
}
