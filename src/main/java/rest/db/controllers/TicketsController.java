package rest.db.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import lombok.*;
import rest.db.models.*;
import rest.db.projections.*;
import rest.db.repositories.*;
import rest.util.Context;

import static rest.ApplicationConstants.*;

@RestController
@AllArgsConstructor
@RequestMapping("tickets")
public class TicketsController {
	private final EntityManager entityManager;
	private final TicketsRepository ticketsRepo;
	private final DepartmentsRepository departmentsRepo;
	private final UsersRepository usersRepo;
	private final StatusRepository statusRepo;
	
	@ResponseBody
	@GetMapping("/manage")
	public List<TicketModel> getTickets(String status, String department) {
		String userRole = Context.getUser().getRole().getValue();
		switch(userRole) {
			case ROLE_OWNER: return ownerManagedTickets(status, department);
			case ROLE_MODERATOR: return moderatorManagedTickets(status, department);
			case ROLE_ADMIN: return adminManagedTickets(status, department);
			default: return null;
		}
	}

	public List<TicketModel> ownerManagedTickets(String status, String department) {
		StatusModel statusModel = StatusModel.getInstance(status);
		DepartmentModel departmentModel = DepartmentModel.getInstance(department);
		switch (decideCase(status, department)) {
			case 1:	return ticketsRepo.findByStatus(statusModel);
			case 2: return ticketsRepo.findByOpenedByDepartment(departmentModel);
			case 3: return ticketsRepo.findByOpenedByDepartmentAndStatus(departmentModel, statusModel);
			case 4: 
			default: return ticketsRepo.findBy();
		}
	}

	public List<TicketModel> moderatorManagedTickets(String status, String department) {
		DepartmentModel concernedDepartment= Context.getUser().getDepartment();		
		StatusModel statusModel = StatusModel.getInstance(status);
		DepartmentModel departmentModel = DepartmentModel.getInstance(department);
		switch (decideCase(status, department)) {
			case 1:	return ticketsRepo.findByConcernedDepartmentAndStatus(concernedDepartment, statusModel);
			case 2: return ticketsRepo.findByConcernedDepartmentAndOpenedByDepartment(concernedDepartment, departmentModel);
			case 3: return ticketsRepo.findByConcernedDepartmentAndOpenedByDepartmentAndStatus(concernedDepartment, departmentModel, statusModel);
			case 4: 
			default: return ticketsRepo.findByConcernedDepartment(concernedDepartment);
		}
	}
	
	public List<TicketModel> adminManagedTickets(String status, String department) {
		UserModel assignedTo = Context.getUser();		
		StatusModel statusModel = StatusModel.getInstance(status);
		DepartmentModel departmentModel = DepartmentModel.getInstance(department);
		switch (decideCase(status, department)) {
			case 1:	return ticketsRepo.findByAssignedToAndStatus(assignedTo, statusModel);
			case 2: return ticketsRepo.findByAssignedToAndOpenedByDepartment(assignedTo, departmentModel);
			case 3: return ticketsRepo.findByAssignedToAndOpenedByDepartmentAndStatus(assignedTo, departmentModel, statusModel);
			case 4: 
			default: return ticketsRepo.findByAssignedTo(assignedTo);
		}
	}	
	
	@ResponseBody
	@GetMapping("/count")
	public List<TicketCountProjection> countTickets() {
		return ticketsRepo.countTickets(Context.getUser().getId());		
	}
		
	@ResponseBody
	@GetMapping("/mytickets") 
	public List<TicketModel> getMyTickets(String status) {		
		UserModel userModel = Context.getUser();
		if(status!=null) {
			StatusModel statusModel = StatusModel.getInstance(status);
			return ticketsRepo.findByOpenedByAndStatus(userModel, statusModel);
		} 
		else return ticketsRepo.findByOpenedBy(userModel);
	}
		
	@ResponseBody
	@PostMapping("/add")
	public void insertTicket(@RequestBody TicketModel ticket) {
		UserModel openedBy = Context.getUser();
		ticket.setOpenedBy(openedBy);			
		ticketsRepo.save(ticket);
	}	
	
	@PutMapping("/{id}/update")
	@PreAuthorize("hasAnyAuthority(T(rest.ApplicationConstants).ROLE_MODERATOR, T(rest.ApplicationConstants).ROLE_OWNER)")
	public TicketModel assignTo(
				@PathVariable Integer id,
				@RequestBody UserModel assignTo) {
		StatusModel statusModel = StatusModel.getInstance("Pending");
		TicketModel ticket = ticketsRepo.getOne(id);
		assignTo = usersRepo.findById(assignTo.getId()).get();
		ticket.setAssignedTo(assignTo);
		ticket.setStatus(statusModel);
		ticket.setClosedBy(null);
		return ticketsRepo.save(ticket);
	}
	
	@ResponseBody
	@PutMapping("/{id}/close")
	@PreAuthorize("hasAnyAuthority(T(rest.ApplicationConstants).ROLE_ADMIN, T(rest.ApplicationConstants).ROLE_MODERATOR, T(rest.ApplicationConstants).ROLE_OWNER)")
	public TicketModel closeTicket(
				@PathVariable Integer id,
				@RequestBody StatusModel status) {
		UserModel closedBy = Context.getUser();
		TicketModel ticket = ticketsRepo.getOne(id);
		ticket.setClosedBy(closedBy);
		ticket.setStatus(status);
		return ticketsRepo.save(ticket);
	}
	
	@ResponseBody
	@DeleteMapping("/{id}/delete")
	@PreAuthorize("hasAuthority(T(rest.ApplicationConstants).ROLE_OWNER)")
	public void deleteTicket(@PathVariable Integer id) {
		TicketModel ticket = ticketsRepo.getOne(id);
		ticketsRepo.delete(ticket);
	}
	
	private int decideCase(Object A, Object B) {
		if (A!=null && B==null) return 1;	//A
		if (A==null && B!=null) return 2;	//B
		if (A!=null && B!=null) return 3;	//both
		return 4;							//none				
	}
}
