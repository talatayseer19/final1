package jpa.finalproject.tala.jwt.and.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class DoctorServiceInterceptorConfig implements WebMvcConfigurer {
	@Autowired
	private DoctorInterceptorService doctorInterceptorService;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(doctorInterceptorService).addPathPatterns("/doctor/**")
		.excludePathPatterns("/login/userLogin")
		.excludePathPatterns("/doctor/doctorRegistration")
		.excludePathPatterns("/doctor/getDoctorReportFile");

	}

}
