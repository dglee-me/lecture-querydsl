package kr.co.dglee.lecture.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Hello {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

}
