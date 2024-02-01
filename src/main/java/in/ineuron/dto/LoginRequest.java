package in.ineuron.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.validation.constraints.*;

@Getter
@ToString
@NoArgsConstructor
public class LoginRequest {

	@NotBlank(message = "Email is required, please enter email")
	@Email(regexp = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$",
	message="invalid email!")
	private String email;


	@NotBlank(message = "Password should not be empty or null")
    @Pattern(regexp = "^(?!.*\\s).*$",
             message = "Invalid password")
	private String password;


	public void setEmail(String email) {
		this.email = email.trim();
	}
	
	public void setPassword(String password) {
		this.password = password.trim();
	}
	
	
}
