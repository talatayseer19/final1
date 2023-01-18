package jpa.finalproject.tala.result.info;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Result {
	private String StatusCode; //0 or 1
	private String StatusDescription; //Successful or contain error
	private List <Object> systemResult;

}
