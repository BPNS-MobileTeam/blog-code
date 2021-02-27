package com.jojoldu.blogcode.entityql.entity.domain.academy;

/**
 * Created by jojoldu@gmail.com on 2018-12-28
 * Blog : http://jojoldu.tistory.com
 * Github : https://github.com/jojoldu
 */

import com.jojoldu.blogcode.querydsl.domain.BaseTimeEntity;
import com.jojoldu.blogcode.querydsl.domain.student.Student;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "academy")
public class Academy extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "match_key")
    private String matchKey;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column
    private String phoneNumber;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "academy")
    private List<Student> students = new ArrayList<>();

    public Academy(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Academy(Long id, String matchKey) {
        this.id = id;
        this.matchKey = matchKey;
    }

    @Builder
    public Academy(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public void addStudent(List<Student> students) {
        for (Student student : students) {
            addStudent(student);
        }
    }

    public void addStudent(Student student) {
        this.students.add(student);
        student.setAcademy(this);
    }

    public void setMatchKey(String matchKey) {
        this.matchKey = matchKey;
    }
}
