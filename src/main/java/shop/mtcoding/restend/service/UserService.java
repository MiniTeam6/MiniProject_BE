package shop.mtcoding.restend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import shop.mtcoding.restend.core.annotation.MyLog;
import shop.mtcoding.restend.core.auth.jwt.MyJwtProvider;
import shop.mtcoding.restend.core.auth.session.MyUserDetails;
import shop.mtcoding.restend.core.exception.Exception400;
import shop.mtcoding.restend.core.exception.Exception401;
import shop.mtcoding.restend.core.exception.Exception404;
import shop.mtcoding.restend.core.exception.Exception500;
import shop.mtcoding.restend.dto.ResponseDTO;
import shop.mtcoding.restend.dto.user.UserRequest;
import shop.mtcoding.restend.dto.user.UserResponse;
import shop.mtcoding.restend.model.user.User;
import shop.mtcoding.restend.model.user.UserRepository;
import shop.mtcoding.restend.model.user.UserRole;

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
    public UserResponse.SignupResponseDTO 회원가입(UserRequest.SignupRequestDTO signupRequestDTO, MultipartFile image){

        // 이미지 S3 에 저장
        // 썸네일 생성
        String imageUri = "imageUri";
        String thumbnailUri = "thumbnailUri";

        Optional<User> userOP =userRepository.findByEmail(signupRequestDTO.getEmail());
        if(userOP.isPresent()){
            // 이 부분이 try catch 안에 있으면 Exception500에게 제어권을 뺏긴다.
            throw new Exception400("email", "이메일이 존재합니다");
        }
        String encPassword = passwordEncoder.encode(signupRequestDTO.getPassword()); // 60Byte
        signupRequestDTO.setPassword(encPassword);
        System.out.println("encPassword : "+encPassword);

        // 디비 save 되는 쪽만 try catch로 처리하자.
        try {
            User userPS = userRepository.save(signupRequestDTO.toEntity(imageUri, thumbnailUri));
            return new UserResponse.SignupResponseDTO(userPS);
        }catch (Exception e){
            throw new Exception500("회원가입 실패 : "+e.getMessage());
        }
    }

    @MyLog
    public Object[] 로그인(UserRequest.LoginRequestDTO loginRequestDTO) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                    = new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();

            Object[] result = new Object[2];
            result[0] = new UserResponse.loginResponseDTO(myUserDetails.getUser());
            result[1] = MyJwtProvider.create(myUserDetails.getUser());
            return result;

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
                    .map(user->new UserResponse.UserListOutDTO(user))
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
        List<User>users=userRepository.findUsersByStatus(true);
        List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                .map(user->new UserResponse.UserListOutDTO(user))
                .collect(Collectors.toList());
        return userDTOs;
    }
    @Transactional
    public UserResponse.DetailOutDTO 권한업데이트(UserRequest.RoleUpdateInDTO roleUpdateInDTO){
        Optional<User> user = userRepository.findByEmail(roleUpdateInDTO.getEmail());
        if(user.isEmpty()){
            throw new Exception404(roleUpdateInDTO.getEmail()+"  User를 찾을 수 없습니다. ");
        }

        user.get().setRole(UserRole.valueOf(roleUpdateInDTO.getRole()));
        try{
            User userPS=userRepository.save(user.get());
            return new UserResponse.DetailOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500(e+roleUpdateInDTO.getEmail()+"유저권한 업데이트 실패");
        }
    }

    @Transactional
    public UserResponse.StatusUpdateOutDTO 회원가입승인(UserRequest.StatusUpdateInDTO statusUpdateInDTO){
        Optional<User> user = userRepository.findByEmail(statusUpdateInDTO.getEmail());
        if(user.isEmpty()){
            throw new Exception404(statusUpdateInDTO.getEmail()+"  User를 찾을 수 없습니다. ");
        }
        user.get().setStatus(true);
        try{
            User userPS=userRepository.save(user.get());
            return new UserResponse.StatusUpdateOutDTO(userPS);
        }catch (Exception e){
            throw new Exception500(e+statusUpdateInDTO.getEmail()+"유저권한 업데이트 실패");
        }
    }

    public List<UserResponse.UserListOutDTO> 회원가입요청목록(){
        List<User>users=userRepository.findUsersByStatus(false);
        List<UserResponse.UserListOutDTO> userDTOs = users.stream()
                .map(user->new UserResponse.UserListOutDTO(user))
                .collect(Collectors.toList());
        return userDTOs;
    }




}
