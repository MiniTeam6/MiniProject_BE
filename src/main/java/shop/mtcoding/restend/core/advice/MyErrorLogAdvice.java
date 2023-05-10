//package shop.minostreet.shoppingmall.handler.aop;
//
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import shop.minostreet.shoppingmall.config.auth.LoginUser;
//import shop.minostreet.shoppingmall.domain.ErrorLog;
//import shop.minostreet.shoppingmall.domain.User;
//import shop.minostreet.shoppingmall.handler.exception.MyValidationException;
//import shop.minostreet.shoppingmall.repository.ErrorLogRepository;
//
//import javax.servlet.http.HttpSession;
//import java.util.HashMap;
//import java.util.Map;
//@RequiredArgsConstructor
//@Aspect
////Aspect = PointCut + Advice
//@Component
//public class MyErrorLogAdvice {
//    private final Logger log = LoggerFactory.getLogger(getClass());
//    private final HttpSession session;
//
//    private final ErrorLogRepository errorLogRepository;
//    @Pointcut("@annotation(shop.minostreet.shoppingmall.handler.annotation.MyErrorLogRecord)")
//    public void myErrorLog(){}
//
//    @Before("myErrorLog()")
//    public void errorLogAdvice(JoinPoint jp) throws HttpMessageNotReadableException {
//        log.debug("디버그 : errorLogAdvice 호출됨");
//        Object[] args = jp.getArgs();
//
//        for (Object arg : args) {
//            //매개변수를 돌면서 Exception이 존재하는지 체크한다.
//            //: Exception의 자식까지 모두 확인
//            if(arg instanceof Exception){
//                Exception e = (Exception) arg;
//
//                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//                    User userDetails = (User) authentication.getPrincipal();
//                    LoginUser loginUser = new LoginUser(userDetails); // UserDetails 객체를 LoginUser 객체로 변환합니다.
//
//                    ErrorLog errorLog = ErrorLog.builder()
//                            .userId(loginUser.getUser().getId())
//                            .msg(e.getMessage())
//                            .build();
//                    errorLogRepository.save(errorLog);
//                }
//
////                Authentication authentication=(Authentication) SecurityContextHolder.getContext().getAuthentication();
////                if(authentication != null){
////                LoginUser loginUser = (LoginUser)authentication.getPrincipal();
////
//////                LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
////
////                    ErrorLog errorLog =ErrorLog.builder().userId(loginUser.getUser().getId()).msg(e.getMessage()).build();
////                    //에러 로그의 아이디, 에러 로그 메시지를 전달해 객체 생성
////                    errorLogRepository.save(errorLog);
////                }
//            }
//        }
//    }
//}
///**
// * 유효성 검사
// * get, delete, post, put에서 body가 존재하는 post, put만 존재
// */