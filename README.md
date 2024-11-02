# Spring Security Boilerplate

## Session Auth

### Troubleshooting

#### SessionCreationPolicy.IF_REQUIRED VS SessionCreationPolicy.STATELESS

스프링 시큐리티에서 기본 세션 생성 정책은 `SessionCreationPolicy.IF_REQUIRED`다. `SessionCreationPolicy.IF_REQUIRED` 관련 [문서](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/http/SessionCreationPolicy.html)를 살펴보면 "Spring Security will only create an HttpSession if required", 즉 시큐리티가 필요시 `HttpSession`을 생성하는 것을 알 수 있다.  해당 설명이 매우 모호하다고 느껴졌고, 서비스 시 불필요한 부분에서 세션을 생성할 것으로 생각했다.

확인 결과, 예외 처리 미흡으로 5xx 에러가 발생하나, 시큐리티로 인해 403으로 전달될 때 불필요한 세션 생성과 클라이언트에게 세션 쿠키가 전달되는 것을 확인했다. 이처럼 세션 생성 흐름이 알기 힘든 `SessionCreationPolicy.IF_REQUIRED`보다 `SessionCreationPolicy.STATELESS`를 사용해 세션을 직접 관리해 주는 것이 더 좋다고 판단했고, 보일러플레이트 코드에 적용했다.

#### 서로 다른 세션 아이디

로그인 후 세션을 받고, 인증이 필요한 엔드포인트에 접근하면 새로운 세션이 접근하는 것을 확인했다. 디버깅 과정을 통해 `SessionAuthorizationFilter`에서 `SecurityContextHolder`에 `Authentication` 구현체를 담으면 새로운 세션이 생성되는 것을 확인했다. `SecurityConfig`의 `securityFilterChain`에 `sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::none)`을 추가함으로써 위 문제를 해결하려 했다.

`sessionFixation()`에 대한 정보를 탐색 중 `sessionFixation()`을 비활성화(`none`)하는 것은 세션 고정 공격을 당할 수 있기 때문에 좋지 않은 선택이라고 알게 되었고, 보일러플레이트 코드에 기본값을 적용했다.

#### 세션에 사용자 권한 저장 고민

세션에 Long 타입의 사용자 아이디뿐만 아니라 사용자 권한 또한 저장할지를 두고 고민했다.

사용자 아이디만 저장할 경우, 인증이 필요한 요청마다 데이터베이스에서 사용자 아이디를 기반으로 권한을 조회해야 한다. 사용자 권한도 저장한다면 데이터베이스 조회 없이 세션 내에서 직접 권한을 확인할 수 있다. 다만, 권한이 변경될 경우 해당 사용자와 관련된 세션을 찾아 권한을 갱신해야 한다.

권한을 데이터베이스에 저장하고, 세션을 인-메모리에 저장한다고 가정할 때, 세션을 통해 권한을 조회하는 것이 더 빠를 것이고, Long 타입의 사용자 아이디를 저장할 때보다 조금 더 많은 메모리를 사용할 것이다. 인증을 요구하는 API의 호출 빈도가 높고, 권한 변경은 상대적으로 드물다고 판단했기 때문에, 권한을 세션에 저장하는 방식이 서버 부하를 줄이는 데 유리할 것으로 생각한다.

##### 세션의 사용자 권한 변경에 대한 추가 고민

현재 코드의 경우 톰캣 내장 메모리에 세션을 저장한다. 조금 더 자세하게 말하면 ManagerBase 클래스에 `ConcurrentHashMap<>()`으로 세션 객체를 저장한다. 톰캣 내장 메모리 세션 저장소는 모든 세션을 조회하거나 특정 사용자와 관련된 세션을 조회하는 기능을 제공하지 않는 것을 확인했다. 즉, 사용자의 권한이 변경되었을 때 세션의 권한을 변경할 수 없다. 다른 방법이 필요하다.

#### AuthenticationProvider, Filter 빈 등록

`AuthenticationProvider`와 `Filter` 사용에서 `new` 키워드 그리고 스프링 컨테이너에 등록해 사용할지 고민이 있었다. 스프링 컨테이너에 포함된 객체를 주입 받는다면 의존성 관리와 생명주기를 이유로 `AuthenticationProvider`, `Filter` 또한 스프링 컨테이너로 관리하는 것이 좋겠다고 생각했다.

`AuthenticationProvider`는 스프링 컨테이너에서 관리되는 `UserDetailsService`를 사용하므로 `AuthenticationProvider` 또한 스프링 컨테이너로 관리하는 것이 바람직하다고 생각해 `@Component` 어노테이션으로 등록했다. 이를 통해 `SecurityConfig`에 `UserDetailsService`를 주입하지 않게 되었다. 다만 `SessionAuthenticationFilter`는 `AuthenticationManager`를 주입 받으나 순환 참조가 발생해 `new` 키워드로 등록했다.

#### 다중 세션 제어 문제 발생

스프링에서 다음과 같이 설정해 다중 세션을 설정할 수 있다.

```java

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
        httpSecurity
                // 필요 시 cors 설정
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .maximumSessions(1)
                                .sessionRegistry(new SessionRegistryImpl())) // 생략 가능
    // 생략
```

문제가 발생했다, 세션 생성 정책을 `SessionCreationPolicy.STATELESS`로 설정하고, maximumSessions()을 설정하면 정상적으로 동작하는 것을 확인했지만, 다중 세션 제어가 인증 필터보다 후순위에서 이루어져 원하는 동작이 발생하지 않는다. 다중 세션에 대한 처리는 `SessionManagementFilter`에서 이루어진다. A에서 로그인하고, B에서 로그인할 때 A에서 인증이 필요한 작업 시 바로 401을 반환해야 하지만, 컨트롤러 진입 없이 200 응답 후 다음 응답에서 403을 응답한다. "세션에 사용자 권한 저장 고민"에 대한 고민과 더불어 세션에 무엇을 저장할지, 다중 세션을 어떻게 관리할지 고민이 더 필요하다. 별도의 설정을 하지 않았다면 다중 세션 제어는 `ConcurrentSessionControlAuthenticationStrategy` 클래스의 `allowableSessionsExceeded()` 메서드에서 이루어진다.

## Reference

- [스프링 시큐리티 인 액션](https://product.kyobobook.co.kr/detail/S000061695014)
