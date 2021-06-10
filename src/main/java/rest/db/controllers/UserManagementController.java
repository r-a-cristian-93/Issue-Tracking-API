package rest.db.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import rest.db.models.*;
import rest.db.repositories.*;
import rest.db.projections.*;
import java.util.*;
import lombok.*;

@Controller
@AllArgsConstructor
@RequestMapping("usermanagement")
public class UserManagementController {
	private UsersRepository usersRepo;		
	private BCryptPasswordEncoder pwdEncoder;
	
	@ResponseBody
	@PostMapping("/register")
	@PreAuthorize("hasAuthority(T(rest.ApplicationConstants).ROLE_OWNER)")
	public void registerUser(@RequestBody UserModel userModel) {
		userModel.setPassword(pwdEncoder.encode(userModel.getPassword()));
		usersRepo.save(userModel);
	}

	@ResponseBody
	@GetMapping("/myinfo")
	public UserModel getMyInfo() {
		return usersRepo.findByEmail(getUserFromContext().getEmail());
	}
	
	private UserModel getUserFromContext() {
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return usersRepo.findByEmail(email);
	}
}
