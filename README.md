# Spring Security Boilerplate

## Session Auth

### Troubleshooting

#### SessionCreationPolicy.IF_REQUIRED VS SessionCreationPolicy.STATELESS

스프링 시큐리티에서 기본 세션 생성 정책은 `SessionCreationPolicy.IF_REQUIRED`다. `SessionCreationPolicy.IF_REQUIRED` 관련 [문서](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/http/SessionCreationPolicy.html)를 살펴보면 "Spring Security will only create an HttpSession if required", 즉 시큐리티가 필요시 `HttpSession`을 생성하는 것을 알 수 있다.  해당 설명이 매우 모호하다고 느껴졌고, 서비스 시 불필요한 부분에서 세션을 생성할 것으로 생각했다.

확인 결과, 예외 처리 미흡으로 5xx 에러가 발생하나, 시큐리티로 인해 403으로 전달될 때 불필요한 세션 생성과 클라이언트에게 세션 쿠키가 전달되는 것을 확인했다. 이처럼 세션 생성 흐름이 알기 힘든 `SessionCreationPolicy.IF_REQUIRED`보다 `SessionCreationPolicy.STATELESS`를 사용해 세션을 직접 관리해 주는 것이 더 좋다고 판단했고, 보일러플레이트 코드에 적용했다.