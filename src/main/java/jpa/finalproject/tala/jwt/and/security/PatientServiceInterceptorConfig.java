package jpa.finalproject.tala.jwt.and.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class PatientServiceInterceptorConfig implements WebMvcConfigurer {
	@Autowired
	private PatientInterceptorService patientInterceptorService;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(patientInterceptorService).addPathPatterns("/patient/**")
				.excludePathPatterns("/login/userLogin")
				.excludePathPatterns("/patient/patientRegistration")
				.excludePathPatterns("/patient/getPatientReportFile");
	}

}
