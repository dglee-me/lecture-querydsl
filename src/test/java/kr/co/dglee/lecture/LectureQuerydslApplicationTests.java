package kr.co.dglee.lecture;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.co.dglee.lecture.entity.Hello;
import kr.co.dglee.lecture.entity.QHello;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LectureQuerydslApplicationTests {

  @Autowired
  EntityManager em;

  @Test
  void contextLoads() {

    Hello hello = new Hello();
    em.persist(hello);

    Hello result = new JPAQueryFactory(em)
        .selectFrom(QHello.hello)
        .fetchOne();

    Assertions.assertEquals(result, hello);
    Assertions.assertEquals(result.getId(), hello.getId());
  }
}
