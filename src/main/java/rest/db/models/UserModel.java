package rest.db.models;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.*;
import rest.db.models.*;

@Entity
@Table(name = "users")
@Getter @Setter
@DynamicInsert
@DynamicUpdate
public class UserModel{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private Integer id;
	@Column(name="email")
	private String email;
	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(name="password")
	private String password;
	@Column(name="lastname")
	private String lastname;
	@Column(name="firstname")
	private String firstname;
	@ManyToOne
	@JoinColumn(name="department")
	private DepartmentModel department;
	@ManyToOne
	@JoinColumn(name="role")
	private RoleModel role;
	
	protected UserModel() {};
	
	public static UserModel getInstance() {
		return new UserModel();
	}
}
