package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.mtcoding.restend.core.annotation.MyLog;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.core.exception.Exception401;
import shop.mtcoding.restend.core.exception.Exception500;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @MyLog
    @Transactional
    public UserResponse.JoinOutDTO 회원가입(UserRequest.JoinInDTO joinInDTO){
        Optional<User> userOP =userRepository.findByEmail(joinInDTO.getEmail());
        if(userOP.isPresent()){
            // 이 부분이 try catch 안에 있으면 Exception500에게 제어권을 뺏긴다.
            throw new Exception400("email", "이메일이 존재합니다");
        }
        String encPassword = passwordEncoder.encode(joinInDTO.getPassword()); // 60Byte
        joinInDTO.setPassword(encPassword);
        System.out.println("encPassword : "+encPassword);

        // 디비 save 되는 쪽만 try catch로 처리하자.
        try {
            User userPS = userRepository.save(joinInDTO.toEntity());
            return new UserResponse.JoinOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500("회원가입 실패 : "+e.getMessage());
        }
    }

    @MyLog
    public String 로그인(UserRequest.LoginInDTO loginInDTO) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginInDTO.getEmail(), loginInDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
            return MyJwtProvider.create(myUserDetails.getUser());
        }catch (Exception e){
            throw new Exception401("인증되지 않았습니다");
        }
    }

    @MyLog
    public UserResponse.DetailOutDTO 회원상세보기(Long id) {
        User userPS = userRepository.findById(id).orElseThrow(
                ()-> new Exception400("id", "해당 유저를 찾을 수 없습니다")

        );
        return new UserResponse.DetailOutDTO(userPS);
    }


    public List<UserResponse.UserListOutDTO> 회원리스트검색(UserRequest.SearchInDTO searchInDTO){
        if(searchInDTO.getSearchType().equals("name")){
            List<User> users = userRepository.findByUsernameContaining(searchInDTO.getKeyword());
            List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                    .map(UserResponse.UserListOutDTO::new)
                    .collect(Collectors.toList());

            return userDTOs;
        }else{
            List<User> users = userRepository.findByEmailContaining(searchInDTO.getKeyword());
            List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                    .map(UserResponse.UserListOutDTO::new)
                    .collect(Collectors.toList());
            return userDTOs;
        }
    }

    public List<UserResponse.UserListOutDTO> 회원전체리스트(){
        List<User> users=userRepository.findAll();
        List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                .map(UserResponse.UserListOutDTO::new)
                .collect(Collectors.toList());
        return userDTOs;
    }




}
