package com.sparta.myselectshop.entity;

import java.time.LocalDate;

public class Book {

    // 필수 멤버 필드
    private String title;
    private String author;

    // 선택 멤버 필드
    private String color;
    private int pages;
    private LocalDate publicationDate;
    private String weight;

    // 1. 클래스 안에 static 멤버 클래스로 Builder 클래스를 만들어 줍니다.
    public static class Builder {

        // 2. Builder 클래스는 생성할 클래스의 멤버 필드를 똑같이 가집니다.
        private String title;
        private String author;

        // 3. Builder 클래스의 선택 멤버 필드를 기본값으로 초기화합니다.
        private String color = "white";
        private int pages = 0;
        private LocalDate publicationDate = LocalDate.now();
        private String weight = "0kg";

        // 4. Builder 클래스에 필수 멤버 필드를 매개변수로 하는 생성자를 선언합니다.
        public Builder(String title, String author) {
            this.title = title;
            this.author = author;
        }

        // 5. 선택 매개변수의 값을 저장하고 Builder 객체를 반환하는 메서드를 만듭니다.
        public Builder color(String color) {
            this.color = color;

            return this;
        }

        public Builder pages(int pages) {
            this.pages = pages;

            return this;
        }

        public Builder publicationDate(LocalDate publicationDate) {
            this.publicationDate = publicationDate;

            return this;
        }

        public Builder weight(String weight) {
            this.weight = weight;

            return this;
        }

        // 6. 생성할 클래스의 객체를 반환해주는 builder 메서드를 만듭니다.
        public Book build() {
            return new Book(this);
        }
    }

    // 7. 생성할 클래스의 생성자로 Builder 객체를 받는 생성자를 만듭니다.
    private Book(Builder builder) {
        title = builder.title;
        author = builder.author;
        color = builder.color;
        pages = builder.pages;
        publicationDate = builder.publicationDate;
        weight = builder.weight;
    }
}