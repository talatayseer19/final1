package jpa.finalproject.tala.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "patient", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }) })
@Data
public class Patient {
	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@Column(name = "age", columnDefinition = "int Check (age > 0 and age < 100)")
	private Integer age;

	@Column(name = "gender", columnDefinition = "VARCHAR(6) CHECK (gender IN ('Female', 'Male')",nullable = false)
	private String gender;
	
	@JsonIgnore
	@OneToMany(mappedBy = "patient")
	private Set <Appointment> appointment;
}
