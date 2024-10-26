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
