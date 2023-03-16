package in.bushansirgur.expensetrackerapi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.bushansirgur.expensetrackerapi.entity.User;
import in.bushansirgur.expensetrackerapi.dto.UserModel;
import in.bushansirgur.expensetrackerapi.exceptions.ItemExistsException;
import in.bushansirgur.expensetrackerapi.exceptions.ResourceNotFoundException;
import in.bushansirgur.expensetrackerapi.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private PasswordEncoder bcryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User createUser(UserModel user) {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new ItemExistsException("User is already register with email:"+user.getEmail());
		}
		User newUser = new User();
		BeanUtils.copyProperties(user, newUser);
		newUser.setPassword(bcryptPasswordEncoder.encode(newUser.getPassword()));
		return userRepository.save(newUser);
	}

	@Override
	public User readUser() {
		return userRepository.findById(getLoggedInUser().getId()).orElseThrow(() -> new ResourceNotFoundException("User not found the user:"));
	}

	@Override
	public User updateUser(UserModel user) {
		User existingUser = readUser();
		existingUser.setName(user.getName() != null ? user.getName() : existingUser.getName());
		existingUser.setEmail(user.getEmail() != null ? user.getEmail() : existingUser.getEmail());
		existingUser.setPassword(user.getPassword() != null ? bcryptPasswordEncoder.encode(user.getPassword()) : existingUser.getPassword());
		existingUser.setAge(user.getAge() != null ? user.getAge() : existingUser.getAge());
		return userRepository.save(existingUser);
	}

	@Override
	public void deleteUser() {
		User existingUser = readUser();
		userRepository.delete(existingUser);
	}

	@Override
	public User getLoggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Invalid User"));
	}

}

























