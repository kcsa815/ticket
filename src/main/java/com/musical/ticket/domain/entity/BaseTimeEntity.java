package com.musical.ticket.domain.entity;
//공통 시간 필드(대부분 테이블이 created_at, updated_at 필드를 가짐. 이를 분리하면 코드중복을 줄일 수 있음.

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass //이 클래스를 상속하는 엔티티들이 아래 필드들을 컬럼으로 갖도록 함.
@EntityListeners(AuditingEntityListener.class) //jpa auditiong : 엔티티의 생성, 수정시점의 타임스탬프와 사용자 정보를 자동으로 기록하는 기능
public abstract class BaseTimeEntity {
    
    @CreatedDate //엔티티 생성되어 저장될 때 시간이 자동으로 저장됨
    private LocalDateTime createdAt; //생성 시간

    @LastModifiedDate //엔티티 값이 변경될 떄 시간이 자동으로 저장됨
    private LocalDateTime updatedAt; //수정 시간
}
