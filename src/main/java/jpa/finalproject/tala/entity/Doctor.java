package jpa.finalproject.tala.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doctor")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "name", nullable = false,unique = true)
	private String name;

	@Column(name = "national_id", unique = true)
	private String nationalId;

	@Column(name = "specialty", nullable = false)
	private String specialty;

	@Column(name = "phone_number", nullable = false)
	private String phoneNumber;

	@JsonIgnore
	@OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL) // , orphanRemoval = true
	private Set<Appointment> appointment;

}
