package com.core.example.singleton;

import com.core.example.AppConfig;
import com.core.example.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class SingletonTest {

    /**
     * 스프링 없는 순수한 DI 컨테이너인 AppConfig는 요청을 할 때 마다 객체를 새로 생성한다.
     * 고객 트래픽이 초당 100이 나온다고 가정했을 때 초당 100개 객체가 생성되고 소멸된다. -> 메모리 낭비가 심하다.
     * 해결방안은 해당 객체가 딱 1개만 생성되고, 공유하도록 설계하면 된다. -> 싱글톤 패턴
     */
    @Test
    @DisplayName("스프링 없는 순수한 DI 컨테이너")
    void pureContainer() {
        AppConfig appConfig = new AppConfig();

        //1. 조회: 호출할 때 마다 객체를 생성
        MemberService memberService1 = appConfig.memberService();

        //2. 조회: 호출할 때 마다 객체를 생성
        MemberService memberService2 = appConfig.memberService();


        // 참조값이 다른 것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);

        assertThat(memberService1).isNotSameAs(memberService2);
    }

    @Test
    @DisplayName("싱글톤 패턴을 적용한 객체 사용")
    void singletonServiceTest() {
        SingletonService singletonService1 = SingletonService.getInstance();
        SingletonService singletonService2 = SingletonService.getInstance();

        System.out.println("singletonService1 = " + singletonService1);
        System.out.println("singletonService2 = " + singletonService2);

        assertThat(singletonService1).isSameAs(singletonService2);
    }

    @Test
    @DisplayName("스프링 컨테이너와 싱글톤")
    void springContainer() {

        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        //1. 조회: 호출할 때 마다 객체를 생성
        MemberService memberService1 = ac.getBean("memberService", MemberService.class);

        //2. 조회: 호출할 때 마다 객체를 생성
        MemberService memberService2 = ac.getBean("memberService", MemberService.class);


        // 참조값이 다른 것을 확인
        System.out.println("memberService1 = " + memberService1);
        System.out.println("memberService2 = " + memberService2);

        assertThat(memberService1).isSameAs(memberService2);

        /**
         * 위의 방식은 싱글톤 방식이다.
         * 스프링의 빈 등록 방식은 기본적으로 싱글톤 방식이다.
         * 그러나 싱글톤 방식만 지원하는 것이 아니다. 요청할 때마다 새로운 객체를 생성해서 반환하는 기능도 제공한다.
         *
         * 싱글톤 방식의 주의점
         *  1. 싱글톤 패턴이든, 스프링 같은 싱글톤 컨테이더를 사용하든, 객체 인스턴스를 하나만 사용해서 공유하는 싱글톤 방식
         *      여러 클라이언트가 객체 인스턴스를 공유하기때문에 상태를 유지(stateful)하게 설계하면 안된다.
         *  2. 무상태(stateless)로 설계해야 한다.
         *      * 특정 클라이언트에 의존적인 필드가 있으면 안된다.
         *      * 특정 클라이언트가 값을 변경할 수 있는 필드가 있으면 안된다.
         *      * 가급적 읽기만 가능해야 한다.
         *      * 필드 대신에 자바에서 공유되지 않는, 지역변수, 파라미터, ThreadLocal 등을 사용해야 한다.
         *  3. 스프링 빈의 필드에 공유 값을 설정하면 정말 큰 장애가 발생할 수 있다.
         */
    }
}
