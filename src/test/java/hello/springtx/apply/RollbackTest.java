package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class RollbackTest {

    @Autowired RollbackService rollbackService;

    @Test
    void runtimeException(){
        // 테스트 시 빨간색 에러가 뜨지 않도록 예외 체크
        // 롤백 되어야 함. 
        assertThatThrownBy(()-> rollbackService.runtimeException())
                        .isInstanceOf(RuntimeException.class);
//        rollbackService.runtimeException();
    }
    @Test
    void checkedException(){
        //커밋 되어야 함
        assertThatThrownBy(()-> rollbackService.checkedException())
                .isInstanceOf(MyException.class);
    }
    @Test
    void rollbackFor(){
        //롤백 되어야 함
        assertThatThrownBy(()-> rollbackService.rollbackFor())
                .isInstanceOf(MyException.class);    }

    @TestConfiguration
    static class RollbackTestConfig {
        @Bean
        RollbackService rollbackService(){
            return new RollbackService();
        }
    }

    @Slf4j
    static class RollbackService {

        //런타임 예외 발생: 롤백
        @Transactional
        public void runtimeException(){
            log.info("call runtimeException ");
            throw new RuntimeException();
        }
        //체크 예외 발생: 커밋
        //체크 예외이므로 잡거나 던져야 함. 이건 던지자.
        @Transactional
        public void checkedException() throws MyException{
            log.info("call checkedException");
            throw new MyException();
        }
        //체크 예외 rollbackFor 지정: 롤백
        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException{
            log.info("call checkedException");
            throw new MyException();
        }
    }
    static class MyException extends Exception {

    }
}
