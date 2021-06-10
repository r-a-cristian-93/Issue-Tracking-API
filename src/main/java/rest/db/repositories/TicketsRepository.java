package rest.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
import rest.db.models.*;
import rest.db.projections.*;

public interface TicketsRepository extends JpaRepository<TicketModel, Integer> {
	<T> List<T> findById(Integer id, Class<T> type);
	
	@Query(value = "SELECT status, COUNT(*) count from tickets WHERE opened_by=?1 GROUP BY status ORDER BY status ASC;", nativeQuery=true)
	List<TicketCountProjection> countTickets(Integer openedBy);

	List<TicketModel> findByOpenedBy(UserModel openedBy);
	List<TicketModel> findByOpenedByAndStatus(UserModel openedBy, StatusModel status);
	
	//for OWNER
	List<TicketModel> findByStatus(StatusModel status);
	List<TicketModel> findByOpenedByDepartment(DepartmentModel department);
	List<TicketModel> findByOpenedByDepartmentAndStatus(DepartmentModel department, StatusModel status);
	List<TicketModel> findBy();

	//for MODERATOR
	List<TicketModel> findByConcernedDepartmentAndStatus(DepartmentModel concernedDep, StatusModel status);
	List<TicketModel> findByConcernedDepartmentAndOpenedByDepartment(DepartmentModel concernedDep, DepartmentModel openedByDep);
	List<TicketModel> findByConcernedDepartmentAndOpenedByDepartmentAndStatus(DepartmentModel concernedDep, DepartmentModel openedByDep, StatusModel status);
	List<TicketModel> findByConcernedDepartment(DepartmentModel department);
	
	//for ADMIN
	List<TicketModel> findByAssignedToAndStatus(UserModel assignedTo, StatusModel status);
	List<TicketModel> findByAssignedToAndOpenedByDepartment(UserModel assignedTo, DepartmentModel department);
	List<TicketModel> findByAssignedToAndOpenedByDepartmentAndStatus(UserModel assignedTo, DepartmentModel department, StatusModel status);
	List<TicketModel> findByAssignedTo(UserModel assignedTo);
}
