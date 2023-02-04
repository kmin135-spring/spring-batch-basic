# 개요

* jdk 11
* 강의에 맞춰 spring 2.x 를 사용하므로 spring-batch도 4.x 임
  * spring 3.x 부터는 JDK 17을 써야하고 spring-batch도 5.x로 api에 많은 변화가 있으니 참고
    * ex. JobBuilderFactory, StepBuilderFactory 가 모두 deprecated 됨
* https://docs.spring.io/spring-batch/docs/4.3.7/reference/html/

# 실행

* 프로그램 아규먼트로 job 이름(,로 여러개 가능)과 필요할 경우 파라미터 전달
  * `--spring.batch.job.names=conditionalStepJob -fileName=test.csv`

# 스케줄링
 
* 배치 자체에서 스케줄링 기능을 제공하진 않음
* 아래의 방법들을 검토할 수 있음
  * 스프링에서 제공하는 스케줄링 기능을 이용하거나 `@EnableScheduling`, `@Scheduled(cron = "0 */1 * * * *")`
    * 배치는 보통 오래걸리고 무거운 작업이 많은데 스케줄링까지 코드에 포함되어 있으면 번거로울 것 같음
    * 코드수정없이 스케줄링 정책을 조정할 수 있는 다른 방법들이 좋을 듯
  * OS의 cron+script
  * k8s의 크론잡
  * 젠킨스같은 CI도구
  * 쿼츠 같은 전문 스케줄링 라이브러리
  * spring cloud data flow