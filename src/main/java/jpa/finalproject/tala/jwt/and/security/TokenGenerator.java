package jpa.finalproject.tala.jwt.and.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jpa.finalproject.tala.result.info.Result;

@Component
public class TokenGenerator {
	public final Integer expDate = 60000;

	public String generateToken(String username, String userType) {
		Map<String, Object> info = new HashMap();
		info.put("username", username);
		info.put("userType", userType);
		return Jwts.builder().setClaims(info).signWith(SignatureAlgorithm.HS256, "DentalClinic123").compact();
	}
	
	public Result checkToken(String token) {
		Result result = new Result();
		List<Object> objList = new ArrayList<>();
		try {
			Claims s = Jwts.parser().setSigningKey("DentalClinic123").parseClaimsJws(token).getBody();
			result.setStatusCode("0");
			String username = (String) s.get("username");
			result.setStatusDescription("username");
			objList.add(username);
			result.setSystemResult(objList);
			return result;
		} catch (SignatureException ex) {
			result.setStatusCode("1");
			result.setStatusDescription("Error");
			objList.add("Invalid JWT signature");
			result.setSystemResult(objList);
			return result;

		} catch (MalformedJwtException ex) {
			result.setStatusCode("1");
			result.setStatusDescription("Error:");
			objList.add("Invalid JWT token");
			result.setSystemResult(objList);
			return result;

		} catch (ExpiredJwtException ex) {
			result.setStatusCode("1");
			result.setStatusDescription("Error:");
			objList.add("Expired JWT token");
			result.setSystemResult(objList);
			return result;

		} catch (UnsupportedJwtException ex) {
			result.setStatusCode("1");
			result.setStatusDescription("Error:");
			objList.add("Unsupported JWT token");
			result.setSystemResult(objList);
			return result;

		}

		catch (IllegalArgumentException ex) {
			result.setStatusCode("1");
			result.setStatusDescription("Error:");
			objList.add("JWT string is empty");
			result.setSystemResult(objList);
			return result;

		} catch (Exception e) {
			result.setStatusCode("1");
			result.setStatusDescription("Error:");
			objList.add(e.getMessage());
			result.setSystemResult(objList);
			return result;
		}

	}

}
