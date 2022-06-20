package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService    @Transactional : OFF
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON
     */
    @Test
    void outerTxOff_success(){
        //given
        String username = "outerTxOff_success";
        //when
        memberService.joinV1(username);
        //JUNIT걸 쓰면 assertTrue 라는 게 있다.
        //then: 모든 데이터가 정상 저장된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());

    }
    /**
     * memberService    @Transactional : OFF
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON
     */
    @Test
    void outerTxOff_fail(){
        //given
        String username = "로그예외 outerTxOff_fail";
        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then: Member는 저장이 되지만 Log는 롤백이 된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }
    /**
     * memberService    @Transactional : ON
     * memberRepository @Transactional : OFF
     * logRepository    @Transactional : OFF
     */
    @Test
    void singleTx(){
        //given
        String username = "outerTxOff_success";
        //when
        memberService.joinV1(username);
        //JUNIT걸 쓰면 assertTrue 라는 게 있다.
        //then: 모든 데이터가 정상 저장된다.
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional : ON
     * memberRepository @Transactional : OFF
     * logRepository    @Transactional : OFF
     */
    @Test
    void singleTx_fail(){
        //given
        String username = "로그예외_singleTx_fail";
        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then: 모든 데이터가 Rollback된다.
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }
    /**
     * memberService    @Transactional : ON
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON
     */
    @Test
    void outerTxOn_success(){
        //given
        String username = "outerTxOn_success";
        //when
        memberService.joinV1(username);
        //then
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }
    /**
     * memberService    @Transactional : ON
     * memberRepository @Transactional : ON
     * logRepository    @Transactional : ON
     */
    @Test
    void outerTxOn_fail(){
        //given
        String username = "로그예외_outerTxOn_fail";
        //when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then: 모든 데이터가 Rollback된다.
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }
    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON Exception
     */
    @Test
    void recoverException_fail() {
        //given
        String username = "로그예외_recoverException_fail";
        //when
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);
        //then: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * MemberService    @Transactional:ON
     * MemberRepository @Transactional:ON
     * LogRepository    @Transactional:ON (REQUIRED_NEW) Exception
     */
    @Test
    void recoverException_success() {
        //given
        String username = "로그예외_recoverException_success";
        //when
        memberService.joinV1(username);
        //then: Member는 저장되고 Log는 롤백된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }
}