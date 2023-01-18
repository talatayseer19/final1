package jpa.finalproject.tala.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "appointment")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
	@Column(name = "appointment_id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Column(name = "appointment_date")
	LocalDate appointmentDate;

	@JsonFormat(pattern = "HH:mm")
	@Column(name = "appointment_time")
	LocalTime appointmentTime;

	@JsonFormat(pattern = "HH:mm")
	@Column(name = "end_time", columnDefinition = "time default (appointment_time +10000)")
	LocalTime appointmentEndTime;

	@Column(name = "visit_status", columnDefinition = "int default 0 check (visit_status in (0,1))")
	Integer visitStatus;
	
	@ManyToOne
	@JoinColumn(name = "doctor_id",referencedColumnName = "id")//referenced
	private Doctor doctor;

	@ManyToOne
	@JoinColumn(name = "patient_id",referencedColumnName = "id")
	private Patient patient;

}
