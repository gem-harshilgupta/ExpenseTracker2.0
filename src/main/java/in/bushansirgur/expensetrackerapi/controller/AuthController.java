package in.bushansirgur.expensetrackerapi.controller;

import in.bushansirgur.expensetrackerapi.dto.JWTResponse;
import in.bushansirgur.expensetrackerapi.dto.LoginModel;
import in.bushansirgur.expensetrackerapi.entity.User;
import in.bushansirgur.expensetrackerapi.dto.UserModel;
import in.bushansirgur.expensetrackerapi.service.CustomUserDetailService;
import in.bushansirgur.expensetrackerapi.service.UserService;
import in.bushansirgur.expensetrackerapi.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody LoginModel loginModel) throws Exception {
        authenticate(loginModel.getEmail(),loginModel.getPassword());
        // We need to generate JWT Token
        final UserDetails userDetails= customUserDetailService.loadUserByUsername(loginModel.getEmail());
        final String jwtToken= jwtTokenUtil.generateToken(userDetails);
        return new ResponseEntity<>(new JWTResponse(jwtToken),HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserModel user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
    }

    private void authenticate(String email,String password) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
        }
        catch (DisabledException e){
            throw new Exception("User disabled");
        }
        catch (BadCredentialsException e){
            throw new Exception("Bad Credentials");
        }
    }


}
