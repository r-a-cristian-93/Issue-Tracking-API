package rest.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.beans.BeansException;

import rest.db.repositories.UsersRepository;
import rest.db.models.UserModel;

@Component
public class Context implements ApplicationContextAware {
	private static UsersRepository usersRepo;
	
	private Context() {}
	
	@Override
	public void setApplicationContext(ApplicationContext appContext) throws BeansException {		
		usersRepo = appContext.getBean(UsersRepository.class);
	}
	
	public static UserModel getUser() {		
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return usersRepo.findByEmail(email);
	}
}
